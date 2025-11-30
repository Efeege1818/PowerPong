package de.hhn.it.devtools.components.powerPong.provider;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongListener;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.apis.powerPong.Score;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.input.KeyCode;

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

  public PowerPongMatchEngine() {
    this(new Random());
  }

  PowerPongMatchEngine(Random randomGenerator) {
    this.random = Objects.requireNonNull(randomGenerator, "randomGenerator must not be null");
    this.physics = new PhysicsEngine(this.random);
    this.powerUpManager = new PowerUpManager(this.physics, this.random);
    this.snapshot = buildSnapshot();
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
    powerUpManager.reset();

    physics.launchBall(random.nextBoolean() ? 1 : -1);

    rebuildSnapshot();
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
    double leftDir = directionFromInput(input, KeyCode.W, KeyCode.S);
    double rightDir = 0;

    if (currentMode == GameMode.PLAYER_VS_AI || currentMode == GameMode.SURVIVAL) {
      rightDir = calculateAIMovement();
    } else {
      rightDir = directionFromInput(input, KeyCode.UP, KeyCode.DOWN);
    }

    physics.movePaddle(true, leftDir, FRAME_TIME_SECONDS);
    physics.movePaddle(false, rightDir, FRAME_TIME_SECONDS);
  }

  private double calculateAIMovement() {
    PhysicsEngine.Ball ball = physics.getBall();
    if (ball == null) {
      return 0;
    }

    // Simple AI: Follow the ball's Y position
    // Add some reaction delay or imperfection if desired, but for now perfect
    // tracking (limited by paddle speed)
    double paddleY = physics.getPaddle2Y();
    double ballY = ball.y;

    // Deadzone to prevent jitter
    if (Math.abs(paddleY - ballY) < 10.0) {
      return 0;
    }

    return ballY > paddleY ? 1 : -1;
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
