package de.hhn.it.devtools.javafx.powerpong.view;

import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PowerUpState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
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

    // Ball trail history (Ring Buffer - Zero Allocation)
    private static final int TRAIL_LENGTH = 8;
    private final double[][] ballTrailBuffer = new double[TRAIL_LENGTH][2];
    private int trailHead = 0;
    private int trailSize = 0;

    // Particle System (Object Pool) - Optimized for zero GC
    private static final int MAX_PARTICLES = 100;
    private final Particle[] particlePool = new Particle[MAX_PARTICLES];
    private int activeParticleCount = 0;

    private double lastBallX = -1;
    private double lastBallY = -1;
    private double lastSecondaryBallX = -1;
    private double lastSecondaryBallY = -1;

    // Screen shake effect
    private double shakeIntensity = 0;
    private double shakeOffsetX = 0;
    private double shakeOffsetY = 0;

    // Score animation (per player)
    private double score1Scale = 1.0;
    private double score2Scale = 1.0;
    private int lastPlayer1Score = -1; // Force init
    private int lastPlayer2Score = -1;

    // Cached fonts and colors to avoid per-frame allocations
    private static final Color SCORE_COLOR = Color.web("#808080");
    private static final javafx.scene.text.Font SCORE_FONT_BASE = javafx.scene.text.Font.font("Arial",
            javafx.scene.text.FontWeight.BOLD, 150);

    // Shield visual tracking (set externally when shield is active)
    private boolean player1ShieldActive = false;
    private boolean player2ShieldActive = false;

    // Power-up collection flash effect
    private double collectionFlashAlpha = 0;
    private Color collectionFlashColor = Color.WHITE;

    // Animated Tron-style background grid
    private double gridAnimationPhase = 0;
    private static final double GRID_SPACING = 40.0;
    private static final Color GRID_COLOR = Color.web("#00f3ff", 0.08); // Subtle cyan
    private static final Color GRID_PULSE_COLOR = Color.web("#00f3ff", 0.25); // Brighter pulse

    // Confetti system for win celebration
    private static final int MAX_CONFETTI = 150;
    private final Particle[] confettiPool = new Particle[MAX_CONFETTI];
    private int activeConfettiCount = 0;
    private boolean confettiActive = false;

    // Sound manager for sound effects
    private SoundManager soundManager;

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    // Image Caches (Bit Blit Optimization)
    private final Map<PowerUpType, Image> powerUpImageCache = new HashMap<>();

    public Image getPowerUpImage(PowerUpType type) {
        return powerUpImageCache.get(type);
    }

    private Image ballImageCache;
    private static final int CACHE_SIZE = 256; // High-Res Cache (256x256) for crisp scaling

    public GameRenderer() {
        preRenderResources();
        // Initialize particle pool
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particlePool[i] = new Particle();
        }
        // Initialize confetti pool
        for (int i = 0; i < MAX_CONFETTI; i++) {
            confettiPool[i] = new Particle();
        }
    }

    private void preRenderResources() {
        // 1. Pre-render Ball
        Canvas ballCanvas = new Canvas(CACHE_SIZE, CACHE_SIZE);
        GraphicsContext bgc = ballCanvas.getGraphicsContext2D();
        double center = CACHE_SIZE / 2.0;
        double radius = 60.0; // High-res radius for downscaling (crisp edges)

        // Draw glow and ball on temp canvas
        bgc.setEffect(GLOW_WHITE);
        bgc.setFill(Color.WHITE);
        bgc.fillOval(center - radius, center - radius, radius * 2, radius * 2);
        bgc.setEffect(null);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        ballImageCache = ballCanvas.snapshot(params, null);

        // 2. Pre-render PowerUps
        for (PowerUpType type : PowerUpType.values()) {
            Canvas pCanvas = new Canvas(CACHE_SIZE, CACHE_SIZE);
            GraphicsContext pgc = pCanvas.getGraphicsContext2D();

            // Draw high-res neon icon
            drawNeonPowerUpIconOnCanvas(pgc, type, center, center, radius);

            powerUpImageCache.put(type, pCanvas.snapshot(params, null));
        }
    }

    /**
     * Helper to draw the vector icon onto a temporary canvas for caching.
     */
    private void drawNeonPowerUpIconOnCanvas(GraphicsContext gc, PowerUpType type, double cx, double cy, double r) {
        Color color = getColorForPowerUp(type);
        double d = r * 2;

        // 1. Glow Ring
        gc.setEffect(getCachedGlow(color));
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.setFill(color.deriveColor(0, 1, 1, 0.15));
        gc.strokeOval(cx - r, cy - r, d, d);
        gc.fillOval(cx - r, cy - r, d, d);

        // 2. Symbol
        gc.setEffect(null);
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);

        double s = r * 0.5;

        switch (type) {
            case BIGGER_PADDLE -> {
                // Two arrows pointing outwards (Vertical)
                // Top Arrow
                gc.strokeLine(cx, cy - s * 0.3, cx, cy - s); // Shaft
                gc.strokeLine(cx - s * 0.4, cy - s * 0.6, cx, cy - s); // Left wing
                gc.strokeLine(cx + s * 0.4, cy - s * 0.6, cx, cy - s); // Right wing

                // Bottom Arrow
                gc.strokeLine(cx, cy + s * 0.3, cx, cy + s); // Shaft
                gc.strokeLine(cx - s * 0.4, cy + s * 0.6, cx, cy + s); // Left wing
                gc.strokeLine(cx + s * 0.4, cy + s * 0.6, cx, cy + s); // Right wing
            }
            case SMALLER_ENEMY_PADDLE -> {
                // Two arrows pointing inwards (Vertical)
                // Top Arrow (pointing down)
                gc.strokeLine(cx, cy - s, cx, cy - s * 0.2); // Shaft
                gc.strokeLine(cx - s * 0.4, cy - s * 0.6, cx, cy - s * 0.2); // Left wing
                gc.strokeLine(cx + s * 0.4, cy - s * 0.6, cx, cy - s * 0.2); // Right wing

                // Bottom Arrow (pointing up)
                gc.strokeLine(cx, cy + s, cx, cy + s * 0.2); // Shaft
                gc.strokeLine(cx - s * 0.4, cy + s * 0.6, cx, cy + s * 0.2); // Left wing
                gc.strokeLine(cx + s * 0.4, cy + s * 0.6, cx, cy + s * 0.2); // Right wing
            }
            case DOUBLE_BALL -> {
                gc.fillOval(cx - s, cy - s / 2, s, s);
                gc.fillOval(cx, cy - s / 2, s, s);
            }
            case SHIELD -> {
                // Shield shifted down slightly for visual centering
                double offsetY = s * 0.2; // Shift down by 20% of size
                gc.beginPath();
                gc.moveTo(cx - s, cy - s + offsetY);
                gc.lineTo(cx + s, cy - s + offsetY);
                gc.lineTo(cx + s, cy + offsetY);
                gc.quadraticCurveTo(cx, cy + s * 1.5 + offsetY, cx - s, cy + offsetY);
                gc.closePath();
                gc.stroke();
            }
            case BARRIERLESS -> {
                gc.strokeLine(cx - s, cy - s, cx + s, cy + s);
                gc.strokeLine(cx + s, cy - s, cx - s, cy + s);
            }
            case SLOW_ENEMY_PADDLE -> {
                gc.beginPath();
                gc.moveTo(cx, cy);
                gc.arc(cx, cy, s, s, 0, 270);
                gc.stroke();
            }
            case FASTER_BALL_ENEMY_SIDE -> {
                gc.beginPath();
                gc.moveTo(cx + s * 0.6, cy - s);
                gc.lineTo(cx - s * 0.2, cy + s * 0.1);
                gc.lineTo(cx + s * 0.6, cy + s * 0.1);
                gc.lineTo(cx - s * 0.6, cy + s);
                gc.stroke();
            }
        }
    }

    private static class Particle {
        double x, y, vx, vy;
        double life, maxLife;
        double size = 6.0; // Size for confetti particles
        Color color;
        boolean active = false;
    }

    /**
     * Clears all visual effects (trail, particles) for a fresh game start.
     */
    public void reset() {
        // Reset buffers
        trailHead = 0;
        trailSize = 0;
        for (Particle p : particlePool)
            p.active = false;
        activeParticleCount = 0;
        lastBallX = -1;
        lastBallY = -1;
        lastSecondaryBallX = -1;
        lastSecondaryBallY = -1;
        shakeIntensity = 0;
        shakeOffsetX = 0;
        shakeOffsetY = 0;
        score1Scale = 1.0;
        score2Scale = 1.0;
        lastPlayer1Score = -1;
        lastPlayer2Score = -1;
        player1ShieldActive = false;
        player2ShieldActive = false;
        leftPaddleHitHandled = false;
        rightPaddleHitHandled = false;
        leftPaddleHitHandled2 = false;
        rightPaddleHitHandled2 = false;
        collectionFlashAlpha = 0;
    }

    /** Set shield active status for rendering visual shield behind paddle */
    public void setShieldActive(int player, boolean active) {
        if (player == 1)
            player1ShieldActive = active;
        else
            player2ShieldActive = active;
    }

    /** Trigger collection flash effect when power-up is picked up */
    public void triggerCollectionEffect(Color color) {
        collectionFlashAlpha = 0.4;
        collectionFlashColor = color;
        // Spawn particles at random location for collection
        spawnParticles(LOGICAL_WIDTH / 2, LOGICAL_HEIGHT / 2, color, 15, 0.8);
    }

    // Helper to spawn pooled particles
    private void spawnParticles(double x, double y, Color color, int count, double life) {
        int spawned = 0;
        for (int i = 0; i < MAX_PARTICLES && spawned < count; i++) {
            if (!particlePool[i].active) {
                Particle p = particlePool[i];
                p.active = true;
                p.x = x;
                p.y = y;
                double angle = Math.random() * Math.PI * 2;
                double speed = 3 + Math.random() * 5;
                p.vx = Math.cos(angle) * speed;
                p.vy = Math.sin(angle) * speed;
                p.life = life;
                p.maxLife = life;
                p.color = color;
                spawned++;
            }
        }
    }

    /**
     * Trigger screen shake effect (call on collision).
     */
    public void triggerShake(double intensity) {
        this.shakeIntensity = intensity;
    }

    /**
     * Spawn goal particles when a point is scored.
     */
    public void spawnGoalParticles(int side) {
        double x = (side == 1) ? LOGICAL_WIDTH - 20 : 20; // Right or left edge
        double y = LOGICAL_HEIGHT / 2;
        Color color = (side == 1) ? NEON_BLUE : NEON_PINK;

        // Use pooled spawner (20 particles)
        spawnParticles(x, y, color, 20, 1.0);
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

        // Update screen shake
        if (shakeIntensity > 0) {
            shakeOffsetX = (Math.random() - 0.5) * shakeIntensity * 10;
            shakeOffsetY = (Math.random() - 0.5) * shakeIntensity * 10;
            shakeIntensity *= 0.85; // Decay
            if (shakeIntensity < 0.1)
                shakeIntensity = 0;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        // Check for score changes (animate only the player who scored)
        int p1Score = state.score().player1();
        int p2Score = state.score().player2();
        if (p1Score != lastPlayer1Score) {
            score1Scale = 1.5; // Player 1 scored
            lastPlayer1Score = p1Score;
        }
        if (p2Score != lastPlayer2Score) {
            score2Scale = 1.5; // Player 2 scored
            lastPlayer2Score = p2Score;
        }
        // Animate scores back to normal
        if (score1Scale > 1.0) {
            score1Scale = Math.max(1.0, score1Scale - 0.03);
        }
        if (score2Scale > 1.0) {
            score2Scale = Math.max(1.0, score2Scale - 0.03);
        }

        // Apply transformation for scaling (stretch to fill) + shake
        gc.save();
        gc.translate(shakeOffsetX, shakeOffsetY);
        gc.scale(scaleX, scaleY);

        // Draw animated Tron-style background grid
        drawTronGrid(gc);

        // Draw Field Decorations (Line, Scores)
        drawFieldDecorations(gc, state);

        // Draw shields behind paddles first (so paddles appear on top)
        if (player1ShieldActive && state.player1Paddle() != null) {
            drawShieldBehindPaddle(gc, state.player1Paddle(), true);
        }
        if (player2ShieldActive && state.player2Paddle() != null) {
            drawShieldBehindPaddle(gc, state.player2Paddle(), false);
        }

        if (state.player1Paddle() != null) {
            drawNeonPaddle(gc, state.player1Paddle(), NEON_BLUE);
            drawEffectTimerBars(gc, state.player1Paddle(), 1);
        }
        if (state.player2Paddle() != null) {
            drawNeonPaddle(gc, state.player2Paddle(), NEON_PINK);
            drawEffectTimerBars(gc, state.player2Paddle(), 2);
        }

        // Update and draw particles
        updateAndDrawParticles(gc);

        List<BallState> balls = state.balls();
        if (balls != null) {
            currentBallIndex = 0; // Reset ball index for collision tracking
            for (BallState ball : balls) {
                // Draw ball trail
                drawBallTrail(gc, ball);
                // Draw actual ball (collision detection uses currentBallIndex)
                drawNeonBall(gc, ball, state);
                currentBallIndex++;
            }
        }

        List<PowerUpState> powerUps = state.activePowerUpsOnField();
        if (powerUps != null) {
            for (PowerUpState powerUp : powerUps) {
                drawNeonPowerUp(gc, powerUp);
            }
        }

        // Draw collection flash effect overlay
        if (collectionFlashAlpha > 0) {
            gc.setEffect(null);
            gc.setFill(collectionFlashColor.deriveColor(0, 1, 1, collectionFlashAlpha));
            // Draw over the entire logical field
            gc.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

            // Decay
            collectionFlashAlpha -= 0.02;
            if (collectionFlashAlpha < 0)
                collectionFlashAlpha = 0;
        }

        // Draw confetti (win celebration)
        updateAndDrawConfetti(gc);

        // Restore original transformation
        gc.restore();
    }

    private void updateAndDrawParticles(GraphicsContext gc) {
        // Use Global Alpha to avoid Color.deriveColor() allocation per particle per
        // frame
        double originalAlpha = gc.getGlobalAlpha();

        for (Particle p : particlePool) {
            if (!p.active)
                continue;

            p.x += p.vx;
            p.y += p.vy;
            p.life -= 0.05;

            if (p.life <= 0) {
                p.active = false;
            } else {
                double alpha = Math.max(0, p.life / p.maxLife);
                gc.setGlobalAlpha(alpha);
                gc.setFill(p.color);
                double size = 6 * alpha;
                gc.fillOval(p.x - size / 2, p.y - size / 2, size, size);
            }
        }

        gc.setGlobalAlpha(originalAlpha); // Restore alpha
    }

    private void drawBallTrail(GraphicsContext gc, BallState ball) {
        // Add current position to ring buffer
        ballTrailBuffer[trailHead][0] = ball.xPosition();
        ballTrailBuffer[trailHead][1] = ball.yPosition();

        trailHead = (trailHead + 1) % TRAIL_LENGTH;
        if (trailSize < TRAIL_LENGTH) {
            trailSize++;
        }

        // Draw trail (Iterate from oldest to newest)
        // Correct start index: (head - size + length) % length
        int startIndex = (trailHead - trailSize + TRAIL_LENGTH) % TRAIL_LENGTH;

        // Save alpha
        double originalAlpha = gc.getGlobalAlpha();

        for (int i = 0; i < trailSize - 1; i++) {
            int idx = (startIndex + i) % TRAIL_LENGTH;
            double[] pos = ballTrailBuffer[idx];

            double alpha = (double) (i + 1) / trailSize * 0.5;
            double size = ball.radius() * 2 * alpha;

            gc.setGlobalAlpha(alpha);
            gc.setFill(Color.WHITE); // Use setGlobalAlpha instead of deriveColor
            gc.fillOval(pos[0] - size / 2, pos[1] - size / 2, size, size);
        }

        gc.setGlobalAlpha(originalAlpha); // Restore
    }

    private void spawnCollisionParticles(double x, double y, Color color) {
        spawnParticles(x, y, color, 8, 0.6); // Use pooled spawner
    }

    private void drawFieldDecorations(GraphicsContext gc, GameState state) {
        // 1. Draw Center Line
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        gc.setLineDashes(15, 15); // Dashed pattern
        gc.strokeLine(LOGICAL_WIDTH / 2, 0, LOGICAL_WIDTH / 2, LOGICAL_HEIGHT);
        gc.setLineDashes(null);

        // 2. Draw Background Scores with per-player animation
        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        gc.setTextBaseline(javafx.geometry.VPos.CENTER);

        // Get current scores directly from state (no caching - simpler and reliable)
        int score1 = state.score().player1();
        int score2 = state.score().player2();

        // Trigger animation on score change
        if (score1 != lastPlayer1Score) {
            score1Scale = 1.5; // Pop effect
            lastPlayer1Score = score1;
        }
        if (score2 != lastPlayer2Score) {
            score2Scale = 1.5; // Pop effect
            lastPlayer2Score = score2;
        }

        double originalAlpha = gc.getGlobalAlpha();

        // Player 1 Score (Left Center)
        double fontSize1 = 150 * score1Scale;
        double alpha1 = Math.min(1.0, 0.6 + (score1Scale - 1.0));
        gc.setGlobalAlpha(alpha1);
        gc.setFill(SCORE_COLOR); // Use cached color
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize1));
        gc.fillText(String.valueOf(score1), LOGICAL_WIDTH / 4, LOGICAL_HEIGHT / 2);

        // Player 2 Score (Right Center)
        double fontSize2 = 150 * score2Scale;
        double alpha2 = Math.min(1.0, 0.6 + (score2Scale - 1.0));
        gc.setGlobalAlpha(alpha2);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize2));
        gc.fillText(String.valueOf(score2), LOGICAL_WIDTH * 3 / 4, LOGICAL_HEIGHT / 2);

        gc.setGlobalAlpha(originalAlpha); // Restore
    }

    // Cached Effects to avoid Garbage Collection lag
    private static final Effect GLOW_BLUE = new javafx.scene.effect.DropShadow(20, NEON_BLUE);
    private static final Effect GLOW_PINK = new javafx.scene.effect.DropShadow(12, NEON_PINK); // Reduced for visual
                                                                                               // balance
    private static final Effect GLOW_GREEN = new javafx.scene.effect.DropShadow(20, NEON_GREEN);
    private static final Effect GLOW_RED = new javafx.scene.effect.DropShadow(20, NEON_RED);
    private static final Effect GLOW_YELLOW = new javafx.scene.effect.DropShadow(20, NEON_YELLOW);
    private static final Effect GLOW_WHITE = new javafx.scene.effect.DropShadow(15, Color.WHITE);
    private static final Effect GLOW_CYAN = new javafx.scene.effect.DropShadow(20, Color.CYAN);
    private static final Effect GLOW_ORANGE = new javafx.scene.effect.DropShadow(20, Color.ORANGE);
    private static final Effect GLOW_MAGENTA = new javafx.scene.effect.DropShadow(20, Color.MAGENTA);

    // Fallback for dynamic colors (still better to cache if possible, but minimal
    // usage)

    private Effect getCachedGlow(Color color) {
        if (color == NEON_BLUE)
            return GLOW_BLUE;
        if (color == NEON_PINK)
            return GLOW_PINK;
        if (color == NEON_GREEN)
            return GLOW_GREEN;
        if (color == NEON_RED)
            return GLOW_RED;
        if (color == NEON_YELLOW)
            return GLOW_YELLOW;
        if (color == Color.WHITE)
            return GLOW_WHITE;
        if (color == Color.CYAN)
            return GLOW_CYAN;
        if (color == Color.ORANGE)
            return GLOW_ORANGE;
        return GLOW_MAGENTA;
    }

    private void drawNeonPaddle(GraphicsContext gc, PaddleState paddle, Color color) {
        double x = paddle.xPosition();
        double y = paddle.yPosition() - paddle.height() / 2;
        double w = paddle.width();
        double h = paddle.height();

        // Glow - Use Cached Effect
        gc.setEffect(getCachedGlow(color));

        gc.setFill(color);
        gc.fillRoundRect(x, y, w, h, 10, 10);

        // Inner bright core
        gc.setEffect(null);
        gc.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.8));
        gc.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 8, 8);
    }

    private void drawShieldBehindPaddle(GraphicsContext gc, PaddleState paddle, boolean isPlayer1) {
        double paddleX = paddle.xPosition();
        double paddleY = paddle.yPosition();
        double paddleH = paddle.height();

        // Shield position: behind the paddle (towards the goal)
        double shieldX = isPlayer1 ? paddleX - 15 : paddleX + paddle.width() + 5;
        double shieldW = 10;
        double shieldH = paddleH + 40; // Taller than paddle
        double shieldY = paddleY - shieldH / 2;

        Color shieldColor = Color.web("#00f3ff"); // Cyan glow

        // Outer glow - Use Cached Effect (Cyan is effectively Neon Blue here or close
        // enough)
        gc.setEffect(GLOW_BLUE);
        gc.setFill(shieldColor.deriveColor(0, 1, 1, 0.6));
        gc.fillRoundRect(shieldX, shieldY, shieldW, shieldH, 5, 5);

        // Inner bright core
        gc.setEffect(null);
        gc.setFill(Color.WHITE.deriveColor(0, 1, 1, 0.9));
        gc.fillRoundRect(shieldX + 2, shieldY + 5, shieldW - 4, shieldH - 10, 3, 3);
    }

    // Collision debounce flags (primary ball)
    private boolean leftPaddleHitHandled = false;
    private boolean rightPaddleHitHandled = false;
    // Collision debounce flags (secondary ball)
    private boolean leftPaddleHitHandled2 = false;
    private boolean rightPaddleHitHandled2 = false;
    // Wall hit flags
    private boolean wallHitHandled = false;
    private boolean wallHitHandled2 = false;
    // Track which ball index we're drawing
    private int currentBallIndex = 0;

    private void drawNeonBall(GraphicsContext gc, BallState ball, GameState state) {
        double r = ball.radius();
        double d = r * 2;
        double x = ball.xPosition() - r;
        double y = ball.yPosition() - r;

        // Get the correct last position and debounce flags based on ball index
        boolean isSecondary = currentBallIndex > 0;
        double prevX = isSecondary ? lastSecondaryBallX : lastBallX;

        // Detect wall collision (top/bottom)
        double ballY = ball.yPosition();
        double fieldHeight = 600.0; // Fixed game height

        if (isSecondary) {
            if (ballY > 50 && ballY < fieldHeight - 50) {
                wallHitHandled2 = false;
            } else if ((ballY <= r + 5 || ballY >= fieldHeight - r - 5) && !wallHitHandled2) {
                if (soundManager != null)
                    soundManager.playSound(SoundManager.SoundType.WALL_HIT);
                wallHitHandled2 = true;
            }
        } else {
            if (ballY > 50 && ballY < fieldHeight - 50) {
                wallHitHandled = false;
            } else if ((ballY <= r + 5 || ballY >= fieldHeight - r - 5) && !wallHitHandled) {
                if (soundManager != null)
                    soundManager.playSound(SoundManager.SoundType.WALL_HIT);
                wallHitHandled = true;
            }
        }

        // Detect paddle collision and spawn particles
        if (prevX >= 0) {
            double ballX = ball.xPosition();
            PaddleState leftPaddle = state.player1Paddle();
            PaddleState rightPaddle = state.player2Paddle();

            if (isSecondary) {
                // Secondary ball collision logic
                if (ballX > 100) {
                    leftPaddleHitHandled2 = false;
                } else if (leftPaddle != null && ballX > prevX && !leftPaddleHitHandled2) {
                    spawnCollisionParticles(ballX, ball.yPosition(), NEON_BLUE);
                    triggerShake(0.8); // Slightly weaker shake for secondary
                    if (soundManager != null)
                        soundManager.playSound(SoundManager.SoundType.PADDLE_HIT);
                    leftPaddleHitHandled2 = true;
                }
                if (ballX < 700) {
                    rightPaddleHitHandled2 = false;
                } else if (rightPaddle != null && ballX < prevX && !rightPaddleHitHandled2) {
                    spawnCollisionParticles(ballX, ball.yPosition(), NEON_PINK);
                    triggerShake(0.8);
                    if (soundManager != null)
                        soundManager.playSound(SoundManager.SoundType.PADDLE_HIT);
                    rightPaddleHitHandled2 = true;
                }
            } else {
                // Primary ball collision logic (same as before)
                if (ballX > 100) {
                    leftPaddleHitHandled = false;
                } else if (leftPaddle != null && ballX > prevX && !leftPaddleHitHandled) {
                    spawnCollisionParticles(ballX, ball.yPosition(), NEON_BLUE);
                    triggerShake(1.0);
                    if (soundManager != null)
                        soundManager.playSound(SoundManager.SoundType.PADDLE_HIT);
                    leftPaddleHitHandled = true;
                }
                if (ballX < 700) {
                    rightPaddleHitHandled = false;
                } else if (rightPaddle != null && ballX < prevX && !rightPaddleHitHandled) {
                    spawnCollisionParticles(ballX, ball.yPosition(), NEON_PINK);
                    triggerShake(1.0);
                    if (soundManager != null)
                        soundManager.playSound(SoundManager.SoundType.PADDLE_HIT);
                    rightPaddleHitHandled = true;
                }
            }
        }

        // Update the correct last position based on ball index
        if (isSecondary) {
            lastSecondaryBallX = ball.xPosition();
            lastSecondaryBallY = ball.yPosition();
        } else {
            lastBallX = ball.xPosition();
            lastBallY = ball.yPosition();
        }

        // Optimized Drawing: Use Cached Image
        if (ballImageCache != null) {
            // Draw image slightly larger than physical radius to account for glow in image
            double drawSize = d * 2.5; // Scale factor because cache includes glow margin
            gc.drawImage(ballImageCache, ball.xPosition() - drawSize / 2, ball.yPosition() - drawSize / 2, drawSize,
                    drawSize);
        } else {
            // Fallback (should not happen)
            gc.setEffect(GLOW_WHITE);
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, d, d);
            gc.setEffect(null);
        }
    }

    private void drawNeonPowerUp(GraphicsContext gc, PowerUpState powerUp) {
        double cx = powerUp.xPosition();
        double cy = powerUp.yPosition();
        double r = powerUp.radius();

        // 1. Pulse Animation
        double time = System.currentTimeMillis() / 150.0;
        double pulse = 1.0 + Math.sin(time) * 0.15; // +/- 15% pulsating size

        // Optimized Drawing: Use Cached Image
        Image img = powerUpImageCache.get(powerUp.type());
        if (img != null) {
            // Calculate draw size based on pulse
            // Base radius in cache was 60.0 (visual radius), drawing size depends on actual
            // powerUp radius
            // Logic: Cache image is 256x256. Center is 128,128. Visual radius was 60.
            // We want the visual radius (60 in cache) to match 'r' * 'pulse'.

            double scaleFactor = (r * pulse) / 60.0;
            double drawW = img.getWidth() * scaleFactor;
            double drawH = img.getHeight() * scaleFactor;

            gc.drawImage(img, cx - drawW / 2, cy - drawH / 2, drawW, drawH);
        } else {
            // Fallback if cache fails (should not happen)
            Color color = getColorForPowerUp(powerUp.type());
            gc.setEffect(getCachedGlow(color));
            gc.setFill(color);
            gc.fillOval(cx - r, cy - r, r * 2, r * 2);
            gc.setEffect(null);
        }
    }

    public Color getColorForPowerUp(PowerUpType type) {
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

    // ==================== TRON GRID ====================

    private void drawTronGrid(GraphicsContext gc) {
        // Animate grid phase
        gridAnimationPhase += 0.02;
        if (gridAnimationPhase > GRID_SPACING)
            gridAnimationPhase = 0;

        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(1.0);

        // Vertical lines with animation offset
        for (double x = -gridAnimationPhase; x < LOGICAL_WIDTH + GRID_SPACING; x += GRID_SPACING) {
            // Pulse effect on certain lines
            if (Math.abs((x + gridAnimationPhase) % (GRID_SPACING * 4)) < 2) {
                gc.setStroke(GRID_PULSE_COLOR);
            } else {
                gc.setStroke(GRID_COLOR);
            }
            gc.strokeLine(x, 0, x, LOGICAL_HEIGHT);
        }

        // Horizontal lines (static)
        gc.setStroke(GRID_COLOR);
        for (double y = 0; y < LOGICAL_HEIGHT + GRID_SPACING; y += GRID_SPACING) {
            gc.strokeLine(0, y, LOGICAL_WIDTH, y);
        }
    }

    // ==================== CONFETTI ====================

    private static final Color[] CONFETTI_COLORS = {
            Color.web("#ff00ff"), Color.web("#00f3ff"), Color.web("#ffdc00"),
            Color.web("#00ff00"), Color.web("#ff3366"), Color.WHITE
    };

    /** Triggers confetti explosion from center of screen */
    public void triggerConfetti() {
        confettiActive = true;
        activeConfettiCount = 0;
        java.util.Random rand = new java.util.Random();

        double centerX = LOGICAL_WIDTH / 2.0;
        double centerY = LOGICAL_HEIGHT / 2.0;

        for (int i = 0; i < MAX_CONFETTI && activeConfettiCount < MAX_CONFETTI; i++) {
            Particle p = confettiPool[activeConfettiCount++];
            p.active = true;
            p.x = centerX + (rand.nextDouble() - 0.5) * 100;
            p.y = centerY;
            p.vx = (rand.nextDouble() - 0.5) * 600; // Wide spread
            p.vy = -rand.nextDouble() * 400 - 200; // Upward burst
            p.life = 1.0;
            p.color = CONFETTI_COLORS[rand.nextInt(CONFETTI_COLORS.length)];
            p.size = 6 + rand.nextDouble() * 8;
        }
    }

    /** Check if confetti animation is still running */
    public boolean isConfettiActive() {
        return confettiActive;
    }

    private void updateAndDrawConfetti(GraphicsContext gc) {
        if (!confettiActive)
            return;

        boolean anyActive = false;
        double gravity = 15.0; // Pixels per frame (downward pull)

        for (int i = 0; i < activeConfettiCount; i++) {
            Particle p = confettiPool[i];
            if (!p.active)
                continue;

            anyActive = true;
            p.x += p.vx * 0.016; // Assume ~60fps
            p.y += p.vy * 0.016;
            p.vy += gravity; // Gravity
            p.life -= 0.008; // Fade out

            if (p.life <= 0 || p.y > LOGICAL_HEIGHT + 50) {
                p.active = false;
                continue;
            }

            gc.setFill(p.color.deriveColor(0, 1, 1, p.life));
            // Draw as small rotating rectangles for confetti effect
            gc.save();
            gc.translate(p.x, p.y);
            gc.rotate(p.x * 0.5 + p.y * 0.3); // Spin based on position
            gc.fillRect(-p.size / 2, -p.size / 4, p.size, p.size / 2);
            gc.restore();
        }

        if (!anyActive) {
            confettiActive = false;
            activeConfettiCount = 0;
        }
    }

    // ==================== EFFECT TIMER BARS ====================

    private java.util.Map<Integer, java.util.List<EffectTimerInfo>> effectTimers = new java.util.HashMap<>();

    public record EffectTimerInfo(Color color, double remainingRatio) {
    }

    /** Set effect timers to display under paddles */
    public void setEffectTimers(int player, java.util.List<EffectTimerInfo> timers) {
        effectTimers.put(player, timers);
    }

    private void drawEffectTimerBars(GraphicsContext gc, PaddleState paddle, int player) {
        java.util.List<EffectTimerInfo> timers = effectTimers.get(player);
        if (timers == null || timers.isEmpty())
            return;

        double paddleX = paddle.xPosition();
        double paddleCenterY = paddle.yPosition();
        double paddleHalfHeight = paddle.height() / 2;
        double barWidth = paddle.width();
        double barHeight = 3;
        double spacing = 5;
        double margin = 12;

        // Calculate total height needed for bars
        double totalBarsHeight = timers.size() * spacing + margin;

        // The paddle's bottom edge position
        double paddleBottomY = paddleCenterY + paddleHalfHeight;

        // Check if bars would go off-screen if drawn below
        boolean showAbove = (paddleBottomY + totalBarsHeight) > (LOGICAL_HEIGHT - 5);

        double startY;
        if (showAbove) {
            // Draw above paddle (use top edge)
            double paddleTopY = paddleCenterY - paddleHalfHeight;
            startY = paddleTopY - margin - (timers.size() - 1) * spacing - barHeight;
        } else {
            // Draw below paddle
            startY = paddleBottomY + margin;
        }

        int index = 0;
        for (EffectTimerInfo timer : timers) {
            double y = startY + index * spacing;

            // Background bar (more transparent)
            gc.setFill(Color.gray(0.2, 0.3));
            gc.fillRect(paddleX, y, barWidth, barHeight);

            // Progress bar (more transparent)
            gc.setFill(timer.color.deriveColor(0, 1, 1, 0.5));
            gc.fillRect(paddleX, y, barWidth * timer.remainingRatio, barHeight);

            index++;
        }
    }
}
