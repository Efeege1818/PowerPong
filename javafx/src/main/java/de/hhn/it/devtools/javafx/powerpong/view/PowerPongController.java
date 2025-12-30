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

public class PowerPongController extends StackPane {
  private final PowerPongViewModel viewModel;
  private GameTimer gameTimer;
  private GameMode lastSelectedMode = GameMode.CLASSIC_DUEL;

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
    viewModel.gameStatusProperty().addListener((observable, oldStatus, newStatus) -> {
      if (newStatus == GameStatus.PLAYER_1_WINS || newStatus == GameStatus.PLAYER_2_WINS) {
        gameTimer.stop();
        if (newStatus == GameStatus.PLAYER_1_WINS) {
          winnerLabel.setTextFill(Color.BLUE);
        } else {
          winnerLabel.setTextFill(Color.RED);
        }
        gameOverBox.setVisible(true);
      }
    });
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
      viewModel.startGame(mode);
      menuBox.setVisible(false);
      if (menuDecorations != null) {
        menuDecorations.setVisible(false);
      }
      gameOverBox.setVisible(false);
      gameCanvas.setVisible(true);
      // Score is now rendered on canvas
      scoreLabel.setVisible(false);

      if (menuBox.getScene() != null) {
        menuBox.getScene().getRoot().requestFocus();
      }

      gameTimer.start();
    } catch (GameLogicException e) {
      e.printStackTrace();
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