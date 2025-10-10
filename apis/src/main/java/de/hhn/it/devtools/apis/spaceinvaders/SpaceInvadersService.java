package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.exceptions.IllegalConfigurationException;

/**
 * Interface of a simple SpaceInvadersService.
 */
public interface SpaceInvadersService {
  int DEFAULT_SIZE = 20;
  int DEFAULT_DELAY = 1;
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
  void start() throws IllegalStateException;

  /**
   * Aborts the game if the GameState is RUNNING or PAUSED. Switch GameState = ABORTED.
   *
   * @throws IllegalStateException if the GameState is PREPARED
   */
  void abort() throws IllegalStateException;

  /**
   * Pause the game if the GameState is RUNNING. Switch GameState = PAUSED.
   *
   * @throws IllegalStateException if the GameState is not RUNNING
   */
  void pause() throws IllegalStateException;

  /**
   * Resumes the game if the GameState. Switch GameState = RUNNING.
   *
   * @throws IllegalStateException is the GameState is not PAUSED
   */
  void resume() throws IllegalStateException;

  /**
   * Reset the game field if all aliens are destroyed, and increase the wave number.
   *
   * @throws IllegalStateException is the GameState is not RUNNING
   */
  void nextRound() throws IllegalStateException;

  /**
   * Change the Spaceship direction by turning left if the GameState is RUNNING.
   *
   * @throws IllegalStateException is the GameState is not appropriate
   */
  void move(Direction direction) throws IllegalStateException;

  /**
   * Shoot Projectile from Spaceship.
   *
   * @throws IllegalStateException is the GameState is not appropriate
   */
  void shoot() throws IllegalStateException;

  /**
   * Plays a sound with the given name.
   *
   * @param sound the name of the sound to play
   */
  void playSound(String sound) throws IllegalStateException;

  /**
   * Adds a listener for game updates.
   *
   * @param listener listener to be added
   * @return true if the listener could be added. Otherwise, false.
   */
  boolean addListener(SpaceInvadersListener listener);

  /**
   * Removes a listener from the list of listeners.
   *
   * @param listener listener to be removed.
   * @return true if the listener could be removed. Otherwise, false.
   */
  boolean removeListener(SpaceInvadersListener listener);

  /**
   * Configures the next game if GameState is PREPARED or ABORTED.
   *
   * @param configuration contains the game configuration parameters
   * @throws IllegalConfigurationException if the values cannot be accepted due whatever reason
   * @throws IllegalStateException if the game is not in the assumed GameState
   */
  void configure(GameConfiguration configuration) throws IllegalStateException,
          IllegalConfigurationException;

  /**
   * Returns the actual configuration.
   *
   * @return actual GameConfiguration
   */
  GameConfiguration getConfiguration();

}
