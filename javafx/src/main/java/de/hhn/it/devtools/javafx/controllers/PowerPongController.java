package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongListener;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import de.hhn.it.devtools.apis.powerPong.Score;
import de.hhn.it.devtools.components.powerPong.provider.PowerPongMatchEngine;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;

public class PowerPongController extends Controller implements PowerPongListener {

    private PowerPongService service;
    private final PlayerInput playerInput = new PlayerInput();
    private GameTimer gameTimer;

    @FXML
    private StackPane rootStack;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private VBox menuBox;
    @FXML
    private VBox gameOverBox;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label winnerLabel;
    @FXML
    private Button btnStart;

    private static final double GAME_WIDTH = 800.0;
    private static final double GAME_HEIGHT = 600.0;

    public PowerPongController() {
        // Initialize engine here as in Step 4
        this.service = new PowerPongMatchEngine();
    }

    @FXML
    public void initialize() {
        gameTimer = new GameTimer();
        // Removed dynamic resizing logic
    }

    @FXML
    public void onStartGame(ActionEvent event) {
        try {
            // Default to CLASSIC_DUEL
            service.startGame(GameMode.CLASSIC_DUEL);
            menuBox.setVisible(false);
            gameOverBox.setVisible(false);
            gameCanvas.setVisible(true);
            scoreLabel.setVisible(true);
            updateScoreFormat(0, 0);

            // Ensure focus for input
            if (menuBox.getScene() != null) {
                menuBox.getScene().getRoot().requestFocus();
            }

            service.removeListener(this);
            service.addListener(this);

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
        service.endGame();
    }

    @Override
    public void resume() {
        super.resume();
        if (menuBox != null && menuBox.getScene() != null) {
            Scene scene = menuBox.getScene();
            scene.setOnKeyPressed(this::handleKeyPressed);
            scene.setOnKeyReleased(this::handleKeyReleased);
        }
        // Safely start timer only if running? Or just rely on onStartGame
        // In Step 4 we might have just let it run or started it in onStartGame.
        // To be safe and avoid the crash user reported, I will NOT start the timer here
        // if the game isn't running. But to strictly revert, I'll rely on the guard in
        // GameTimer.
    }

    @Override
    public void pause() {
        super.pause();
        if (gameTimer != null)
            gameTimer.stop();
        if (menuBox != null && menuBox.getScene() != null) {
            Scene scene = menuBox.getScene();
            scene.setOnKeyPressed(null);
            scene.setOnKeyReleased(null);
        }
        if (service != null) {
            service.setPaused(true);
            service.removeListener(this);
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        InputAction action = mapKeyCode(event.getCode());
        if (action != null) {
            playerInput.keyPressed(action);
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        InputAction action = mapKeyCode(event.getCode());
        if (action != null) {
            playerInput.keyReleased(action);
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
                // GUARD: Only update if game is actually running
                if (service != null && service.getGameState() != null
                        && service.getGameState().status() == GameStatus.RUNNING) {
                    service.updateGame(playerInput);
                }

                // Always render if possible
                if (service != null && service.getGameState() != null) {
                    render(service.getGameState());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateScoreFormat(int p1, int p2) {
        scoreLabel.setText("P1: " + p1 + " - P2: " + p2);
    }

    private void render(GameState state) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // Clear logic for fixed size
        gc.clearRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // NO SCALING - Direct drawing

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

        List<de.hhn.it.devtools.apis.powerPong.PowerUpState> powerUps = state.activePowerUpsOnField();
        if (powerUps != null) {
            for (de.hhn.it.devtools.apis.powerPong.PowerUpState powerUp : powerUps) {
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
        gc.fillRoundRect(paddle.xPosition(), paddle.yPosition() - paddle.height() / 2, paddle.width(), paddle.height(),
                10, 10);
    }

    private void drawPowerUp(GraphicsContext gc, de.hhn.it.devtools.apis.powerPong.PowerUpState powerUp) {
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

    // --- Listeners ---
    @Override
    public void onPlayerScored(int scoringPlayerIndex, Score updatedScore) {
        Platform.runLater(() -> updateScoreFormat(updatedScore.player1(), updatedScore.player2()));
    }

    @Override
    public void onGameEnd(GameStatus finalStatus, GameState finalState) {
        Platform.runLater(() -> {
            gameTimer.stop();
            if (finalStatus == GameStatus.PLAYER_1_WINS) {
                winnerLabel.setText("PLAYER 1 WINS!");
                winnerLabel.setTextFill(Color.BLUE);
            } else {
                winnerLabel.setText("PLAYER 2 WINS!");
                winnerLabel.setTextFill(Color.RED);
            }
            gameOverBox.setVisible(true);
        });
    }

    @Override
    public void onBallCollision(GameState updatedState) {
    }

    @Override
    public void onPowerUpCollected(int idx, PowerUpType type) {
    }
}
