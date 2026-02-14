package de.hhn.it.devtools.components.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.exceptions.IllegalConfigurationException;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.utils.Constants;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple implementation of SpaceInvadersService.
 */
public class SimpleSpaceInvadersService implements SpaceInvadersService {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleSpaceInvadersService.class);
  private final List<SpaceInvadersListener> listeners = new ArrayList<>();
  private GameConfiguration gameConfiguration;
  private SimpleGameLoop simpleGameLoop;
  private GameState gameState;
  private int round = 1;
  boolean alienMoveAllowed = true;
  public int score = 0;
  private EntityProvider entityProvider;
  public boolean test = false;

  /**
   * Default constructor. Load default values for configuration.
   */
  public SimpleSpaceInvadersService() {
    this.gameConfiguration = new GameConfiguration(3, Difficulty.NORMAL);
    this.gameState = GameState.PREPARED;
  }

  @Override
  public void reset() {
    logger.debug("Service Reset");
    gameState = GameState.PREPARED;
    simpleGameLoop.stopGame();
  }

  @Override
  public void start() throws IllegalStateException {
    logger.debug("Service Start");
    checkIfGameStateIsLegal(GameState.PREPARED);
    entityProvider = new EntityProvider(this);
    simpleGameLoop = new SimpleGameLoop(this);
    simpleGameLoop.start();
    gameState = GameState.RUNNING;
    notifyListeners((l) -> l.changedGameState(gameState));
    notifyListeners((l) -> l.updateShip(entityProvider.getPlayer().getImmutableShip()));
    entityProvider.getBarriers().values().forEach(simpleBarrier -> notifyListeners(
            spaceInvadersListener -> spaceInvadersListener.updateBarrier(
                    simpleBarrier.getImmutableBarrier())));
  }

  @Override
  public void abort() throws IllegalStateException {
    logger.debug("Service Abort");
    this.gameState = GameState.ABORTED;
    entityProvider = null;
    if (simpleGameLoop != null) {
      simpleGameLoop.stopGame();
    }
    notifyListeners((l) -> {
      l.changedGameState(gameState);
      l.gameEnded();
    });
  }

  @Override
  public void pause() throws IllegalStateException {
    logger.debug("Service Pause");
    checkIfGameStateIsLegal(GameState.RUNNING);
    this.gameState = GameState.PAUSED;
    if (entityProvider.getAliens().isEmpty()) {
      notifyListeners(l -> l.updateScore(score += Constants.ROUND_ENDING_POINTS * round));
    }
    notifyListeners((l) -> l.changedGameState(gameState));
  }

  @Override
  public void resume() throws IllegalStateException {
    logger.debug("Service Resume");
    checkIfGameStateIsLegal(GameState.PAUSED);
    this.gameState = GameState.RUNNING;
    synchronized (simpleGameLoop) {
      this.simpleGameLoop.notify();
    }
    notifyListeners((l) -> l.changedGameState(gameState));
  }

  @Override
  public void nextRound() throws IllegalStateException {
    logger.debug("Service NextRound");
    checkIfGameStateIsLegal(GameState.PAUSED);
    this.gameState = GameState.RUNNING;
    notifyListeners((l) -> l.changedGameState(gameState));
    notifyListeners((l) -> l.updateRound(++round));
    entityProvider.generateAliens();
    entityProvider.clearProjectiles();
    synchronized (simpleGameLoop) {
      this.simpleGameLoop.notify();
    }
  }

  @Override
  public void move(Direction direction) throws IllegalStateException {
    logger.debug("Service Move");
    entityProvider.getPlayer().move(direction);
    notifyListeners(spaceInvadersListener -> spaceInvadersListener
            .updateShip(entityProvider.getPlayer().getImmutableShip()));
  }

  @Override
  public void shoot() throws IllegalStateException {
    logger.debug("Service Shoot");
    entityProvider.shootPlayer();
  }

  @Override
  public void playSound(Sound sound) throws IllegalStateException {
    logger.debug("Service PlaySound");
    notifyListeners((l) -> l.updateSound(sound));
  }

  @Override
  public boolean addListener(SpaceInvadersListener listener) {
    logger.debug("Service Add Listener");
    return this.listeners.add(listener);
  }

  @Override
  public boolean removeListener(SpaceInvadersListener listener) {
    logger.debug("Service Remove Listener");
    return this.listeners.remove(listener);
  }

  @Override
  public void configure(GameConfiguration configuration) throws IllegalStateException,
          IllegalConfigurationException {
    logger.debug("Service Configure");
    if (configuration == null) {
      throw new IllegalConfigurationException("Configuration is null");
    }
    this.gameConfiguration = configuration;
    logger.info("configure: {}", this.gameConfiguration);
  }

  @Override
  public GameConfiguration getConfiguration() {
    logger.debug("Service Get Configuration");
    return gameConfiguration;
  }

  /**
   * Method to trigger listeners.
   *
   * @param listener triggered callback.
   */
  public void notifyListeners(Consumer<SpaceInvadersListener> listener) {
    for (SpaceInvadersListener l : listeners) {
      listener.accept(l);
      logger.trace("Listener {} triggered with callback {}", l, listener);
    }
  }

  /**
   * Checks if the required GameState is given.
   *
   * @param gameState required game state.
   */
  private void checkIfGameStateIsLegal(GameState gameState) {
    if (!this.gameState.equals(gameState)) {
      throw new IllegalStateException(
              "Game state is not equal to current game. Required state is " + gameState
                      + ". Current state is " + this.gameState);
    }
  }

  /**
   * This method gets triggered every loop by SimpleGameLoop.
   */
  public void triggeredByGameLoop() {
    if (entityProvider == null) {
      logger.debug("Something went wrong; no EntityProvider");
      return;
    }
    if (entityProvider.getAliens().isEmpty()) {
      notifyListeners(l -> l.updateSound(Sound.LEVELUP));
      pause();
    }

    notifyListeners(spaceInvadersListener -> spaceInvadersListener
            .updateAliens(entityProvider
                    .getAliens()
                    .values()
                    .stream()
                    .map(SimpleAlien::immutableAlien).toArray(Alien[]::new)));
    entityProvider.updateProjectiles();
    entityProvider.checkCollision();
    entityProvider.shootAliens();

    if (alienMoveAllowed) {
      entityProvider.updateAliens();
      alienMoveAllowed = false;
    } else {
      alienMoveAllowed = true;
    }

    if (entityProvider.getPlayer().getHitPoints() <= 0) {
      notifyListeners(l -> l.updateSound(Sound.GAMEOVER));
      abort();
    }

  }

  public int getRound() {
    return round;
  }

  protected GameState getGameState() {
    return gameState;
  }

  public Difficulty getDifficulty() {
    return this.gameConfiguration.difficulty();
  }
}
