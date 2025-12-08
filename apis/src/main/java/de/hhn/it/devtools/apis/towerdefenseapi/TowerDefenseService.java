package de.hhn.it.devtools.apis.towerdefenseapi;

import java.util.List;
import java.util.Map;

/**
 * Interface for the TowerDefenseSystem.
 */
public interface TowerDefenseService {

  GameState getCurrentGameState();

  /**
   * Adds a listener to the list.
   *
   * @param listener the listener to be added
   * @return true if the listener was successfully added, false otherwise
   * @throws IllegalArgumentException if listener is null
   */
  boolean addListener(TowerDefenseListener listener) throws IllegalArgumentException;

  /**
   * Removes a listener from the list.
   *
   * @param listener the listener to be removed
   * @return true if the listener was successfully removed, false otherwise
   * @throws IllegalArgumentException if listener is null
   */
  boolean removeListener(TowerDefenseListener listener) throws IllegalArgumentException;

  /**
   * Starts a new game session.
   *
   * @throws IllegalStateException if a game is already running
   */
  void startGame() throws IllegalStateException;

  /**
   * Aborts the current game immediately and resets internal states.
   *
   * @throws IllegalStateException if no game is currently active
   */
  void abortGame() throws IllegalStateException;

  /**
   * Resets the game to its initial state.
   * Should clear map, enemies, towers, and player stats.
   */
  void resetGame();

  /**
   * Retries the last failed round.
   *
   * @throws IllegalStateException if there is no failed round to retry
   */
  void retry() throws IllegalStateException;

  /**
   * Called when the player has failed the current round.
   */
  void roundFailed();

  /**
   * Returns the current game map.
   *
   * @return the current Map.
   * @throws IllegalStateException if the game has not been initialized
   */
  Grid getMap() throws IllegalStateException;

  /**
   * Returns the current tower configuration.
   *
   * @return the towerBoard.
   * @throws IllegalStateException if towerBoard is uninitialized
   */
  Map<Coordinates, Tower> getTowerBoard() throws IllegalStateException;

  /**
   * Returns the enemies in this round (wave).
   *
   * @return the enemies in this round.
   * @throws IllegalStateException if there are no enemies active
   */
  List<Enemy> getCurrentEnemies() throws IllegalStateException;

  /**
   * Places a tower on the map at its defined position.
   *
   * @param tower the tower to be placed
   * @throws IllegalArgumentException if tower is null or
   *     placement is invalid (on Path or on other tower).
   */
  void placeTower(Tower tower) throws IllegalArgumentException;

  /**
   * Updates the player's health when damaged.
   *
   * @param health the new health value
   * @throws IllegalArgumentException if health is negative
   */
  void updateHealth(int health) throws IllegalArgumentException;

  /**
   * Updates the player's money when killing enemies or spending on towers.
   *
   * @param money the new money value
   */
  void updateMoney(int money);

  /**
   * Provides the number of the last round that has been started.
   *
   * @return the last round number as a positive integer
   */
  int getCurrentRound();
}