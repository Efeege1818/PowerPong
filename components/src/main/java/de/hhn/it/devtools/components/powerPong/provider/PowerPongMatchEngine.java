package de.hhn.it.devtools.components.powerPong.provider;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PowerPongListener;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.apis.powerPong.Score;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of PowerPongService using the Facade pattern.
 * Orchestrates the game flow by delegating to PhysicsEngine
 * and PowerUpManager.
 *
 * This class serves as the main entry point for the PowerPong game logic
 * and is responsible for:
 * - Game lifecycle management (start, pause, end)
 * - Coordinating physics and power-up systems
 * - Processing player input
 * - Managing game state and scoring
 * - Notifying listeners of game events
 *
 * The engine supports two game modes:
 * - GameMode.CLASSIC_DUEL - Traditional Pong gameplay
 * - GameMode.POWERUP_DUEL - Pong with power-ups and special effects
 */
public class PowerPongMatchEngine implements PowerPongService {

  private static final double FRAME_TIME_SECONDS = 1.0 / 60.0;
  private static final int TARGET_SCORE = 5;
  private static final double PVP_BALL_SPEED_MULTIPLIER = 0.7;

  private final Random random;
  private final List<PowerPongListener> listeners = new CopyOnWriteArrayList<>();

  private final PhysicsEngine physics;
  private final PowerUpManager powerUpManager;

  private GameMode currentMode = GameMode.CLASSIC_DUEL;
  private GameStatus status = GameStatus.MENU;
  private Score score = new Score(0, 0);
  private boolean running;
  private boolean paused;

  private GameState snapshot;

  // AI difficulty settings
  private double aiReactionDelay = 0.0; // seconds before AI reacts
  private double aiPredictionError = 0.0; // random offset added to target
  private double aiSpeedMultiplier = 1.0; // how fast AI can move (1.0 = normal)
  private double aiReactionTimer = 0.0;
  private double aiCachedTargetY = 300.0;

  // AI random mistakes
  private double aiMistakeChance = 0.0; // chance per update to make a mistake
  private double aiMistakeDuration = 0.0; // how long the mistake lasts
  private double aiCurrentMistakeTimer = 0.0; // countdown for current mistake
  private int aiMistakeType = 0; // 0=none, 1=freeze, 2=wrong direction, 3=hesitate

  public PowerPongMatchEngine() {
    this(new Random());
  }

  PowerPongMatchEngine(Random randomGenerator) {
    this.random = Objects.requireNonNull(randomGenerator, "randomGenerator must not be null");
    this.physics = new PhysicsEngine(this.random);
    this.powerUpManager = new PowerUpManager(this.physics, this.random);
    this.snapshot = buildSnapshot();
  }

  /**
   * Sets AI difficulty level. Call before startGame.
   * AI paddle moves at same speed as player - only intelligence changes.
   * AI is always beatable, even on hard (has minimum reaction time & error).
   * AI also makes random "mistakes" (freezes, wrong direction) based on
   * difficulty.
   * 
   * @param difficulty 0.0 = Easy, 0.5 = Medium, 1.0 = Hard
   */
  public void setAiDifficulty(double difficulty) {
    // Easy: slow reaction, big errors, frequent mistakes
    // Hard: fast reaction, small errors, rare mistakes
    // Paddle speed is ALWAYS the same as player (fair play)
    difficulty = Math.max(0, Math.min(1, difficulty)); // clamp 0-1

    // Reaction delay: 0.5s (easy) to 0.08s (hard) - always some delay!
    this.aiReactionDelay = 0.08 + 0.42 * (1.0 - difficulty);

    // Prediction error: 250px (easy) to 30px (hard) - always some error!
    this.aiPredictionError = 30 + 220 * (1.0 - difficulty);

    // Speed multiplier: always 1.0 (same as player)
    this.aiSpeedMultiplier = 1.0;

    // Random mistakes: 15% (easy) to 2% (hard) chance per reaction cycle
    this.aiMistakeChance = 0.02 + 0.13 * (1.0 - difficulty);

    // Mistake duration: 0.4s (easy) to 0.15s (hard)
    this.aiMistakeDuration = 0.15 + 0.25 * (1.0 - difficulty);

    // Reset cached values
    this.aiReactionTimer = 0;
    this.aiCachedTargetY = PhysicsEngine.FIELD_HEIGHT / 2.0;
    this.aiCurrentMistakeTimer = 0;
    this.aiMistakeType = 0;
  }

  private double survivalTime = 0;
  private static final double SURVIVAL_DIFFICULTY_INTERVAL = 10.0; // Increase difficulty every 10 seconds
  private static final double DIFFICULTY_INCREMENT = 0.1; // +10% speed

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
    survivalTime = 0;

    physics.reset();
    physics.setDifficultyMultiplier(getBallSpeedMultiplier(mode));
    powerUpManager.reset();

    physics.launchBall(random.nextBoolean() ? 1 : -1);

