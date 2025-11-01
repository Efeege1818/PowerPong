package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;

public class BasicEnemy {

  int id;
  int speed;
  int health;
  EnemyType type;

  /**
   * Reduces the enemy’s health by the given amount and marks it as dead
   * if its health reaches zero or below.
   *
   * <p>The actual game logic should update the enemy’s state or remove it
   * from the active enemy list when it dies.
   *
   * @param amount the amount of damage to apply (must be non-negative)
   * @return {@code true} if the enemy dies as a result of the damage,
   *         {@code false} otherwise
   * @throws IllegalArgumentException if the damage amount is negative
   */
  public boolean damageEnemy(int amount) {
    // TODO: Implement health reduction and death logic
    // TODO: Check throwable and logic here
    return false;
  }

  /**
   * Deals damage to the player when the enemy reaches the end of its path.
   *
   * <p>The amount of damage is usually determined by the enemy type.
   *
   * @return the amount of damage dealt to the player
   */
  public int damagePlayer() {
    // TODO: Check logic and add throwable + params
    // TODO: Implement logic for how much damage the enemy causes the player
    return 0;
  }

  /**
   * Moves the enemy forward along its path based on its speed.
   *
   * <p>The implementation should calculate the next position
   * using the current path progress and enemy speed.
   *
   * @return the updated coordinates of the enemy after moving
   */
  public Coordinates move() {
    // TODO: Implement movement logic along the path
    return null;
  }

  /**
   * Called when the enemy successfully reaches the end of its path.
   *
   * <p>Typically, this triggers damage to the player and removal of the enemy.
   */
  public void endReached() {
    // TODO: Implement behavior for when the enemy reaches the end of the path
    // TODO: NEEDS FIXING IN LOGIC
    // Does it not just call damagePlayer(); ?
  }

}
