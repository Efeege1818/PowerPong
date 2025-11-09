package de.hhn.it.devtools.components.powerPong;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongListener;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.apis.powerPong.PowerUpState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import de.hhn.it.devtools.apis.powerPong.Score;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.input.KeyCode;

/**
 * Local implementation of {@link PowerPongService}. Classic mode keeps the rules light-weight,
 * Power-Up Duel activates deterministic power-up spawning and time-limited effects so the UI can
 * demonstrate the full feature set without external dependencies.
 */
public class PowerPongMatchEngine implements PowerPongService {

  private static final double FIELD_WIDTH = 900.0;
  private static final double FIELD_HEIGHT = 520.0;
  private static final double PADDLE_WIDTH = 14.0;
  private static final double PADDLE_HEIGHT = 110.0;
  private static final double BALL_RADIUS = 8.0;
  private static final double BASE_PADDLE_SPEED = 400.0; // px / second
  private static final double BASE_BALL_SPEED = 360.0;   // px / second
  private static final double FRAME_TIME_SECONDS = 1.0 / 60.0;
  private static final double LEFT_PADDLE_X = 35.0;
  private static final double RIGHT_PADDLE_X = FIELD_WIDTH - LEFT_PADDLE_X - PADDLE_WIDTH;
  private static final int TARGET_SCORE = 5;
  private static final double POWERUP_RADIUS = 16.0;
  private static final double POWERUP_SPAWN_INTERVAL = 8.0;
  private static final double EFFECT_DURATION_SECONDS = 8.0;
  private static final double ENLARGE_FACTOR = 1.4;
  private static final double SHRINK_FACTOR = 0.65;
  private static final double SLOW_FACTOR = 0.5;
  private static final double FAST_BALL_FACTOR = 1.35;

  private final Random random;
  private final List<PowerPongListener> listeners = new CopyOnWriteArrayList<>();

  private GameMode currentMode = GameMode.CLASSIC_DUEL;
  private GameStatus status = GameStatus.MENU;
  private Score score = new Score(0, 0);
  private boolean running;
  private boolean paused;
  private Ball ball;
  private Ball secondaryBall;
  private boolean noWalls;
  private boolean leftShield;
  private boolean rightShield;
  private double paddle1CenterY = FIELD_HEIGHT / 2.0;
  private double paddle2CenterY = FIELD_HEIGHT / 2.0;
  private double leftHeightFactor = 1.0;
  private double rightHeightFactor = 1.0;
  private double leftSpeedFactor = 1.0;
  private double rightSpeedFactor = 1.0;
  private GameState snapshot = buildSnapshot();
  private final List<FieldPowerUp> powerUps = new ArrayList<>();
  private final List<ActiveEffect> activeEffects = new ArrayList<>();
  private double spawnTimer;

  public PowerPongMatchEngine() {
    this(new Random());
  }

  PowerPongMatchEngine(Random randomGenerator) {
    this.random = Objects.requireNonNull(randomGenerator, "randomGenerator must not be null");
  }

  @Override
  public void startGame(GameMode mode) throws GameLogicException {
    if (mode == null) {
      throw new GameLogicException("Game mode must not be null.");
    }
    currentMode = mode;
    score = new Score(0, 0);
    running = true;
    paused = false;
    status = GameStatus.RUNNING;
    paddle1CenterY = FIELD_HEIGHT / 2.0;
    paddle2CenterY = FIELD_HEIGHT / 2.0;
    launchBall(random.nextBoolean() ? 1 : -1);
    secondaryBall = null;
    activeEffects.clear();
    powerUps.clear();
    spawnTimer = 0;
    noWalls = false;
    leftShield = false;
    rightShield = false;
    leftHeightFactor = 1.0;
    rightHeightFactor = 1.0;
    leftSpeedFactor = 1.0;
    rightSpeedFactor = 1.0;
    rebuildSnapshot();
  }

  @Override
  public void updateGame(PlayerInput input) throws GameLogicException {
    ensureGameRunning();
    if (paused || ball == null) {
      return;
    }
    applyInput(input);
    advanceBalls();
    updateEffects();
    if (currentMode == GameMode.POWERUP_DUEL) {
      spawnTimer += FRAME_TIME_SECONDS;
      maybeSpawnPowerUp();
      handlePowerUpCollisions();
    }
    rebuildSnapshot();
  }

  @Override
  public GameState getGameState() {
    return snapshot;
  }

  @Override
  public void setPaused(boolean isPaused) {
    if (!running) {
      return;
    }
    paused = isPaused;
    status = isPaused ? GameStatus.PAUSED : GameStatus.RUNNING;
    rebuildSnapshot();
  }

