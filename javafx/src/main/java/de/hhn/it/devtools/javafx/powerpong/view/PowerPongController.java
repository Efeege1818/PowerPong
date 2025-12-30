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
  private VBox gameOverBox;
  @FXML
  private VBox powerUpLegend;
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
      gameOverBox.setVisible(false);
      gameCanvas.setVisible(true);
      scoreLabel.setVisible(true);
      if (powerUpLegend != null) {
        powerUpLegend.setVisible(mode == GameMode.POWERUP_DUEL);
      }

      if (menuBox.getScene() != null) {
        menuBox.getScene().getRoot().requestFocus();
      }

      gameTimer.start();
    } catch (GameLogicException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void onBackToMenu(ActionEvent event) {
    gameTimer.stop();
    gameOverBox.setVisible(false);
    menuBox.setVisible(true);
    scoreLabel.setVisible(false);
    if (powerUpLegend != null) {
      powerUpLegend.setVisible(false);
    }
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

    gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

    drawFieldDecorations(gc);

    if (state.player1Paddle() != null) {
      drawPaddle(gc, state.player1Paddle(), Color.BLUE);
    }
    if (state.player2Paddle() != null) {
      drawPaddle(gc, state.player2Paddle(), Color.RED);
    }

    gc.setFill(Color.WHITE);
    List<BallState> balls = state.balls();
    if (balls != null) {
      for (BallState ball : balls) {
        double r = ball.radius();
        double d = r * 2;
        gc.fillOval(ball.xPosition() - r, ball.yPosition() - r, d, d);
      }
    }

    List<PowerUpState> powerUps = state.activePowerUpsOnField();
    if (powerUps != null) {
      for (PowerUpState powerUp : powerUps) {
        drawPowerUp(gc, powerUp);
      }
    }
  }

  private void drawFieldDecorations(GraphicsContext gc) {
    gc.setStroke(Color.GRAY);
    gc.setLineWidth(2);
    gc.setLineDashes(10);
    gc.strokeLine(GAME_WIDTH / 2, 0, GAME_WIDTH / 2, GAME_HEIGHT);
    gc.setLineDashes(null);
  }

  private void drawPaddle(GraphicsContext gc, PaddleState paddle, Color color) {
    gc.setFill(color);
    gc.fillRoundRect(paddle.xPosition(), paddle.yPosition() - paddle.height() / 2,
        paddle.width(), paddle.height(), 10, 10);
  }

  private void drawPowerUp(GraphicsContext gc, PowerUpState powerUp) {
    gc.setFill(getColorForPowerUp(powerUp.type()));
    double r = powerUp.radius();
    double d = r * 2;
    gc.fillOval(powerUp.xPosition() - r, powerUp.yPosition() - r, d, d);
  }

  private Color getColorForPowerUp(PowerUpType type) {
    return switch (type) {
      case BIGGER_PADDLE -> Color.LIGHTGREEN;
      case SMALLER_ENEMY_PADDLE -> Color.INDIANRED;
      case DOUBLE_BALL -> Color.WHITE;
      case SHIELD -> Color.LIGHTBLUE;
      case BARRIERLESS -> Color.CYAN;
      case SLOW_ENEMY_PADDLE -> Color.YELLOW;
      case FASTER_BALL_ENEMY_SIDE -> Color.ORANGE;
      default -> Color.MAGENTA;
    };
  }
}