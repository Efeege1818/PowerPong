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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;

public class PowerPongController extends Controller implements PowerPongListener {

    private PowerPongService service;
    private final PlayerInput playerInput = new PlayerInput();
    private GameTimer gameTimer;

    @FXML
    private javafx.scene.layout.StackPane rootStack;
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

    // Buttons for referencing if needed, otherwise handled by onAction
    @FXML
    private Button btnClassic;
    @FXML
    private Button btnPowerUp;
    @FXML
    private Button btnAI;

    private static final double GAME_WIDTH = 800.0;
    private static final double GAME_HEIGHT = 600.0;

    public PowerPongController() {
        // Empty constructor, defer heavy init to initialize
    }

    @FXML
    public void initialize() {
        try {
            this.service = new PowerPongMatchEngine();
            this.gameTimer = new GameTimer();

            // Dynamic Resizing: Bind Canvas to StackPane
            if (rootStack != null && gameCanvas != null) {
                gameCanvas.widthProperty().bind(rootStack.widthProperty());
                gameCanvas.heightProperty().bind(rootStack.heightProperty());

                // Redraw on resize
                gameCanvas.widthProperty().addListener(evt -> safeRender());
                gameCanvas.heightProperty().addListener(evt -> safeRender());
            } else {
                System.err.println("CRITICAL: rootStack or gameCanvas failed to inject!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CRITICAL ERROR during PowerPongController initialization: " + e.getMessage());
        }
    }

    // Helper to avoid duplicate guarded logic
    private void safeRender() {
        try {
            if (service != null) {
                GameState state = service.getGameState();
                // Render if state exists and NOT running (timer handles running state)
                if (state != null && state.status() != GameStatus.RUNNING) {
                    render(state);
                }
            }
        } catch (Exception e) {
            // Ignore render errors during resize to prevent crash loop
        }
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
    public void onStartAI(ActionEvent event) {
        startGame(GameMode.PLAYER_VS_AI);
    }

    private void startGame(GameMode mode) {
        try {
            service.startGame(mode);
            menuBox.setVisible(false);
            gameOverBox.setVisible(false);
            gameCanvas.setVisible(true);
            scoreLabel.setVisible(true);
            updateScoreFormat(0, 0); // Reset score label
            gameCanvas.requestFocus();

            service.removeListener(this);
            service.addListener(this);

            gameTimer.start();
        } catch (GameLogicException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onBackToMenu(ActionEvent event) {
        gameOverBox.setVisible(false);
        menuBox.setVisible(true);
        scoreLabel.setVisible(false);
        service.endGame();
    }

    @Override
    public void resume() {
        super.resume();
        Scene scene = menuBox.getScene(); // menuBox still good anchor
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPressed);
            scene.setOnKeyReleased(this::handleKeyReleased);
        }
        gameTimer.start();
        service.addListener(this);
    }

    @Override
    public void pause() {
        super.pause();
        gameTimer.stop();
        Scene scene = menuBox.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(null);
            scene.setOnKeyReleased(null);
        }
        service.setPaused(true);
        service.removeListener(this);
    }

    // --- PowerPongListener Implementation ---

    @Override
    public void onBallCollision(GameState updatedState) {
        // Optional: Play sound or particle effect
    }

    @Override
    public void onPlayerScored(int scoringPlayerIndex, Score updatedScore) {
        Platform.runLater(() -> {
            updateScoreFormat(updatedScore.player1(), updatedScore.player2());
        });
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
    public void onPowerUpCollected(int collectingPlayerIndex, PowerUpType powerUpType) throws GameLogicException {
        // Handle PowerUp visual feedback if needed
    }

    // --- Input & Loop ---

    private void handleKeyPressed(KeyEvent event) {
        playerInput.keyPressed(mapKeyCode(event.getCode()));
    }

    private void handleKeyReleased(KeyEvent event) {
        playerInput.keyReleased(mapKeyCode(event.getCode()));
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
                service.updateGame(playerInput);
                render(service.getGameState());
            } catch (GameLogicException e) {
                e.printStackTrace();
                // If game ended abruptly
            }
        }
    }

    private void updateScoreFormat(int p1, int p2) {
        scoreLabel.setText("P1: " + p1 + " - P2: " + p2);
    }

    private void render(GameState state) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        double canvasWidth = gameCanvas.getWidth();
        double canvasHeight = gameCanvas.getHeight();

        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Dynamic Scaling
        double scaleX = canvasWidth / GAME_WIDTH;
        double scaleY = canvasHeight / GAME_HEIGHT;

        gc.save();
        gc.scale(scaleX, scaleY);

        drawFieldDecorations(gc);

        // Paddles
        if (state.player1Paddle() != null) {
            drawPaddle(gc, state.player1Paddle(), Color.BLUE);
        }
        if (state.player2Paddle() != null) {
            drawPaddle(gc, state.player2Paddle(), Color.RED);
        }

        // Balls
        gc.setFill(Color.WHITE);
        List<BallState> balls = state.balls();
        if (balls != null) {
            for (BallState ball : balls) {
                double r = ball.radius();
                double d = r * 2;
                gc.fillOval(ball.xPosition() - r, ball.yPosition() - r, d, d);
            }
        }

        // PowerUps
        List<de.hhn.it.devtools.apis.powerPong.PowerUpState> powerUps = state.activePowerUpsOnField();
        if (powerUps != null) {
            for (de.hhn.it.devtools.apis.powerPong.PowerUpState powerUp : powerUps) {
                drawPowerUp(gc, powerUp);
            }
        }

        gc.restore();
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
        // Rounded corners for polish
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
            case DOUBLE_BALL -> Color.WHITE; // Maybe a different shade or border
            case SHIELD -> Color.LIGHTBLUE;
            case BARRIERLESS -> Color.CYAN;
            case SLOW_ENEMY_PADDLE -> Color.YELLOW;
            case FASTER_BALL_ENEMY_SIDE -> Color.ORANGE;
            default -> Color.MAGENTA;
        };
    }
}
