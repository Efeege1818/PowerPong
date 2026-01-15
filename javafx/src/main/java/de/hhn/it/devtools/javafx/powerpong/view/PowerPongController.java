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
  private double aiDifficulty = 1.0; // AI difficulty multiplier (0.6=easy, 1.0=medium, 1.5=hard)

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
    aiDifficulty = 0.5; // Default medium
    startGame(GameMode.PLAYER_VS_AI);
  }

  @FXML
  public void onStartVsAiEasy(ActionEvent event) {
    aiDifficulty = 0.0; // Easy: slow reaction, big errors
    startGame(GameMode.PLAYER_VS_AI);
  }

  @FXML
  public void onStartVsAiMedium(ActionEvent event) {
    aiDifficulty = 0.5; // Medium: balanced
    startGame(GameMode.PLAYER_VS_AI);
  }

  @FXML
  public void onStartVsAiHard(ActionEvent event) {
    aiDifficulty = 1.0; // Hard: fast, precise
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

      // Create game over overlay for fullscreen (neon styled)
      VBox fullscreenGameOverBox = new VBox(25);
      fullscreenGameOverBox.setAlignment(javafx.geometry.Pos.CENTER);
      fullscreenGameOverBox.setStyle(
          "-fx-background-color: rgba(5, 5, 20, 0.9); -fx-padding: 60; -fx-background-radius: 30; -fx-border-radius: 30; -fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 2;");
      fullscreenGameOverBox.setMaxWidth(500);
      fullscreenGameOverBox.setMaxHeight(350);
      fullscreenGameOverBox.setVisible(false);

      Label fullscreenWinnerLabel = new Label();
      fullscreenWinnerLabel.setStyle("-fx-font-size: 52; -fx-font-weight: bold;");
      fullscreenWinnerLabel.setEffect(new javafx.scene.effect.DropShadow(25, Color.WHITE));

      javafx.scene.control.Button rematchBtn = new javafx.scene.control.Button("🔄 REMATCH");
      rematchBtn.setStyle(
          "-fx-background-color: linear-gradient(to bottom, rgba(0, 243, 255, 0.4), rgba(0, 150, 200, 0.2)); -fx-text-fill: #00f3ff; -fx-font-size: 22; -fx-font-weight: bold; -fx-padding: 15 50; -fx-background-radius: 25; -fx-border-color: #00f3ff; -fx-border-radius: 25; -fx-border-width: 2; -fx-cursor: hand;");
      rematchBtn.setEffect(new javafx.scene.effect.DropShadow(15, Color.web("#00f3ff")));
      rematchBtn.setOnAction(e -> {
        fullscreenGameOverBox.setVisible(false);
        startCountdownAndGame();
      });

      javafx.scene.control.Button menuBtn = new javafx.scene.control.Button("🏠 MENÜ");
      menuBtn.setStyle(
          "-fx-background-color: linear-gradient(to bottom, rgba(255, 100, 100, 0.3), rgba(200, 50, 50, 0.15)); -fx-text-fill: #ff6464; -fx-font-size: 18; -fx-font-weight: bold; -fx-padding: 12 40; -fx-background-radius: 20; -fx-border-color: #ff6464; -fx-border-radius: 20; -fx-border-width: 1.5; -fx-cursor: hand;");
      menuBtn.setOnAction(e -> closeFullscreenAndReturnToMenu());

      fullscreenGameOverBox.getChildren().addAll(fullscreenWinnerLabel, rematchBtn, menuBtn);

      // Create pause overlay (neon styled)
      pauseOverlay = new VBox(25);
      pauseOverlay.setAlignment(javafx.geometry.Pos.CENTER);
      pauseOverlay.setStyle(
          "-fx-background-color: rgba(5, 5, 20, 0.85); -fx-padding: 50; -fx-background-radius: 25; -fx-border-radius: 25; -fx-border-color: rgba(255,255,0,0.3); -fx-border-width: 2;");
      pauseOverlay.setMaxWidth(450);
      pauseOverlay.setMaxHeight(320);
      pauseOverlay.setVisible(false);

      Label pauseLabel = new Label("⏸ PAUSIERT");
      pauseLabel.setStyle("-fx-font-size: 52; -fx-font-weight: bold; -fx-text-fill: #ffdc00;");
      pauseLabel.setEffect(new javafx.scene.effect.DropShadow(20, Color.web("#ffdc00")));

      Label pauseHint = new Label("Drücke P zum Fortsetzen");
      pauseHint.setStyle("-fx-font-size: 18; -fx-text-fill: #888888;");

      javafx.scene.control.Button resumeBtn = new javafx.scene.control.Button("▶ FORTSETZEN");
      resumeBtn.setStyle(
          "-fx-background-color: linear-gradient(to bottom, rgba(100, 255, 100, 0.3), rgba(50, 200, 50, 0.15)); -fx-text-fill: #7fff7f; -fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 12 45; -fx-background-radius: 22; -fx-border-color: #7fff7f; -fx-border-radius: 22; -fx-border-width: 2; -fx-cursor: hand;");
      resumeBtn.setEffect(new javafx.scene.effect.DropShadow(12, Color.web("#7fff7f")));
      resumeBtn.setOnAction(e -> togglePause());

      javafx.scene.control.Button pauseMenuBtn = new javafx.scene.control.Button("🏠 MENÜ");
      pauseMenuBtn.setStyle(
          "-fx-background-color: linear-gradient(to bottom, rgba(255, 100, 100, 0.25), rgba(200, 50, 50, 0.1)); -fx-text-fill: #ff6464; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 10 35; -fx-background-radius: 18; -fx-border-color: #ff6464; -fx-border-radius: 18; -fx-border-width: 1.5; -fx-cursor: hand;");
      pauseMenuBtn.setOnAction(e -> closeFullscreenAndReturnToMenu());

      pauseOverlay.getChildren().addAll(pauseLabel, pauseHint, resumeBtn, pauseMenuBtn);

      // Create countdown overlay
      countdownOverlay = new VBox();
      countdownOverlay.setAlignment(javafx.geometry.Pos.CENTER);
      countdownOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
      countdownOverlay.setVisible(false);

      countdownLabel = new Label("3");
      countdownLabel.setStyle("-fx-font-size: 150; -fx-font-weight: bold; -fx-text-fill: white;");
      countdownLabel.setEffect(new javafx.scene.effect.DropShadow(30, Color.web("#00f3ff")));
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
            fullscreenWinnerLabel.setText("DU GEWINNST!");
            fullscreenWinnerLabel.setTextFill(Color.web("#00f3ff")); // Neon Blue
          } else {
            // Check if we're in AI mode
            if (lastSelectedMode == GameMode.PLAYER_VS_AI || lastSelectedMode == GameMode.SURVIVAL) {
              fullscreenWinnerLabel.setText("KI GEWINNT!");
            } else {
              fullscreenWinnerLabel.setText("SPIELER 2 GEWINNT!");
            }
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
      // Set AI difficulty based on selected level (0=easy, 0.5=medium, 1.0=hard)
      if (lastSelectedMode == GameMode.PLAYER_VS_AI) {
        viewModel.setAiDifficulty(aiDifficulty);
      }
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
    // Don't allow pause during countdown
    if (countdownOverlay != null && countdownOverlay.isVisible()) {
      return;
    }

    if (viewModel.getGameState() == null ||
        viewModel.getGameState().status() == GameStatus.PLAYER_1_WINS ||
        viewModel.getGameState().status() == GameStatus.PLAYER_2_WINS) {
      return; // Don't pause if game is over or not started
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