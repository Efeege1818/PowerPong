package de.hhn.it.devtools.apis.towerdefenseapi;


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
 * @param id          a unique Identifier
 * @param coordinates the position of this enemy on the Board
 * @param type        the EnemyType of this Enemy
 * @param health      the current Health of this enemy
 * @param index       the current index in the path progression
 */
public record Enemy(int id,
                    Coordinates coordinates,
                    EnemyType type,
                    int health,
                    int index
) {

  /**
   * Returns the default maximum Heath values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the maximum health for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getMaxHealth(EnemyType type) throws NoSuchElementException {
    return switch (type) {
      case SMALL -> 50;
      case MEDIUM -> 100;
      case LARGE -> 150;
      default -> throw new NoSuchElementException();
    };
  }
}
