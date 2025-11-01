package de.hhn.it.devtools.apis.towerdefenseapi;


//TODO verify throwable

/**
 * Represents an enemy in the Tower Defense game.
 *
 * <p>Each enemy has a unique ID, a movement speed (how much time between movements),
 * a health value, a path it follows, and a type (e.g. SMALL, MEDIUM, LARGE).
 * Enemies move along their assigned path toward the player's base.
 *
 * <p>This record is immutable — once created, the enemy’s properties cannot change.
 */
public record Enemy(int id,
                    Coordinates coordinates,
                    EnemyType type) {

  public int getId() {
    return id;
  }

  Coordinates getCoordinates() {
    return coordinates;
  }

  EnemyType getEnemyType() {
    return type;
  }

}
