package de.hhn.it.devtools.components.powerpong.provider;

import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Physics engine for PowerPong game simulation.
 * Handles ball movement, paddle movement, collision detection, and scoring.
 *
 * <p>This class encapsulates all physics-related calculations including:
 * <ul>
 * <li>Ball trajectory and velocity management</li>
 * <li>Paddle movement with bounds checking</li>
 * <li>Ball-paddle and ball-wall collision detection</li>
 * <li>Score detection when balls go out of bounds</li>
 * <li>Power-up effects on physics parameters</li>
 * </ul>
 */
public class PhysicsEngine {

  public static final double FIELD_WIDTH = 800.0;
  public static final double FIELD_HEIGHT = 600.0;
  public static final double PADDLE_WIDTH = 14.0;
  public static final double PADDLE_HEIGHT = 110.0;
  public static final double BALL_RADIUS = 8.0;
  public static final double LEFT_PADDLE_X = 5.0;
  public static final double RIGHT_PADDLE_X = FIELD_WIDTH - LEFT_PADDLE_X - PADDLE_WIDTH;

  // Base speeds (modifiable for difficulty)
  private double basePaddleSpeed = 650.0; // px / second
  private double baseBallSpeed = 500.0; // px / second
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

  // Rally speed increase (reduced for smoother gameplay)
  private int rallyHitCount = 0;
  private double rallySpeedIncrease = 0.02; // 2% faster per hit
  private double maxRallyMultiplier = 1.5; // Max 50% faster

  // Collision tracking for listener callbacks
  private boolean collisionOccurred = false;

  /**
   * Sets the speed increase per rally hit.
   *
   * @param increase the percentage to increase speed (e.g. 0.02 for 2%)
   */
  public void setRallySpeedIncrease(double increase) {
    this.rallySpeedIncrease = increase;
  }

  /**
   * Sets the maximum speed multiplier for rallies.
   *
   * @param max the maximum multiplier (e.g. 1.5 for 50% faster)
   */
  public void setMaxRallyMultiplier(double max) {
    this.maxRallyMultiplier = max;
  }

  /**
   * Constructs a new PhysicsEngine.
   *
   * @param random the Random instance to use for variability
   */
  public PhysicsEngine(Random random) {
    this.random = random;
  }

  /**
   * Returns whether a collision occurred during the last update.
   *
   * @return true if ball collided with paddle or wall
   */
  public boolean wasCollisionDetected() {
    return collisionOccurred;
  }

  /**
   * Resets the collision flag. Should be called after processing collision
   * callback.
   */
  public void resetCollisionFlag() {
    collisionOccurred = false;
  }

  /**
   * Gets the current rally speed multiplier based on hit count.
   *
   * @return the current speed multiplier
   */
  public double getRallyMultiplier() {
    return Math.min(maxRallyMultiplier, 1.0 + (rallyHitCount * rallySpeedIncrease));
  }

  /**
   * Resets the physics engine to its initial state.
   */
  public void reset() {
    paddle1CenterY = FIELD_HEIGHT / 2.0;
    paddle2CenterY = FIELD_HEIGHT / 2.0;
    ball = null;
    secondaryBall = null;
    resetModifiers();
    difficultyMultiplier = 1.0;
    rallyHitCount = 0;
  }

  /**
   * Resets all paddle modifiers to default values.
   */
  public void resetModifiers() {
    leftHeightFactor = 1.0;
    rightHeightFactor = 1.0;
    leftSpeedFactor = 1.0;
    rightSpeedFactor = 1.0;
    noWalls = false;
  }

  /**
   * Sets the difficulty multiplier for ball speed.
   *
   * @param multiplier the difficulty multiplier
   */
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

