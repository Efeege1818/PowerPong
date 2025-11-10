package de.hhn.it.devtools.apis.towerdefenseapi;


// TODO: verify throwable

import java.util.NoSuchElementException;

/**
 * Represents an enemy in the Tower Defense game.
 *
 * <p>Each enemy has a unique ID, a movement speed (how much time between movements),
 * a health value, a path it follows, and a type (e.g. SMALL, MEDIUM, LARGE).
 * Enemies move along their assigned path toward the player's base.
 *
 * <p>This record is immutable — once created, the enemy’s properties cannot change.
 *
 * @param id a unique Identifier
 * @param coordinates the position of this enemy on the Board
 * @param type the EnemyType of this Enemy
 * @param speed the Speed of this Enemy. A faster enemy has a higher speed value
 * @param health the current Health of this enemy
 * @param damage the amount by witch this enemy reduces the players health, if it comes through
 * @param money the amount of money awarded to the player if this enemy is defeated
 */
public record Enemy(int id,
                    Coordinates coordinates,
                    EnemyType type,
                    int speed,
                    int health,
                    int damage,
                    int money
) {

  /**
   * Returns the default maximum Heath values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the maximum health for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getMaxHealth(EnemyType type) throws NoSuchElementException {
    switch (type) {
      case SMALL -> {
        return 50;
      }
      case MEDIUM -> {
        return 100;
      }
      case LARGE -> {
        return 150;
      }
      default -> {
        throw new NoSuchElementException();
      }
    }
  }

  /**
   * Returns the default speed values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the speed for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getSpeed(EnemyType type) throws NoSuchElementException {
    switch (type) {
      case SMALL -> {
        return 10;
      }
      case MEDIUM -> {
        return 8;
      }
      case LARGE -> {
        return 5;
      }
      default -> {
        throw new NoSuchElementException();
      }
    }
  }

  /**
   * Returns the default damage values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the damage for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getDamage(EnemyType type) throws NoSuchElementException {
    switch (type) {
      case SMALL -> {
        return 1;
      }
      case MEDIUM -> {
        return 2;
      }
      case LARGE -> {
        return 3;
      }
      default -> {
        throw new NoSuchElementException();
      }
    }
  }

  /**
   * Returns the default money values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the default money for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getMoney(EnemyType type) throws NoSuchElementException {
    switch (type) {
      case SMALL -> {
        return 1;
      }
      case MEDIUM -> {
        return 2;
      }
      case LARGE -> {
        return 3;
      }
      default -> {
        throw new NoSuchElementException();
      }
    }
  }

}
