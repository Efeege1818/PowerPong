package de.hhn.it.devtools.javafx.powerpong.view;

import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PowerUpState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import java.util.ArrayList;
import java.util.Iterator;
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

    // Logical game dimensions (used for physics/game logic)
    private static final double LOGICAL_WIDTH = 800.0;
    private static final double LOGICAL_HEIGHT = 600.0;

    // Neon Colors
    private static final Color NEON_BLUE = Color.web("#00f3ff");
    private static final Color NEON_PINK = Color.web("#ff00ff");
    private static final Color NEON_GREEN = Color.web("#0aff0a");
    private static final Color NEON_RED = Color.web("#ff0a0a");
    private static final Color NEON_YELLOW = Color.web("#ffff00");
    private static final Color BACKGROUND_COLOR = Color.web("#050510"); // Very dark blue/black

    // Ball trail history
    private static final int TRAIL_LENGTH = 8;
    private final List<double[]> ballTrailHistory = new ArrayList<>();

    // Particle system
    private final List<Particle> particles = new ArrayList<>();
    private double lastBallX = -1;
    private double lastBallY = -1;

    private static class Particle {
        double x, y, vx, vy;
        double life, maxLife;
        Color color;

        Particle(double x, double y, double vx, double vy, double life, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.life = life;
            this.maxLife = life;
            this.color = color;
        }
    }

    /**
     * Clears all visual effects (trail, particles) for a fresh game start.
     */
    public void reset() {
        ballTrailHistory.clear();
        particles.clear();
        lastBallX = -1;
        lastBallY = -1;
    }

    public void render(GraphicsContext gc, GameState state) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        // Stretch to fill entire screen (no aspect ratio preservation)
        double scaleX = canvasWidth / LOGICAL_WIDTH;
        double scaleY = canvasHeight / LOGICAL_HEIGHT;

        // Clear entire canvas with background
        gc.setEffect(null);
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        // Apply transformation for scaling (stretch to fill)
        gc.save();
        gc.scale(scaleX, scaleY);

        // Draw Field Decorations (Line, Scores)
        drawFieldDecorations(gc, state);

        if (state.player1Paddle() != null) {
            drawNeonPaddle(gc, state.player1Paddle(), NEON_BLUE);
        }
        if (state.player2Paddle() != null) {
            drawNeonPaddle(gc, state.player2Paddle(), NEON_PINK);
        }

        // Update and draw particles
        updateAndDrawParticles(gc);

        List<BallState> balls = state.balls();
        if (balls != null) {
            for (BallState ball : balls) {
                // Draw ball trail
                drawBallTrail(gc, ball);
                // Draw actual ball
                drawNeonBall(gc, ball, state);
            }
        }

        List<PowerUpState> powerUps = state.activePowerUpsOnField();
        if (powerUps != null) {
            for (PowerUpState powerUp : powerUps) {
                drawNeonPowerUp(gc, powerUp);
            }
        }

        // Restore original transformation
        gc.restore();
    }

    private void updateAndDrawParticles(GraphicsContext gc) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.x += p.vx;
            p.y += p.vy;
            p.life -= 0.05; // Faster fade for fewer frames

            if (p.life <= 0) {
                iterator.remove();
            } else {
                double alpha = p.life / p.maxLife;
                // No DropShadow effect - just simple colored circle for performance
                gc.setFill(p.color.deriveColor(0, 1, 1, alpha));
                double size = 6 * alpha;
                gc.fillOval(p.x - size / 2, p.y - size / 2, size, size);
            }
        }
    }

    private void drawBallTrail(GraphicsContext gc, BallState ball) {
        // Add current position to trail
        ballTrailHistory.add(new double[] { ball.xPosition(), ball.yPosition() });
        while (ballTrailHistory.size() > TRAIL_LENGTH) {
            ballTrailHistory.remove(0);
        }

        // Draw trail
        for (int i = 0; i < ballTrailHistory.size() - 1; i++) {
            double[] pos = ballTrailHistory.get(i);
            double alpha = (double) (i + 1) / ballTrailHistory.size() * 0.5;
            double size = ball.radius() * 2 * alpha;

            gc.setFill(Color.WHITE.deriveColor(0, 1, 1, alpha));
            gc.fillOval(pos[0] - size / 2, pos[1] - size / 2, size, size);
        }
    }

    private void spawnCollisionParticles(double x, double y, Color color) {
        for (int i = 0; i < 5; i++) { // Reduced from 8 for performance
            double angle = Math.random() * Math.PI * 2;
            double speed = 3 + Math.random() * 3;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            particles.add(new Particle(x, y, vx, vy, 0.6, color)); // Shorter life
        }
    }

    private void drawFieldDecorations(GraphicsContext gc, GameState state) {
        // 1. Draw Center Line
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.setLineDashes(15, 15); // Dashed pattern
        gc.strokeLine(LOGICAL_WIDTH / 2, 0, LOGICAL_WIDTH / 2, LOGICAL_HEIGHT);
        gc.setLineDashes(null);

        // 2. Draw Background Scores
        gc.setFill(Color.web("#222222")); // Subtle dark grey for background score
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 150));
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);

        // Player 1 Score (Left Center)
        gc.fillText(String.valueOf(state.score().player1()), LOGICAL_WIDTH / 4, LOGICAL_HEIGHT / 2);

        // Player 2 Score (Right Center)
        gc.fillText(String.valueOf(state.score().player2()), LOGICAL_WIDTH * 3 / 4, LOGICAL_HEIGHT / 2);
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

    private void drawNeonBall(GraphicsContext gc, BallState ball, GameState state) {
        double r = ball.radius();
        double d = r * 2;
        double x = ball.xPosition() - r;
        double y = ball.yPosition() - r;

        // Detect paddle collision and spawn particles
        if (lastBallX >= 0) {
            // Check if ball direction changed (collision with paddle)
            double ballX = ball.xPosition();
            PaddleState leftPaddle = state.player1Paddle();
            PaddleState rightPaddle = state.player2Paddle();

            // Left paddle collision (ball moving right after being on left side)
            if (leftPaddle != null && lastBallX < 100 && ballX > lastBallX && ballX < 100) {
                spawnCollisionParticles(ballX, ball.yPosition(), NEON_BLUE);
            }
            // Right paddle collision (ball moving left after being on right side)
            if (rightPaddle != null && lastBallX > 700 && ballX < lastBallX && ballX > 700) {
                spawnCollisionParticles(ballX, ball.yPosition(), NEON_PINK);
            }
        }
        lastBallX = ball.xPosition();
        lastBallY = ball.yPosition();

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
