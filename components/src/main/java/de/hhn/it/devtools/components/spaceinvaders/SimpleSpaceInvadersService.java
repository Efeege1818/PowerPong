package de.hhn.it.devtools.components.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.apis.spaceinvaders.exceptions.IllegalConfigurationException;
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
  private int round;

  /**
   * Default constructor. Load default values for configuration.
   */
  public SimpleSpaceInvadersService() {
    this.gameConfiguration = new GameConfiguration(20, Difficulty.NORMAL);
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
    simpleGameLoop = new SimpleGameLoop(this);
    simpleGameLoop.start();
    gameState = GameState.RUNNING;
    notifyListeners((l) -> l.changedGameState(gameState));
  }

  @Override
  public void abort() throws IllegalStateException {
    logger.debug("Service Abort");
    checkIfGameStateIsLegal(GameState.PREPARED);
    simpleGameLoop.stopGame();
    notifyListeners((l) -> {
      l.changedGameState(gameState);
      l.gameEnded();
    });
  }

  @Override
  public void pause() throws IllegalStateException {
    logger.debug("Service Pause");
    checkIfGameStateIsLegal(GameState.RUNNING);
    notifyListeners((l) -> l.changedGameState(gameState));
  }

  @Override
  public void resume() throws IllegalStateException {
    logger.debug("Service Resume");
    checkIfGameStateIsLegal(GameState.PAUSED);
    notifyListeners((l) -> l.changedGameState(gameState));
  }

  @Override
  public void nextRound() throws IllegalStateException {
    logger.debug("Service NextRound");
    checkIfGameStateIsLegal(GameState.RUNNING);
    notifyListeners((l) -> l.updateRound(round));
  }

  @Override
  public void move(Direction direction) throws IllegalStateException {
    logger.debug("Service Move");
    //TODO move func
  }

  @Override
  public void shoot() throws IllegalStateException {
    logger.debug("Service Shoot");
    //TODO shoot func
  }

  @Override
  public void playSound(Sound sound) throws IllegalStateException {
    logger.debug("Service PlaySound");
    //TODO play sound
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
    return gameConfiguration;
  }

  /**
   * Method to trigger listeners.
   *
   * @param listener triggered callback.
   */
  private void notifyListeners(Consumer<SpaceInvadersListener> listener) {
    for (SpaceInvadersListener l : listeners) {
      listener.accept(l);
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
  protected void triggeredByGameLoop() {
    //TODO Spiel logik
  }

  protected GameState getGameState() {
    return gameState;
  }
}