  @Override
  public void endGame() {
    running = false;
    paused = false;
    status = GameStatus.MENU;
    ball = null;
    secondaryBall = null;
    powerUps.clear();
    activeEffects.clear();
    rebuildSnapshot();
  }

  @Override
  public void addListener(PowerPongListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  @Override
  public void removeListener(PowerPongListener listener) {
    listeners.remove(listener);
  }

  private void ensureGameRunning() throws GameLogicException {
    if (!running) {
      throw new GameLogicException("Game has not been started.");
    }
    if (status == GameStatus.PLAYER_1_WINS || status == GameStatus.PLAYER_2_WINS) {
      throw new GameLogicException("Game already finished.");
    }
  }

  private void applyInput(PlayerInput input) {
    double leftDir = directionFromInput(input, KeyCode.W, KeyCode.S);
    double rightDir = directionFromInput(input, KeyCode.UP, KeyCode.DOWN);
    movePaddle(true, leftDir, FRAME_TIME_SECONDS);
    movePaddle(false, rightDir, FRAME_TIME_SECONDS);
  }

  private double directionFromInput(PlayerInput input, KeyCode up, KeyCode down) {
    if (input == null) {
      return 0;
    }
    boolean upPressed = input.isPressed(up);
    boolean downPressed = input.isPressed(down);
    if (upPressed == downPressed) {
      return 0;
    }
    return upPressed ? -1 : 1;
  }

  private void movePaddle(boolean left, double direction, double deltaSeconds) {
    if (!running || paused || direction == 0 || deltaSeconds <= 0) {
      return;
    }
    double modifier = left ? leftSpeedFactor : rightSpeedFactor;
    double speed = BASE_PADDLE_SPEED * modifier * deltaSeconds;
    double delta = direction * speed;
    double minY = PADDLE_HEIGHT / 2.0;
    double maxY = FIELD_HEIGHT - PADDLE_HEIGHT / 2.0;
    if (left) {
      paddle1CenterY = clamp(paddle1CenterY + delta, minY, maxY);
    } else {
      paddle2CenterY = clamp(paddle2CenterY + delta, minY, maxY);
    }
  }

  private void advanceBalls() {
    advanceBall(ball);
    if (secondaryBall != null) {
      advanceBall(secondaryBall);
    }
  }

  private void advanceBall(Ball current) {
    if (current == null) {
      return;
    }
    applyFastBallModifier(current);
    double dt = FRAME_TIME_SECONDS;
    current.x += current.vx * dt;
    current.y += current.vy * dt;
    handleWallBounce(current);
    handlePaddleBounce(current);
    handleScoring(current);
  }

  private void updateEffects() {
    if (activeEffects.isEmpty()) {
      return;
    }
    Iterator<ActiveEffect> iterator = activeEffects.iterator();
    while (iterator.hasNext()) {
      ActiveEffect effect = iterator.next();
      effect.remaining -= FRAME_TIME_SECONDS;
      if (effect.remaining <= 0) {
        revertEffect(effect);
        iterator.remove();
      }
    }
  }

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

  private void handlePaddleBounce(Ball current) {
    double leftPaddleRight = LEFT_PADDLE_X + PADDLE_WIDTH;
    double rightPaddleLeft = RIGHT_PADDLE_X;
    double leftTop = paddle1CenterY - (PADDLE_HEIGHT * leftHeightFactor) / 2.0;
    double leftBottom = paddle1CenterY + (PADDLE_HEIGHT * leftHeightFactor) / 2.0;
    double rightTop = paddle2CenterY - (PADDLE_HEIGHT * rightHeightFactor) / 2.0;
    double rightBottom = paddle2CenterY + (PADDLE_HEIGHT * rightHeightFactor) / 2.0;

    if (current.vx < 0 && current.x - BALL_RADIUS <= leftPaddleRight
        && current.y >= leftTop && current.y <= leftBottom) {
      current.x = leftPaddleRight + BALL_RADIUS;
      bounceFromPaddle(current, +1);
    } else if (current.vx > 0 && current.x + BALL_RADIUS >= rightPaddleLeft
        && current.y >= rightTop && current.y <= rightBottom) {
      current.x = rightPaddleLeft - BALL_RADIUS;
      bounceFromPaddle(current, -1);
    }
  }

  private void bounceFromPaddle(Ball current, int horizontalDirection) {
    double paddleHeight = horizontalDirection < 0
        ? PADDLE_HEIGHT * rightHeightFactor
        : PADDLE_HEIGHT * leftHeightFactor;
    double paddleCenter = horizontalDirection < 0 ? paddle2CenterY : paddle1CenterY;
    double relativeIntersect = (current.y - paddleCenter) / (paddleHeight / 2.0);
    current.vx = horizontalDirection * BASE_BALL_SPEED;
    current.vy = relativeIntersect * BASE_BALL_SPEED;
    rebuildSnapshot();
    for (PowerPongListener listener : listeners) {
      listener.onBallCollision(snapshot);
    }
  }

  private void applyFastBallModifier(Ball current) {
    double multiplier = fastBallMultiplierFor(current);
    if (multiplier <= 1.0) {
      return;
    }
    double speed = Math.hypot(current.vx, current.vy);
    if (speed == 0) {
      return;
    }
    double target = BASE_BALL_SPEED * multiplier;
    double factor = target / speed;
    current.vx *= factor;
    current.vy *= factor;
  }

  private double fastBallMultiplierFor(Ball current) {
    if (current == null) {
      return 1.0;
    }
    double half = FIELD_WIDTH / 2.0;
    for (ActiveEffect effect : activeEffects) {
      if (effect.type == PowerUpType.FASTER_BALL_ENEMY_SIDE) {
        if (effect.owner == 1 && current.x > half) {
          return FAST_BALL_FACTOR;
        }
        if (effect.owner == 2 && current.x < half) {
          return FAST_BALL_FACTOR;
        }
      }
    }
    return 1.0;
  }

  private void handleScoring(Ball current) {
    if (current == null) {
      return;
    }
    if (current.x + BALL_RADIUS < 0) {
      if (leftShield) {
        leftShield = false;
        resetAfterShield();
        return;
      }
      registerScore(2, current);
    } else if (current.x - BALL_RADIUS > FIELD_WIDTH) {
      if (rightShield) {
        rightShield = false;
        resetAfterShield();
        return;
      }
      registerScore(1, current);
    }
  }

  private void registerScore(int scoringPlayer, Ball current) {
    int left = score.player1();
    int right = score.player2();
    if (scoringPlayer == 1) {
      left++;
      status = GameStatus.PLAYER_1_SCORED;
    } else {
      right++;
      status = GameStatus.PLAYER_2_SCORED;
    }
    score = new Score(left, right);
    rebuildSnapshot();
    for (PowerPongListener listener : listeners) {
      listener.onPlayerScored(scoringPlayer, score);
    }
    if (left >= TARGET_SCORE || right >= TARGET_SCORE) {
      status = left >= TARGET_SCORE ? GameStatus.PLAYER_1_WINS : GameStatus.PLAYER_2_WINS;
      running = false;
      paused = false;
      rebuildSnapshot();
      for (PowerPongListener listener : listeners) {
        listener.onGameEnd(status, snapshot);
      }
      return;
    }
    if (current == secondaryBall) {
      secondaryBall = null;
    }
    launchBall(scoringPlayer == 1 ? -1 : 1);
    status = GameStatus.RUNNING;
    rebuildSnapshot();
  }

  private void resetAfterShield() {
    clearActiveEffects();
    launchBall(random.nextBoolean() ? 1 : -1);
    status = GameStatus.RUNNING;
    rebuildSnapshot();
  }

  private void clearActiveEffects() {
    activeEffects.clear();
    noWalls = false;
    leftHeightFactor = 1.0;
    rightHeightFactor = 1.0;
    leftSpeedFactor = 1.0;
    rightSpeedFactor = 1.0;
    secondaryBall = null;
  }

  private void revertEffect(ActiveEffect effect) {
    switch (effect.type) {
      case BIGGER_PADDLE -> {
        if (effect.owner == 1) {
          leftHeightFactor = 1.0;
        } else {
          rightHeightFactor = 1.0;
        }
      }
      case SMALLER_ENEMY_PADDLE -> {
        if (effect.owner == 1) {
          rightHeightFactor = 1.0;
        } else {
          leftHeightFactor = 1.0;
        }
      }
      case SLOW_ENEMY_PADDLE -> {
        if (effect.owner == 1) {
          rightSpeedFactor = 1.0;
        } else {
          leftSpeedFactor = 1.0;
        }
      }
      case BARRIERLESS -> noWalls = false;
      case DOUBLE_BALL -> secondaryBall = null;
      default -> {
      }
    }
  }

  private void launchBall(int horizontalDirection) {
    double startX = FIELD_WIDTH / 2.0;
    double startY = FIELD_HEIGHT / 2.0;
    double vx = horizontalDirection * BASE_BALL_SPEED;
    double vy = BASE_BALL_SPEED * (random.nextDouble() * 0.6 - 0.3);
    ball = new Ball(startX, startY, vx, vy);
  }

  private GameState buildSnapshot() {
    double leftHeight = PADDLE_HEIGHT * leftHeightFactor;
    double rightHeight = PADDLE_HEIGHT * rightHeightFactor;
    PaddleState left = new PaddleState(paddle1CenterY, leftHeight);
    PaddleState right = new PaddleState(paddle2CenterY, rightHeight);
    List<BallState> balls = new ArrayList<>();
    if (ball != null) {
      balls.add(new BallState(ball.x, ball.y));
    }
    if (secondaryBall != null) {
      balls.add(new BallState(secondaryBall.x, secondaryBall.y));
    }
    List<PowerUpState> powerUpStates = new ArrayList<>();
    for (FieldPowerUp powerUp : powerUps) {
      powerUpStates.add(new PowerUpState(powerUp.x, powerUp.y, powerUp.type));
    }
    return new GameState(
        status,
        left,
        right,
        balls,
        score,
        powerUpStates);
  }

  private void maybeSpawnPowerUp() {
    if (spawnTimer < POWERUP_SPAWN_INTERVAL || powerUps.size() >= 3) {
      return;
    }
    spawnTimer -= POWERUP_SPAWN_INTERVAL;
    double margin = BALL_RADIUS * 2;
    double x = margin + random.nextDouble() * (FIELD_WIDTH - 2 * margin);
    double y = margin + random.nextDouble() * (FIELD_HEIGHT - 2 * margin);
    PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];
    powerUps.add(new FieldPowerUp(x, y, type));
  }

