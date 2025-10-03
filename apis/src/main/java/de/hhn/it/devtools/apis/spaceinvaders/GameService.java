package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.exceptions.IllegalConfigurationException;

/**
 * Interface of a simple GameService.
 */
public interface GameService {
  int DEFAULT_SIZE = 20;
  int DEFAULT_VELOCITY = 250;
  int DEFAULT_NUMBER_OF_BARRIER = 1;
  int DEFAULT_PROJECTILE_DELAY = 1;

  /**
   * Reset the game. Switch the GameState to PREPARED.
   */
  void reset();

  /**
   * Start the game if the GameState is PREPARED. Switch GameState = RUNNING
   *
   * @throws IllegalStateException if the GameState is not PREPARED
   */
  void start();

  /**
   * Aborts the game if the GameState is RUNNING or PAUSED. Switch GameState = ABORTED.
   *
   * @throws IllegalStateException if the GameState is PREPARED
   */
  void abort();

  /**
   * Pause the game if the GameState is RUNNING. Switch GameState = PAUSED.
   *
   * @throws IllegalStateException if the GameState is not RUNNING
   */
  void pause();

  /**
   * Resumes the game if the GameState. Switch GameState = RUNNING.
   *
   * @throws IllegalStateException is the GameState is not PAUSED
   */
  void resume();

  /**
   * Change the Spaceship direction by turning left if the GameState is RUNNING.
   *
   * @throws IllegalStateException is the GameState is not appropriate
   */
  void moveLeft();

  /**
   * Change the Spaceship direction by turning right if the GameState is RUNNING.
   *
   * @throws IllegalStateException is the GameState is not appropriate
   */
  void moveRight();

  /**
   * Adds a listener for game updates.
   *
   * @param listener listener to be added
   * @return true if the listener could be added. Otherwise, false.
   */
  boolean addListener(GameListener listener);

  /**
   * Removes a listener from the list of listeners.
   *
   * @param listener listener to be removed.
   * @return true if the listener could be removed. Otherwise, false.
   */
  boolean removeListener(GameListener listener);

  /**
   * Configures the next game if GameState is PREPARED or ABORTED.
   *
   * @param configuration contains the game configuration parameters
   * @throws IllegalConfigurationException if the values cannot be accepted due whatever reason
   * @throws IllegalStateException if the game is not in the assumed GameState
   */
  void configure(GameConfiguration configuration);

  /**
   * Returns the actual configuration.
   *
   * @return actual GameConfiguration
   */
  GameConfiguration getConfiguration();

}