    rebuildSnapshot();
  }

  private double getBallSpeedMultiplier(GameMode mode) {
    if (mode == GameMode.CLASSIC_DUEL || mode == GameMode.POWERUP_DUEL) {
      return PVP_BALL_SPEED_MULTIPLIER;
    }
    return 1.0;
  }

  @Override
  public void updateGame(PlayerInput input) throws GameLogicException {
    ensureGameRunning();
    if (paused || physics.getBall() == null) {
      return;
    }

    applyInput(input);

    if (currentMode == GameMode.SURVIVAL) {
      updateSurvivalMode(FRAME_TIME_SECONDS);
    }

    int scoreEvent = physics.updateBalls(FRAME_TIME_SECONDS);

    if (currentMode == GameMode.POWERUP_DUEL) {
      powerUpManager.update(FRAME_TIME_SECONDS);
    }

    if (scoreEvent != 0) {
      handleScoring(scoreEvent);
    } else {
      rebuildSnapshot();
    }
  }

  private void updateSurvivalMode(double deltaSeconds) {
    survivalTime += deltaSeconds;
    int level = (int) (survivalTime / SURVIVAL_DIFFICULTY_INTERVAL);
    double multiplier = 1.0 + (level * DIFFICULTY_INCREMENT);
    physics.setDifficultyMultiplier(multiplier);
  }

  private void applyInput(PlayerInput input) {
    double leftDir = directionFromInput(input, InputAction.LEFT_UP, InputAction.LEFT_DOWN);
    double rightDir = 0;

    if (currentMode == GameMode.PLAYER_VS_AI || currentMode == GameMode.SURVIVAL) {
      rightDir = calculateAIMovement();
    } else {
      rightDir = directionFromInput(input, InputAction.RIGHT_UP, InputAction.RIGHT_DOWN);
    }

    physics.movePaddle(true, leftDir, FRAME_TIME_SECONDS);
    physics.movePaddle(false, rightDir, FRAME_TIME_SECONDS);
  }

  private double calculateAIMovement() {
    PhysicsEngine.Ball ball = physics.getBall();
    if (ball == null) {
      return 0;
    }

    // Handle ongoing mistakes
    if (aiCurrentMistakeTimer > 0) {
      aiCurrentMistakeTimer -= FRAME_TIME_SECONDS;
      switch (aiMistakeType) {
        case 1: // Freeze - AI stops moving
          return 0;
        case 2: // Wrong direction - AI moves away from ball
          double paddleY = physics.getPaddle2Y();
          return ball.y > paddleY ? -1 : 1; // Opposite of correct direction
        case 3: // Hesitate - AI moves slowly
          return calculateNormalAIMovement(ball) * 0.3;
        default:
          break;
      }
    }

    // AI Logic: Predict where the ball will be
    double targetY;

    // Only update target periodically based on reaction delay
    aiReactionTimer += FRAME_TIME_SECONDS;
    if (aiReactionTimer >= aiReactionDelay) {
      aiReactionTimer = 0;

      // Random chance to make a mistake when updating target
      if (random.nextDouble() < aiMistakeChance && ball.vx > 0) {
        aiCurrentMistakeTimer = aiMistakeDuration;
        aiMistakeType = 1 + random.nextInt(3); // 1, 2, or 3
        // Apply mistake immediately
        switch (aiMistakeType) {
          case 1:
            return 0; // Freeze
          case 2: // Wrong direction
            double py = physics.getPaddle2Y();
            return ball.y > py ? -1 : 1;
          case 3:
            return calculateNormalAIMovement(ball) * 0.3; // Hesitate
        }
      }

      // If ball is moving towards AI (Player 2 is usually Right side, so vx > 0)
      double newTargetY;
      if (ball.vx > 0) {
        newTargetY = physics.predictBallY(PhysicsEngine.RIGHT_PADDLE_X, ball);
        // Add prediction error based on difficulty (calculated once, not every frame)
        newTargetY += (random.nextDouble() - 0.5) * 2 * aiPredictionError;
        // Clamp to field bounds
        newTargetY = Math.max(50, Math.min(PhysicsEngine.FIELD_HEIGHT - 50, newTargetY));
      } else {
        // Idle behavior: Return to center
        newTargetY = PhysicsEngine.FIELD_HEIGHT / 2.0;
      }
      // Smooth transition to new target (reduces jitter)
      aiCachedTargetY = aiCachedTargetY * 0.3 + newTargetY * 0.7;
    }
    targetY = aiCachedTargetY;

    double paddleY = physics.getPaddle2Y();

    // Larger deadzone to prevent jitter (25px instead of 10)
    if (Math.abs(paddleY - targetY) < 25.0) {
      return 0;
    }

    // Return direction at full speed (same as player)
    return targetY > paddleY ? 1 : -1;
  }

  private double calculateNormalAIMovement(PhysicsEngine.Ball ball) {
    double targetY = physics.predictBallY(PhysicsEngine.RIGHT_PADDLE_X, ball);
    double paddleY = physics.getPaddle2Y();
    if (Math.abs(paddleY - targetY) < 10.0)
      return 0;
    return targetY > paddleY ? 1 : -1;
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
    physics.reset();
    powerUpManager.reset();
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

  private double directionFromInput(PlayerInput input, InputAction up, InputAction down) {
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

  private void handleScoring(int scoringPlayer) {
    // Check shields
    if (scoringPlayer == 1 && powerUpManager.hasShield(2)) { // Player 1 scored means ball went out RIGHT (Player 2
      // side)
      powerUpManager.consumeShield(2);
      resetAfterShield();
      return;
    } else if (scoringPlayer == 2 && powerUpManager.hasShield(1)) { // Player 2 scored means ball went out LEFT
      // (Player 1 side)
      powerUpManager.consumeShield(1);
      resetAfterShield();
      return;
    }

    // Actually register score
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

    physics.removeSecondaryBall();
    physics.launchBall(scoringPlayer == 1 ? -1 : 1);
    status = GameStatus.RUNNING;
    rebuildSnapshot();
  }

  private void resetAfterShield() {
    physics.launchBall(random.nextBoolean() ? 1 : -1);
    status = GameStatus.RUNNING;
    rebuildSnapshot();
  }

  private void rebuildSnapshot() {
    snapshot = buildSnapshot();
  }

  private GameState buildSnapshot() {
    return new GameState(
        status,
        physics.getLeftPaddleState(),
        physics.getRightPaddleState(),
        physics.getBallStates(),
        score,
        powerUpManager.getPowerUpStates());
  }
}