  private void handlePowerUpCollisions() {
    if (powerUps.isEmpty()) {
      return;
    }
    List<FieldPowerUp> collected = new ArrayList<>();
    for (FieldPowerUp powerUp : powerUps) {
      if (collides(ball, powerUp)) {
        int owner = ball.x < FIELD_WIDTH / 2.0 ? 1 : 2;
        applyPowerUp(owner, powerUp.type);
        collected.add(powerUp);
      } else if (secondaryBall != null && collides(secondaryBall, powerUp)) {
        int owner = secondaryBall.x < FIELD_WIDTH / 2.0 ? 1 : 2;
        applyPowerUp(owner, powerUp.type);
        collected.add(powerUp);
      }
    }
    powerUps.removeAll(collected);
  }

  private boolean collides(Ball candidate, FieldPowerUp powerUp) {
    if (candidate == null) {
      return false;
    }
    double dx = candidate.x - powerUp.x;
    double dy = candidate.y - powerUp.y;
    double radius = BALL_RADIUS + POWERUP_RADIUS;
    return dx * dx + dy * dy <= radius * radius;
  }

  private void applyPowerUp(int owner, PowerUpType type) {
    switch (type) {
      case BIGGER_PADDLE -> {
        if (owner == 1) {
          leftHeightFactor = ENLARGE_FACTOR;
        } else {
          rightHeightFactor = ENLARGE_FACTOR;
        }
        addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
      }
      case SMALLER_ENEMY_PADDLE -> {
        if (owner == 1) {
          rightHeightFactor = SHRINK_FACTOR;
        } else {
          leftHeightFactor = SHRINK_FACTOR;
        }
        addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
      }
      case SLOW_ENEMY_PADDLE -> {
        if (owner == 1) {
          rightSpeedFactor = SLOW_FACTOR;
        } else {
          leftSpeedFactor = SLOW_FACTOR;
        }
        addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
      }
      case BARRIERLESS -> {
        noWalls = true;
        addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
      }
      case DOUBLE_BALL -> {
        if (secondaryBall == null && ball != null) {
          secondaryBall = new Ball(ball.x, ball.y, -ball.vx, -ball.vy);
        }
        addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
      }
      case FASTER_BALL_ENEMY_SIDE -> addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
      case SHIELD -> {
        if (owner == 1) {
          leftShield = true;
        } else {
          rightShield = true;
        }
      }
    }
  }

  private void addTimedEffect(PowerUpType type, int owner, double duration) {
    Iterator<ActiveEffect> iterator = activeEffects.iterator();
    while (iterator.hasNext()) {
      ActiveEffect running = iterator.next();
      if (running.type == type && running.owner == owner) {
        iterator.remove();
        break;
      }
    }
    activeEffects.add(new ActiveEffect(type, owner, duration));
  }

  private void rebuildSnapshot() {
    snapshot = buildSnapshot();
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  private static final class Ball {
    double x;
    double y;
    double vx;
    double vy;

    Ball(double x, double y, double vx, double vy) {
      this.x = x;
      this.y = y;
      this.vx = vx;
      this.vy = vy;
    }
  }

  private static final class FieldPowerUp {
    final double x;
    final double y;
    final PowerUpType type;

    FieldPowerUp(double x, double y, PowerUpType type) {
      this.x = x;
      this.y = y;
      this.type = type;
    }
  }

  private static final class ActiveEffect {
    final PowerUpType type;
    final int owner;
    double remaining;

    ActiveEffect(PowerUpType type, int owner, double duration) {
      this.type = type;
      this.owner = owner;
      this.remaining = duration;
    }
  }
}
