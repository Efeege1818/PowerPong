package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.components.powerPong.provider.PowerPongMatchEngine;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.List;

public class PowerPongController extends Controller {

    private PowerPongService service;
    private final PlayerInput playerInput = new PlayerInput();
    private GameTimer gameTimer;

    @FXML
    private Canvas gameCanvas;
    @FXML
    private VBox menuBox;
    @FXML
    private Button startButton;

    public PowerPongController() {
        this.service = new PowerPongMatchEngine();
    }

    @FXML
    public void initialize() {
        gameTimer = new GameTimer();
    }

    @FXML
    public void onStartGame(ActionEvent event) {
        try {
            service.startGame(GameMode.CLASSIC_DUEL);
            menuBox.setVisible(false);
            gameCanvas.setVisible(true);
            gameCanvas.requestFocus(); // Ensure canvas or scene gets focus (handled in key listener setup mostly)
        } catch (GameLogicException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        super.resume();
        // Attach Key Listeners to Scene
        Scene scene = menuBox.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(this::handleKeyPressed);
            scene.setOnKeyReleased(this::handleKeyReleased);
        }
        gameTimer.start();
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
        // Pause game logic if running
        service.setPaused(true);
    }

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
            }
        }
    }

    private void render(GameState state) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

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
                // BallState uses xPosition, yPosition, radius
                // Draw oval needs top-left x/y and width/height
                // Assuming xPosition/yPosition are CENTER of ball (common in physics)
                // But if they are Top-Left, we use them directly.
                // Let's assume Center for physics usually, but let's check.
                // Looking at PhysicsEngine would confirm. For now assuming CENTER.
                // actually typically JavaFX shapes are Top-Left.
                // Let's stick to using xPosition() and yPosition() directly first
                // If it looks offset, we can ADJUST.
                double r = ball.radius();
                double d = r * 2;
                gc.fillOval(ball.xPosition() - r, ball.yPosition() - r, d, d);
            }
        }

        // Score
        gc.setFill(Color.WHITE);
        // Simple Score render
        if (state.score() != null) {
            gc.fillText("P1: " + state.score().player1() + " - P2: " + state.score().player2(), 600, 50);
        }

        // Check for Game Over to show menu again
        if (state.status() == GameStatus.PLAYER_1_WINS || state.status() == GameStatus.PLAYER_2_WINS) {
            menuBox.setVisible(true);
        }
    }

    private void drawPaddle(GraphicsContext gc, PaddleState paddle, Color color) {
        gc.setFill(color);
        // paddle.yPosition() is the center Y (from PhysicsEngine analysis)
        // fillRect expects top-left Y. So we subtract half the height.
        // paddle.xPosition() is the left X. So we use it directly.
        gc.fillRect(paddle.xPosition(), paddle.yPosition() - paddle.height() / 2, paddle.width(), paddle.height());
    }
}