  public double getDifficultyMultiplier() {
    return difficultyMultiplier;
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

  /**
   * Launches the ball in the specified horizontal direction.
   *
   * @param horizontalDirection 1 for right, -1 for left
   */
  public void launchBall(int horizontalDirection) {
    double startX = FIELD_WIDTH / 2.0;
    double startY = FIELD_HEIGHT / 2.0;
    double speed = getBaseBallSpeed();
    double vx = horizontalDirection * speed;
    double vy = speed * (random.nextDouble() * 0.6 - 0.3);
    ball = new Ball(startX, startY, vx, vy);
  }

  /**
   * Spawns a secondary ball traveling in the opposite direction.
   */
  public void spawnSecondaryBall() {
    if (ball != null && secondaryBall == null) {
      secondaryBall = new Ball(ball.posX, ball.posY, -ball.vx, -ball.vy);
    }
  }

  public void removeSecondaryBall() {
    secondaryBall = null;
  }

  /**
   * Moves the specified paddle in the given direction.
   *
   * @param left         true for left paddle, false for right paddle
   * @param direction    movement direction (positive = down, negative = up)
   * @param deltaSeconds time elapsed in seconds
   */
  public void movePaddle(boolean left, double direction, double deltaSeconds) {
    if (direction == 0 || deltaSeconds <= 0) {
      return;
    }
    double modifier = left ? leftSpeedFactor : rightSpeedFactor;
    // Paddle speed doesnt necessarily need to scale with difficulty, but could.
    double speed = basePaddleSpeed * modifier * deltaSeconds;
    double delta = direction * speed;
    double heightFactor = left ? leftHeightFactor : rightHeightFactor;
    double currentHeight = PADDLE_HEIGHT * heightFactor;
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
    if (scoreEvent != 0) {
      return scoreEvent;
    }

    if (secondaryBall != null) {
      scoreEvent = updateBall(secondaryBall, deltaSeconds);
    }
    return scoreEvent;
  }

  private int updateBall(Ball current, double deltaSeconds) {
    if (current == null) {
      return 0;
    }

    double dt = deltaSeconds;
    current.posX += current.vx * dt;
    current.posY += current.vy * dt;

    handleWallBounce(current);
    handlePaddleBounce(current);

    return checkScoring(current);
  }

  // Wall Collision / Wrap‑Around
  private void handleWallBounce(Ball current) {
    double top = BALL_RADIUS;
    double bottom = FIELD_HEIGHT - BALL_RADIUS;
    if (noWalls) {
      if (current.posY < top) {
        current.posY = bottom;
      } else if (current.posY > bottom) {
        current.posY = top;
      }
      return;
    }
    if (current.posY <= top && current.vy < 0) {
      current.posY = top;
      current.vy = -current.vy;
      collisionOccurred = true;
    } else if (current.posY >= bottom && current.vy > 0) {
      current.posY = bottom;
      current.vy = -current.vy;
      collisionOccurred = true;
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
    if (current.vx < 0 && current.posX - BALL_RADIUS <= leftPaddleRight && current.posY >= leftTop
        && current.posY <= leftBottom) {
      current.posX = leftPaddleRight + BALL_RADIUS;
      bounceFromPaddle(current, +1, paddle1CenterY, PADDLE_HEIGHT * leftHeightFactor);
      bounced = true;
      collisionOccurred = true;
    } else if (current.vx > 0 && current.posX + BALL_RADIUS >= rightPaddleLeft
        && current.posY >= rightTop && current.posY <= rightBottom) {
      current.posX = rightPaddleLeft - BALL_RADIUS;
      bounceFromPaddle(current, -1, paddle2CenterY, PADDLE_HEIGHT * rightHeightFactor);
      bounced = true;
      collisionOccurred = true;
    }
    return bounced;
  }

  private void bounceFromPaddle(Ball current, int horizontalDirection,
      double paddleCenterY, double paddleHeight) {
    double relativeIntersect = (current.posY - paddleCenterY) / (paddleHeight / 2.0);

    // Increase rally hit count and apply speed boost
    rallyHitCount++;
    double currentRallyMultiplier = Math.min(maxRallyMultiplier,
            1.0 + (rallyHitCount * rallySpeedIncrease));

    double speed = getBaseBallSpeed() * currentRallyMultiplier;
    current.vx = horizontalDirection * speed;
    current.vy = relativeIntersect * speed;
  }

  /**
   * Scales the velocity of all active balls by the given factor.
   *
   * @param factor the scaling factor to apply
   */
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

  /**
   * Sets the velocity of a specific ball.
   *
   * @param ballToSet the ball to modify
   * @param vx        the horizontal velocity
   * @param vy        the vertical velocity
   */
  public void setBallVelocity(Ball ballToSet, double vx, double vy) {
    if (ballToSet != null) {
      ballToSet.vx = vx;
      ballToSet.vy = vy;
    }
  }

  // Calculate scoring - ball must completely leave visible field
  private static final double SCORING_MARGIN = 50.0; // Extra distance ball must travel off-screen

  private int checkScoring(Ball current) {
    if (current.posX + BALL_RADIUS < -SCORING_MARGIN) {
      rallyHitCount = 0; // Reset rally on score
      return 2; // Player 2 scores (ball went out left)
    } else if (current.posX - BALL_RADIUS > FIELD_WIDTH + SCORING_MARGIN) {
      rallyHitCount = 0; // Reset rally on score
      return 1; // Player 1 scores (ball went out right)
    }
    return 0;
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  // Getters and Setters for state
  public PaddleState getLeftPaddleState() {
    double leftHeight = PADDLE_HEIGHT * leftHeightFactor;
    return new PaddleState(LEFT_PADDLE_X, paddle1CenterY, PADDLE_WIDTH, leftHeight);
  }

  public PaddleState getRightPaddleState() {
    double rightHeight = PADDLE_HEIGHT * rightHeightFactor;
    return new PaddleState(RIGHT_PADDLE_X, paddle2CenterY, PADDLE_WIDTH, rightHeight);
  }

  // Helper for AI
  public double getPaddle2Y() {
    return paddle2CenterY;
  }

  /**
   * Sets the Y position of the right paddle (for AI control).
   *
   * @param y the new Y position
   */
  public void setPaddle2Y(double y) {
    double currentHeight = PADDLE_HEIGHT * rightHeightFactor;
    double minY = currentHeight / 2.0;
    double maxY = FIELD_HEIGHT - currentHeight / 2.0;
    this.paddle2CenterY = clamp(y, minY, maxY);
  }

  public int getRallyHitCount() {
    return rallyHitCount;
  }

  /**
   * Returns the states of all active balls.
   *
   * @return list of ball states
   */
  public List<BallState> getBallStates() {
    List<BallState> balls = new ArrayList<>();
    if (ball != null) {
      balls.add(new BallState(ball.posX, ball.posY, BALL_RADIUS));
    }
    if (secondaryBall != null) {
      balls.add(new BallState(secondaryBall.posX, secondaryBall.posY, BALL_RADIUS));
    }
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

  /**
   * Represents a ball in the physics simulation.
   */
  public static class Ball {
    /** The ball's X position. */
    public double posX;
    /** The ball's Y position. */
    public double posY;
    /** The ball's horizontal velocity. */
    public double vx;
    /** The ball's vertical velocity. */
    public double vy;

    /**
     * Creates a new ball with the given position and velocity.
     *
     * @param posX initial X position
     * @param posY initial Y position
     * @param vx   initial horizontal velocity
     * @param vy   initial vertical velocity
     */
    public Ball(double posX, double posY, double vx, double vy) {
      this.posX = posX;
      this.posY = posY;
      this.vx = vx;
      this.vy = vy;
    }
  }

  /**
   * Predicts the Y position of the ball when it reaches a specific X coordinate.
   * Useful for AI to anticipate where to move the paddle.
   *
   * @param targetX The X position where we want to know the ball's Y (e.g.,
   *                paddle X).
   * @param b       The ball to predict for.
   * @return The predicted Y position, clamping for wall bounces is approximated.
   */
  public double predictBallY(double targetX, Ball b) {
    if (b == null || b.vx == 0) {
      return FIELD_HEIGHT / 2.0;
    }

    double timeToTarget = (targetX - b.posX) / b.vx;
    if (timeToTarget < 0) {
      return FIELD_HEIGHT / 2.0; // Moving away
    }

    double predictedY = b.posY + b.vy * timeToTarget;

    // Handle wall bounces (simplified reflection logic)
    // This math effectively "folds" the coordinate space so top/bottom walls mirror
    // it.
    // It's a standard trick for pong AI.
    if (!noWalls) {
      double effectiveHeight = FIELD_HEIGHT - 2 * BALL_RADIUS;
      // Shift to 0-based relative to effective playing area (radius offset)
      double relativeY = predictedY - BALL_RADIUS;

      // How many times does it bounce?
      // Java's % can be negative, so we use a custom mod or careful math.
      // But a simpler iterative approach or abs() logic works for typical Pong.
      // Let's use the absolute-remainder reflection:
      // The position oscillates between 0 and effectiveHeight.

      // This formula wraps 'val' into range [0, max] with "ping-pong" wrapping
      // (0->max->0->max...)
      // formula: y = abs( (val % 2max) - max ) ? No, that's 0->max->0 for triangle
      // wave center at max?
      // Easier:
      double cycle = 2 * effectiveHeight;
      double mod = (relativeY % cycle + cycle) % cycle; // clean positive modulus

      if (mod > effectiveHeight) {
        mod = cycle - mod;
      }

      predictedY = mod + BALL_RADIUS;
    }

    return predictedY;
  }
}
