package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;

public class TowerToolbox {

  private TowerType towerType;
  int id;
  int attackSpeed;
  int price;
  int range;
  Coordinates coords;
  TowerType type;

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
