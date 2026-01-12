package de.hhn.it.devtools.javafx.powerpong.view;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PowerUpState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import de.hhn.it.devtools.javafx.powerpong.viewmodel.PowerPongViewModel;
import java.io.IOException;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PowerPongController extends StackPane {
  private final PowerPongViewModel viewModel;
  private GameTimer gameTimer;
  private GameMode lastSelectedMode = GameMode.CLASSIC_DUEL;
  private Stage fullscreenStage;
  private boolean isPaused = false;
  private VBox pauseOverlay;
  private VBox countdownOverlay;
  private Label countdownLabel;

  @FXML
  private Canvas gameCanvas;
  @FXML
  private VBox menuBox;
  @FXML
  private StackPane menuDecorations;
  @FXML
  private VBox gameOverBox;
  @FXML
  private Label scoreLabel;
  @FXML
  private Label winnerLabel;

  private static final double GAME_WIDTH = 800.0;
  private static final double GAME_HEIGHT = 600.0;

  public PowerPongController(final PowerPongViewModel viewModel) {
    this.viewModel = viewModel;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/powerpong/PowerPongControl.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  private void initialize() {
    gameTimer = new GameTimer();
    scoreLabel.textProperty().bind(viewModel.scoreTextProperty());
    winnerLabel.textProperty().bind(viewModel.winnerTextProperty());
    // Game over handling is now done in the fullscreen window (see startGame
    // method)
  }

  @FXML
  public void onStartClassic(ActionEvent event) {
    startGame(GameMode.CLASSIC_DUEL);
  }

  @FXML
  public void onStartPowerUp(ActionEvent event) {
    startGame(GameMode.POWERUP_DUEL);
  }

  @FXML
  public void onStartVsAi(ActionEvent event) {
    startGame(GameMode.PLAYER_VS_AI);
  }

  @FXML
  public void onStartSurvival(ActionEvent event) {
    startGame(GameMode.SURVIVAL);
  }

  @FXML
  public void onRestartGame(ActionEvent event) {
    startGame(lastSelectedMode);
  }

  private void startGame(GameMode mode) {
    try {
      lastSelectedMode = mode;
      isPaused = false;

      // Create fullscreen stage
      fullscreenStage = new Stage();
      fullscreenStage.setTitle("PowerPong - " + mode.name());

      // Create a new canvas for the fullscreen window
      Canvas fullscreenCanvas = new Canvas();

      // Create game over overlay for fullscreen
      VBox fullscreenGameOverBox = new VBox(20);
      fullscreenGameOverBox.setAlignment(javafx.geometry.Pos.CENTER);
      fullscreenGameOverBox.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 50;");
      fullscreenGameOverBox.setVisible(false);

      Label fullscreenWinnerLabel = new Label();
      fullscreenWinnerLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold;");

      javafx.scene.control.Button rematchBtn = new javafx.scene.control.Button("Rematch");
      rematchBtn.setStyle("-fx-font-size: 24; -fx-padding: 15 40;");
      rematchBtn.setOnAction(e -> {
        fullscreenGameOverBox.setVisible(false);
        startCountdownAndGame();
      });

      javafx.scene.control.Button menuBtn = new javafx.scene.control.Button("Zurück zum Menü");
      menuBtn.setStyle("-fx-font-size: 24; -fx-padding: 15 40;");
      menuBtn.setOnAction(e -> closeFullscreenAndReturnToMenu());

      fullscreenGameOverBox.getChildren().addAll(fullscreenWinnerLabel, rematchBtn, menuBtn);

      // Create pause overlay
      pauseOverlay = new VBox(20);
      pauseOverlay.setAlignment(javafx.geometry.Pos.CENTER);
      pauseOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 50;");
      pauseOverlay.setVisible(false);

      Label pauseLabel = new Label("PAUSIERT");
      pauseLabel.setStyle("-fx-font-size: 60; -fx-font-weight: bold; -fx-text-fill: white;");

      Label pauseHint = new Label("Drücke P zum Fortsetzen");
      pauseHint.setStyle("-fx-font-size: 24; -fx-text-fill: #aaaaaa;");

      javafx.scene.control.Button resumeBtn = new javafx.scene.control.Button("Fortsetzen");
      resumeBtn.setStyle("-fx-font-size: 24; -fx-padding: 15 40;");
      resumeBtn.setOnAction(e -> togglePause());

      javafx.scene.control.Button pauseMenuBtn = new javafx.scene.control.Button("Zurück zum Menü");
      pauseMenuBtn.setStyle("-fx-font-size: 20; -fx-padding: 10 30;");
      pauseMenuBtn.setOnAction(e -> closeFullscreenAndReturnToMenu());

      pauseOverlay.getChildren().addAll(pauseLabel, pauseHint, resumeBtn, pauseMenuBtn);

      // Create countdown overlay
      countdownOverlay = new VBox();
      countdownOverlay.setAlignment(javafx.geometry.Pos.CENTER);
      countdownOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
      countdownOverlay.setVisible(false);

      countdownLabel = new Label("3");
      countdownLabel.setStyle("-fx-font-size: 150; -fx-font-weight: bold; -fx-text-fill: white;");
      countdownOverlay.getChildren().add(countdownLabel);

      StackPane gameRoot = new StackPane(fullscreenCanvas, fullscreenGameOverBox, pauseOverlay, countdownOverlay);
      gameRoot.setStyle("-fx-background-color: black;");

      Scene fullscreenScene = new Scene(gameRoot);
      fullscreenStage.setScene(fullscreenScene);
      fullscreenStage.setFullScreen(true);
      fullscreenStage.setFullScreenExitHint(""); // Empty initially, avoid overlap with countdown

      // Bind canvas size to scene size
      fullscreenCanvas.widthProperty().bind(fullscreenScene.widthProperty());
      fullscreenCanvas.heightProperty().bind(fullscreenScene.heightProperty());

      // Store reference to use in render
      this.gameCanvas = fullscreenCanvas;

      // Handle keyboard input on fullscreen scene (including P for pause)
      fullscreenScene.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.P) {
          togglePause();
        } else {
          handleKeyPressed(event);
        }
      });
      fullscreenScene.setOnKeyReleased(this::handleKeyReleased);

      // Handle game over in fullscreen
      viewModel.gameStatusProperty().addListener((observable, oldStatus, newStatus) -> {
        if (newStatus == GameStatus.PLAYER_1_WINS || newStatus == GameStatus.PLAYER_2_WINS) {
          gameTimer.stop();
          if (newStatus == GameStatus.PLAYER_1_WINS) {
            fullscreenWinnerLabel.setText("SPIELER 1 GEWINNT!");
            fullscreenWinnerLabel.setTextFill(Color.web("#00f3ff")); // Neon Blue
          } else {
            fullscreenWinnerLabel.setText("SPIELER 2 GEWINNT!");
            fullscreenWinnerLabel.setTextFill(Color.web("#ff00ff")); // Neon Pink
          }
          fullscreenGameOverBox.setVisible(true);
        }
      });

      // Handle ESC / fullscreen exit
      fullscreenStage.fullScreenProperty().addListener((obs, wasFullScreen, isFullScreen) -> {
        if (!isFullScreen) {
          closeFullscreenAndReturnToMenu();
        }
      });

      fullscreenStage.setOnCloseRequest(e -> closeFullscreenAndReturnToMenu());

      fullscreenStage.show();
      fullscreenStage.requestFocus();

      // Start countdown before game
      startCountdownAndGame();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void startCountdownAndGame() {
    try {
      viewModel.startGame(lastSelectedMode);
    } catch (GameLogicException e) {
      e.printStackTrace();
      return;
    }

    // Render initial game state so players see the field behind countdown
    renderer.reset(); // Clear old trail/particles from previous game
    try {
      GameState state = viewModel.getGameState();
      if (state != null) {
        render(state);
      }
    } catch (Exception ex) {
      // Ignore
    }

    countdownOverlay.setVisible(true);
    countdownLabel.setText("3");

    javafx.animation.Timeline countdown = new javafx.animation.Timeline(
        new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0), e -> countdownLabel.setText("3")),
        new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> countdownLabel.setText("2")),
        new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2), e -> countdownLabel.setText("1")),
        new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
          countdownLabel.setText("GO!");
          countdownLabel.setStyle("-fx-font-size: 120; -fx-font-weight: bold; -fx-text-fill: #00ff00;");
        }),
        new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3.5), e -> {
          countdownOverlay.setVisible(false);
          countdownLabel.setStyle("-fx-font-size: 150; -fx-font-weight: bold; -fx-text-fill: white;");
          gameTimer.start();
        }));
    countdown.play();
  }

  private void togglePause() {
    if (viewModel.getGameState().status() == GameStatus.PLAYER_1_WINS ||
        viewModel.getGameState().status() == GameStatus.PLAYER_2_WINS) {
      return; // Don't pause if game is over
    }

    isPaused = !isPaused;
    pauseOverlay.setVisible(isPaused);

    if (isPaused) {
      gameTimer.stop();
    } else {
      gameTimer.start();
    }
  }

  private void closeFullscreenAndReturnToMenu() {
    gameTimer.stop();
    viewModel.endGame();
    if (fullscreenStage != null) {
      fullscreenStage.close();
      fullscreenStage = null;
    }
  }

  @FXML
  public void onExitGame(ActionEvent event) {
    System.exit(0);
  }

  @FXML
  public void onBackToMenu(ActionEvent event) {
    gameTimer.stop();
    gameOverBox.setVisible(false);
    menuBox.setVisible(true);
    if (menuDecorations != null) {
      menuDecorations.setVisible(true);
    }
    scoreLabel.setVisible(false);
    viewModel.endGame();
  }

  public void resume() {
    if (menuBox != null && menuBox.getScene() != null) {
      Scene scene = menuBox.getScene();
      scene.setOnKeyPressed(this::handleKeyPressed);
      scene.setOnKeyReleased(this::handleKeyReleased);
    }
  }

  public void pause() {
    if (gameTimer != null) {
      gameTimer.stop();
    }
    if (menuBox != null && menuBox.getScene() != null) {
      Scene scene = menuBox.getScene();
      scene.setOnKeyPressed(null);
      scene.setOnKeyReleased(null);
    }
    viewModel.pause();
  }

  public void shutdown() {
    viewModel.endGame();
  }

  private void handleKeyPressed(KeyEvent event) {
    InputAction action = mapKeyCode(event.getCode());
    if (action != null) {
      viewModel.keyPressed(action);
    }
  }

  private void handleKeyReleased(KeyEvent event) {
    InputAction action = mapKeyCode(event.getCode());
    if (action != null) {
      viewModel.keyReleased(action);
    }
  }

  private InputAction mapKeyCode(KeyCode code) {
    return switch (code) {
      case W -> InputAction.LEFT_UP;
      case S -> InputAction.LEFT_DOWN;
      case UP -> InputAction.RIGHT_UP;
      case DOWN -> InputAction.RIGHT_DOWN;
      default -> null;
    };
  }

  private final GameRenderer renderer = new GameRenderer();

  private class GameTimer extends AnimationTimer {
    @Override
    public void handle(long now) {
      try {
        GameState state = viewModel.updateGame();
        if (state != null) {
          render(state);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void render(GameState state) {
    GraphicsContext gc = gameCanvas.getGraphicsContext2D();
    renderer.render(gc, state);
  }
}