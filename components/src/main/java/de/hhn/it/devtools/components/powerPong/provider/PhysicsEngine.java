package de.hhn.it.devtools.components.powerPong.provider;

import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Physics engine for PowerPong game simulation.
 * Handles ball movement, paddle movement, collision detection, and scoring.
 *
 * This class encapsulates all physics-related calculations including:
 * - Ball trajectory and velocity management
 * - Paddle movement with bounds checking
 * - Ball-paddle and ball-wall collision detection
 * - Score detection when balls go out of bounds
 * - Power-up effects on physics parameters
 */
public class PhysicsEngine {

    public static final double FIELD_WIDTH = 800.0;
    public static final double FIELD_HEIGHT = 600.0;
    public static final double PADDLE_WIDTH = 14.0;
    public static final double PADDLE_HEIGHT = 110.0;
    public static final double BALL_RADIUS = 8.0;
    public static final double LEFT_PADDLE_X = 35.0;
    public static final double RIGHT_PADDLE_X = FIELD_WIDTH - LEFT_PADDLE_X - PADDLE_WIDTH;

    // Base speeds (modifiable for difficulty)
    private double basePaddleSpeed = 400.0; // px / second
    private double baseBallSpeed = 360.0; // px / second
    private double difficultyMultiplier = 1.0;

    private static final double POWERUP_SPAWN_INTERVAL = 6.0; // seconds
    private static final double POWERUP_RADIUS = 12.0;

    private final Random random;

    private Ball ball;
    private Ball secondaryBall;

    private double paddle1CenterY = FIELD_HEIGHT / 2.0;
    private double paddle2CenterY = FIELD_HEIGHT / 2.0;

    private double leftHeightFactor = 1.0;
    private double rightHeightFactor = 1.0;
    private double leftSpeedFactor = 1.0;
    private double rightSpeedFactor = 1.0;

    private boolean noWalls;

    public PhysicsEngine(Random random) {
        this.random = random;
    }

    // reset methods
    public void reset() {
        paddle1CenterY = FIELD_HEIGHT / 2.0;
        paddle2CenterY = FIELD_HEIGHT / 2.0;
        ball = null;
        secondaryBall = null;
        resetModifiers();
        difficultyMultiplier = 1.0;
    }

    public void resetModifiers() {
        leftHeightFactor = 1.0;
        rightHeightFactor = 1.0;
        leftSpeedFactor = 1.0;
        rightSpeedFactor = 1.0;
        noWalls = false;
    }

    // difficulty level
    public void setDifficultyMultiplier(double multiplier) {
        this.difficultyMultiplier = multiplier;
        // Update current ball velocities if they exist
        if (ball != null) {
            updateBallVelocityForDifficulty(ball);
        }
        if (secondaryBall != null) {
            updateBallVelocityForDifficulty(secondaryBall);
        }
    }

    private void updateBallVelocityForDifficulty(Ball b) {
        double currentSpeed = Math.hypot(b.vx, b.vy);
        if (currentSpeed > 0) {
            double targetSpeed = baseBallSpeed * difficultyMultiplier;
            // Scale current velocity vector to match target speed
            double scale = targetSpeed / currentSpeed;
            // Only scale if significantly different (to avoid overriding power-up effects
            // completely)
            // Ideally, difficulty should be a base factor.
            // For simplicity in this refactor: we just scale.
            b.vx *= scale;
            b.vy *= scale;
        }
    }

    public double getBaseBallSpeed() {
        return baseBallSpeed * difficultyMultiplier;
    }

    // Start ball & control double ball
    public void launchBall(int horizontalDirection) {
        double startX = FIELD_WIDTH / 2.0;
        double startY = FIELD_HEIGHT / 2.0;
        double speed = getBaseBallSpeed();
        double vx = horizontalDirection * speed;
        double vy = speed * (random.nextDouble() * 0.6 - 0.3);
        ball = new Ball(startX, startY, vx, vy);
    }

    public void spawnSecondaryBall() {
        if (ball != null && secondaryBall == null) {
            secondaryBall = new Ball(ball.x, ball.y, -ball.vx, -ball.vy);
        }
    }

    public void removeSecondaryBall() {
        secondaryBall = null;
    }

    // move paddle
    public void movePaddle(boolean left, double direction, double deltaSeconds) {
        if (direction == 0 || deltaSeconds <= 0) {
            return;
        }
        double modifier = left ? leftSpeedFactor : rightSpeedFactor;
        double speed = basePaddleSpeed * modifier * deltaSeconds; // Paddle speed doesnt necessarily need to scale with
        // difficulty, but could.
        double delta = direction * speed;
        double currentHeight = left ? PADDLE_HEIGHT * leftHeightFactor : PADDLE_HEIGHT * rightHeightFactor;
        double minY = currentHeight / 2.0;
        double maxY = FIELD_HEIGHT - currentHeight / 2.0;

        if (left) {
            paddle1CenterY = clamp(paddle1CenterY + delta, minY, maxY);
        } else {
            paddle2CenterY = clamp(paddle2CenterY + delta, minY, maxY);
        }
    }

    /**
     * Updates ball positions and handles collisions.
     *
     * @param deltaSeconds time elapsed
     * @return 0 if no score, 1 if player 1 scored (ball went out right), 2 if
     *         player 2 scored (ball went out left)
     */
    public int updateBalls(double deltaSeconds) {
        int scoreEvent = 0;
        scoreEvent = updateBall(ball, deltaSeconds);
        if (scoreEvent != 0)
            return scoreEvent;

        if (secondaryBall != null) {
            scoreEvent = updateBall(secondaryBall, deltaSeconds);
        }
        return scoreEvent;
    }

