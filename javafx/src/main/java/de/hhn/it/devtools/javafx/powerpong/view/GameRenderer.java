package de.hhn.it.devtools.javafx.powerpong.view;

import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PowerUpState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Handles the rendering of the PowerPong game with a Neon/Cyberpunk aesthetic.
 */
public class GameRenderer {

    private static final double GAME_WIDTH = 800.0;
    private static final double GAME_HEIGHT = 600.0;

    // Neon Colors
    private static final Color NEON_BLUE = Color.web("#00f3ff");
    private static final Color NEON_PINK = Color.web("#ff00ff");
    private static final Color NEON_GREEN = Color.web("#0aff0a");
    private static final Color NEON_RED = Color.web("#ff0a0a");
    private static final Color NEON_YELLOW = Color.web("#ffff00");
    private static final Color BACKGROUND_COLOR = Color.web("#050510"); // Very dark blue/black

    public void render(GraphicsContext gc, GameState state) {
        // Clear background
        gc.setEffect(null);
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Draw Field Decorations (Line, Scores)
        drawFieldDecorations(gc, state);

        // Enable global glow effect for game elements if performance allows
        // Note: Applying effect to every single draw call is expensive.
        // Better to draw everything that glows, then apply effect?
        // Or simpler: simulate glow with transparent layers.

        // Let's us simple strokes + shadow/glow for Neon look

        if (state.player1Paddle() != null) {
            drawNeonPaddle(gc, state.player1Paddle(), NEON_BLUE);
        }
        if (state.player2Paddle() != null) {
            drawNeonPaddle(gc, state.player2Paddle(), NEON_PINK);
        }

        List<BallState> balls = state.balls();
        if (balls != null) {
            for (BallState ball : balls) {
                drawNeonBall(gc, ball);
            }
        }

        List<PowerUpState> powerUps = state.activePowerUpsOnField();
        if (powerUps != null) {
            for (PowerUpState powerUp : powerUps) {
                drawNeonPowerUp(gc, powerUp);
            }
        }
    }

    private void drawFieldDecorations(GraphicsContext gc, GameState state) {
        // 1. Draw Center Line
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.setLineDashes(15, 15); // Dashed pattern
        gc.strokeLine(GAME_WIDTH / 2, 0, GAME_WIDTH / 2, GAME_HEIGHT);
        gc.setLineDashes(null);

        // 2. Draw Background Scores
        gc.setFill(Color.web("#222222")); // Subtle dark grey for background score
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 150));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);

        // Player 1 Score (Left Center)
        gc.fillText(String.valueOf(state.score().player1()), GAME_WIDTH / 4, GAME_HEIGHT / 2);

        // Player 2 Score (Right Center)
        gc.fillText(String.valueOf(state.score().player2()), GAME_WIDTH * 3 / 4, GAME_HEIGHT / 2);
    }

    private void drawNeonPaddle(GraphicsContext gc, PaddleState paddle, Color color) {
        double x = paddle.xPosition();
        double y = paddle.yPosition() - paddle.height() / 2;
        double w = paddle.width();
        double h = paddle.height();

        // Glow
        gc.setEffect(new javafx.scene.effect.DropShadow(20, color));

        gc.setFill(color);
        gc.fillRoundRect(x, y, w, h, 10, 10);

        // Inner bright core
        gc.setEffect(null);
        gc.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.8));
        gc.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 8, 8);
    }

    private void drawNeonBall(GraphicsContext gc, BallState ball) {
        double r = ball.radius();
        double d = r * 2;
        double x = ball.xPosition() - r;
        double y = ball.yPosition() - r;

        // Glow trail or intense glow
        gc.setEffect(new javafx.scene.effect.DropShadow(15, Color.WHITE));

        gc.setFill(Color.WHITE);
        gc.fillOval(x, y, d, d);

        gc.setEffect(null);
    }

    private void drawNeonPowerUp(GraphicsContext gc, PowerUpState powerUp) {
        Color color = getColorForPowerUp(powerUp.type());
        double r = powerUp.radius();
        double d = r * 2;
        double x = powerUp.xPosition() - r;
        double y = powerUp.yPosition() - r;
        double cx = powerUp.xPosition();
        double cy = powerUp.yPosition();

        // Glow
        gc.setEffect(new javafx.scene.effect.DropShadow(15, color));

        // Outer Ring
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, d, d);

        // Inner Fill (transparent)
        gc.setFill(color.deriveColor(0, 1, 1, 0.2));
        gc.fillOval(x, y, d, d);

        // Draw Icon
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.setEffect(null); // Clear glow for icon sharpness (optional, or keep it)

        switch (powerUp.type()) {
            case BIGGER_PADDLE:
                // Up Arrow / Plus
                gc.strokeLine(cx, cy - r / 2, cx, cy + r / 2);
                gc.strokeLine(cx - r / 2, cy, cx + r / 2, cy);
                break;
            case SMALLER_ENEMY_PADDLE:
                // Down Arrow / Minus
                gc.strokeLine(cx - r / 2, cy, cx + r / 2, cy);
                break;
            case DOUBLE_BALL:
                // Two dots
                gc.setFill(Color.WHITE);
                gc.fillOval(cx - 5, cy - 5, 4, 4);
                gc.fillOval(cx + 1, cy + 1, 4, 4);
                break;
            case SHIELD:
                // Shield / Square
                gc.strokeRect(cx - r / 2, cy - r / 2, r, r);
                break;
            case BARRIERLESS:
                // Broken line / Slash
                gc.strokeLine(cx - r / 2, cy + r / 2, cx + r / 2, cy - r / 2);
                break;
            case SLOW_ENEMY_PADDLE:
                // Snail / Slow (<<)
                gc.strokeLine(cx + 2, cy - 4, cx - 2, cy);
                gc.strokeLine(cx + 2, cy + 4, cx - 2, cy);
                gc.strokeLine(cx - 2, cy - 4, cx - 6, cy);
                gc.strokeLine(cx - 2, cy + 4, cx - 6, cy);
                break;
            case FASTER_BALL_ENEMY_SIDE:
                // Fast (>>)
                gc.strokeLine(cx - 2, cy - 4, cx + 2, cy);
                gc.strokeLine(cx - 2, cy + 4, cx + 2, cy);
                gc.strokeLine(cx + 2, cy - 4, cx + 6, cy);
                gc.strokeLine(cx + 2, cy + 4, cx + 6, cy);
                break;
        }

        gc.setEffect(null);
    }

    private Color getColorForPowerUp(PowerUpType type) {
        return switch (type) {
            case BIGGER_PADDLE -> NEON_GREEN;
            case SMALLER_ENEMY_PADDLE -> NEON_RED;
            case DOUBLE_BALL -> Color.WHITE;
            case SHIELD -> NEON_BLUE;
            case BARRIERLESS -> Color.CYAN;
            case SLOW_ENEMY_PADDLE -> NEON_YELLOW;
            case FASTER_BALL_ENEMY_SIDE -> Color.ORANGE;
            default -> Color.MAGENTA;
        };
    }
}
