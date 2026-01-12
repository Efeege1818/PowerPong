package de.hhn.it.devtools.apis.shapesurvivor;
/**
 * Models the capabilities of a ShapeSurvivor game listener.
 */
public interface ShapeSurvivorListener {

  /**
   * Informs the listener that the player has been updated.
   *
   * @param player the updated player with current position, health, and stats
   */
  void updatePlayer(Player player);

  /**
   * Informs the listener when enemies are updated.
   *
   * @param enemies the array of updated enemies
   */
  void updateEnemies(Enemy[] enemies);

  /**
   * Informs the listener when a weapon is updated or activated.
   *
   * @param weapon the updated weapon with its current state
   */
  void updateWeapon(Weapon weapon);

  /**
   * Informs the listener when the player takes damage.
   *
   * @param damageAmount the amount of damage taken
   */
  void playerDamaged(int damageAmount);

  /**
   * Informs the listener when an enemy takes damage.
   *
   * @param enemy the enemy that took damage
   * @param damageAmount the amount of damage dealt
   */
  void enemyDamaged(Enemy enemy, int damageAmount);

  /**
   * Informs the listener when an enemy is killed.
   *
   * @param enemy the enemy that was killed
   * @param experienceGained the experience points gained from the kill
   */
  void enemyKilled(Enemy enemy, int experienceGained);

  /**
   * Informs the listener that the player has leveled up.
   */
  void playerLeveledUp();

  /**
   * Informs the listener that the game state has changed.
   *
   * @param gameState the new state of the game
   */
  void changedGameState(GameState gameState);

  /**
   * Informs the listener about the current remaining time.
   *
   * @param remainingTimeSeconds the remaining time
   */
  void updateRemainingTime(int remainingTimeSeconds);

  /**
   * Informs the listener when a new enemy wave spawns.
   *
   * @param waveNumber the current wave number
   * @param enemyCount the number of enemies in this wave
   */
  void enemyWaveSpawned(int waveNumber, int enemyCount);

  /**
   * Informs the listener that the game has ended.
   *
   * @param victory true if the player survived 15 minutes, false if defeated
   */
  void gameEnded(boolean victory);

  /**
   * Informs the listener when experience points are gained.
   *
   * @param currentExperience the current experience points
   * @param experienceToNextLevel the experience needed for next level
   */
  void updateExperience(int currentExperience, int experienceToNextLevel);

  /**
   * Informs the listener about a newly accepted game configuration.
   *
   * @param configuration the new game configuration
   */
  void updateGameConfiguration(GameConfiguration configuration);
}