    private int updateBall(Ball current, double deltaSeconds) {
        if (current == null)
            return 0;

        double dt = deltaSeconds;
        current.x += current.vx * dt;
        current.y += current.vy * dt;

        handleWallBounce(current);
        handlePaddleBounce(current);

        return checkScoring(current);
    }

    // Wall Collision / Wrap‑Around
    private void handleWallBounce(Ball current) {
        double top = BALL_RADIUS;
        double bottom = FIELD_HEIGHT - BALL_RADIUS;
        if (noWalls) {
            if (current.y < top) {
                current.y = bottom;
            } else if (current.y > bottom) {
                current.y = top;
            }
            return;
        }
        if (current.y <= top && current.vy < 0) {
            current.y = top;
            current.vy = -current.vy;
        } else if (current.y >= bottom && current.vy > 0) {
            current.y = bottom;
            current.vy = -current.vy;
        }
    }

    // Paddle Collision
    private boolean handlePaddleBounce(Ball current) {
        double leftPaddleRight = LEFT_PADDLE_X + PADDLE_WIDTH;
        double rightPaddleLeft = RIGHT_PADDLE_X;
        double leftTop = paddle1CenterY - (PADDLE_HEIGHT * leftHeightFactor) / 2.0;
        double leftBottom = paddle1CenterY + (PADDLE_HEIGHT * leftHeightFactor) / 2.0;
        double rightTop = paddle2CenterY - (PADDLE_HEIGHT * rightHeightFactor) / 2.0;
        double rightBottom = paddle2CenterY + (PADDLE_HEIGHT * rightHeightFactor) / 2.0;

        boolean bounced = false;
        if (current.vx < 0 && current.x - BALL_RADIUS <= leftPaddleRight && current.y >= leftTop
                && current.y <= leftBottom) {
            current.x = leftPaddleRight + BALL_RADIUS;
            bounceFromPaddle(current, +1, paddle1CenterY, PADDLE_HEIGHT * leftHeightFactor);
            bounced = true;
        } else if (current.vx > 0 && current.x + BALL_RADIUS >= rightPaddleLeft && current.y >= rightTop
                && current.y <= rightBottom) {
            current.x = rightPaddleLeft - BALL_RADIUS;
            bounceFromPaddle(current, -1, paddle2CenterY, PADDLE_HEIGHT * rightHeightFactor);
            bounced = true;
        }
        return bounced;
    }

    private void bounceFromPaddle(Ball current, int horizontalDirection, double paddleCenterY, double paddleHeight) {
        double relativeIntersect = (current.y - paddleCenterY) / (paddleHeight / 2.0);
        double speed = getBaseBallSpeed();
        current.vx = horizontalDirection * speed;
        current.vy = relativeIntersect * speed;
    }

    // Change ballspeed
    public void scaleBallVelocity(double factor) {
        if (ball != null) {
            ball.vx *= factor;
            ball.vy *= factor;
        }
        if (secondaryBall != null) {
            secondaryBall.vx *= factor;
            secondaryBall.vy *= factor;
        }
    }

    public void setBallVelocity(Ball ballToSet, double vx, double vy) {
        if (ballToSet != null) {
            ballToSet.vx = vx;
            ballToSet.vy = vy;
        }
    }

    // Calculate scoring
    private int checkScoring(Ball current) {
        if (current.x + BALL_RADIUS < 0) {
            return 2; // Player 2 scores (ball went out left)
        } else if (current.x - BALL_RADIUS > FIELD_WIDTH) {
            return 1; // Player 1 scores (ball went out right)
        }
        return 0;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    // Getters and Setters for state
    public PaddleState getLeftPaddleState() {
        return new PaddleState(LEFT_PADDLE_X, paddle1CenterY, PADDLE_WIDTH, PADDLE_HEIGHT * leftHeightFactor);
    }

    public PaddleState getRightPaddleState() {
        return new PaddleState(RIGHT_PADDLE_X, paddle2CenterY, PADDLE_WIDTH, PADDLE_HEIGHT * rightHeightFactor);
    }

    // Helper for AI
    public double getPaddle2Y() {
        return paddle2CenterY;
    }

    public List<BallState> getBallStates() {
        List<BallState> balls = new ArrayList<>();
        if (ball != null)
            balls.add(new BallState(ball.x, ball.y, BALL_RADIUS));
        if (secondaryBall != null)
            balls.add(new BallState(secondaryBall.x, secondaryBall.y, BALL_RADIUS));
        return balls;
    }

    public Ball getBall() {
        return ball;
    }

    public Ball getSecondaryBall() {
        return secondaryBall;
    }

    public void setLeftHeightFactor(double f) {
        this.leftHeightFactor = f;
    }

    public void setRightHeightFactor(double f) {
        this.rightHeightFactor = f;
    }

    public void setLeftSpeedFactor(double f) {
        this.leftSpeedFactor = f;
    }

    public void setRightSpeedFactor(double f) {
        this.rightSpeedFactor = f;
    }

    public void setNoWalls(boolean noWalls) {
        this.noWalls = noWalls;
    }

    public static class Ball {
        public double x;
        public double y;
        public double vx;
        public double vy;

        public Ball(double x, double y, double vx, double vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }
    }
}
