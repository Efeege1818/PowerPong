package de.hhn.it.devtools.apis.towerdefenseapi;

/**
 * Interface for the TowerDefenseSystem.
 */
interface TowerDefenseService {
  int DEFAULT_MAP_SIZE = 10;
  int DEFAULT_PLAYER_HEALTH = 50;
  float DEFAULT_ENEMY_POWER_MULTIPLIER = 1.5f;
  int DEFAULT_MONEY_RATE = 1; // how much money per enemy kill


  public GameState getCurrentGameState();

  /**
   * Adds a listener to the list.
   *
   * @param listener the listener to be added
   * @return true if the listener was successfully added, false otherwise
   * @throws IllegalArgumentException if listener is null
   */
  boolean addListener(TowerDefenseListener listener);

  /**
   * Removes a listener from the list.
   *
   * @param listener the listener to be removed
   * @return true if the listener was successfully removed, false otherwise
   * @throws IllegalArgumentException if listener is null
   */
  boolean removeListener(TowerDefenseListener listener);

  /**
   * Starts a new game session.
   *
   * @throws IllegalStateException if a game is already running
   */
  void startGame();

  /**
   * Aborts the current game immediately and resets internal states.
   *
   * @throws IllegalStateException if no game is currently active
   */
  void abortGame();

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
  void retry();

  /**
   * Called when the player has failed the current round.
   */
  void failed();

  /**
   * Returns the current game map.
   *
   * @return the current Map.
   * @throws IllegalStateException if the game has not been initialized
   */
  Grid getMap();

  /**
   * Returns the current tower configuration.
   *
   * @return the towerBoard.
   * @throws IllegalStateException if towerBoard is uninitialized
   */
  TowerBoard getTowerBoard();

  /**
   * Returns the enemies in this round (wave).
   *
   * @return the enemies in this round.
   * @throws IllegalStateException if there are no enemies active
   */
  EnemyBoard getCurrentEnemies();

  /**
   * Triggers calculation of the next game-tick.
   *
   * @throws IllegalStateException if game is not running
   */
  void triggeredByGameLoop();

  /**
   * Places a tower on the map at its defined position.
   *
   * @param tower the tower to be placed
   * @throws IllegalArgumentException if tower is null or placement is invalid (on Path or on other tower).
   */
  void placeTower(Tower tower);

  /**
   * Updates the player's health when damaged.
   *
   * @param health the new health value
   * @throws IllegalArgumentException if health is negative
   */
  void updateHealth(int health);

  /**
   * Updates the player's money when killing enemies or spending on towers.
   *
   * @param money the new money value
   * @throws IllegalArgumentException if money is negative
   */
  void updateMoney(int money);

  /**
   * Provides the number of the last round that has been started.
   *
   * @return the last round number as a positive integer
   */
  int getCurrentRound();
}