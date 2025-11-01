package de.hhn.it.devtools.components.towerDefenseComponents;

import de.hhn.it.devtools.apis.towerDefenseApis.Coordinates;
import de.hhn.it.devtools.apis.towerDefenseApis.EnemyType;
import de.hhn.it.devtools.components.towerDefenseComponents.Path;

//TODO verify throwables

/**
 * Represents an enemy in the Tower Defense game.
 * <p>
 * Each enemy has a unique ID, a movement speed (how much time between movements),
 * a health value, a path it follows, and a type (e.g. SMALL, MEDIUM, LARGE).
 * Enemies move along their assigned path toward the player's base.
 * <p>
 * This record is immutable — once created, the enemy’s properties cannot change.
 */
public record Enemy(int id, int speed, int health, Path path, EnemyType type) {

  /**
   * Returns the unique identifier of this enemy.
   *
   * @return the enemy ID
   */
  public int getId() {
    return id;
  }

  /**
   * Returns the movement speed of this enemy.
   * A higher value means the time between movements is larger.
   *
   * @return the enemy speed
   */
  public int getSpeed() {
    return speed;
  }

  /**
   * Returns the current health of this enemy.
   *
   * @return the enemy's remaining health
   */
  public int getHealth() {
    return health;
  }

  /**
   * Returns the path this enemy follows to reach the player's base.
   *
   * @return the Path assigned to the enemy
   */
  public Path getPath() {
    return path;
  }

  /**
   * Returns the type of this enemy, e.g. SMALL, MEDIUM, or LARGE.
   *
   * @return the EnemyType of the enemy
   */
  public EnemyType getType() {
    return type;
  }

  /**
   * Reduces the health of the enemy by the given amount and markes the enemy as dead,
   * if the health has reached zero.
   *
   * @param amount the amount of health the enemy's health should be reduced by
   * @return true if the enemy dies due to the health reduction
   * @throws IllegalArgumentException if the amount is negative
   */
  public boolean damageEnemy(int amount) throws IllegalArgumentException {
    return false;
  }
  public int damagePlayer() {
    return 0;
  }

  public Coordinates move() {
    return null;
  }

  public void endReached() {

  }
}
