package de.hhn.it.devtools.components.towerDefenseComponents;

import de.hhn.it.devtools.apis.towerDefenseApis.Coordinates;
import de.hhn.it.devtools.apis.towerDefenseApis.TowerType;

//TODO verify throwable

/**
 * Represents a tower in the Tower Defense game.
 *
 * <p>A tower has a unique ID, an attack speed (how much time between shots),
 * a price (cost to place), a range (how far it can attack),
 * fixed coordinates on the map, and a specific tower type (e.g., MELEE, MONEYMAKER and RANGED).
 *
 * <p>This record is immutable – once created, the tower's properties cannot be changed.
 */
public record Tower(int id,
                    int attackSpeed,
                    int price,
                    int range,
                    Coordinates coords,
                    TowerType type) {


  /**
   * Returns the unique identifier of the tower.
   *
   * @return the tower ID
   */
  int getId() {
    return id;
  }

  /**
   * Returns the attack speed of the tower.
   * A higher value means the time between attacks is larger.
   *
   * @return the attack speed
   */
  int getAttackSpeed() {
    return attackSpeed;
  }

  /**
   * Returns the price of the tower.
   * This value indicates how much the tower costs to build.
   *
   * @return the price
   */
  int getPrice() {
    return price;
  }

  /**
   * Returns the effective attack range of the tower.
   * Furthest enemies within this distance can be targeted.
   *
   * @return the attack range
   */
  int getRange() {
    return range;
  }

  /**
   * Returns the coordinates where the tower is placed on the map.
   *
   * @return the tower's position as Coordinates
   */
  Coordinates getCoordinates() {
    return coords;
  }

  /**
   * Returns the type of this tower, e.g. MELEE, MONEYMAKER and RANGED.
   *
   * @return the TowerType of this tower
   */
  TowerType getTowerType() {
    return type;
  }

  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @param enemyId the ID of the targeted enemy
   * @return {@code true} if attack was successful
   *         {@code false} otherwise
   * @throws IllegalArgumentException if enemyID does not exist or is out of range.
   */
  public boolean attack(int enemyId) throws IllegalArgumentException {
    // TODO: need logic check and parser into enemy class
    return false;
  }

}
