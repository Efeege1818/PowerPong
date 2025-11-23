package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;

import java.util.ArrayList;
import java.util.List;

public class TowerToolbox {


  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @param enemies as list of enemies
   * @return {@code true} if attack was successful
   *         {@code false} otherwise
   * @throws IllegalArgumentException if towers or enemies do not exist.
   */
  public ArrayList<Enemy> action(List<Tower> towers, List<Enemy> enemies) throws IllegalArgumentException {
    return null;
  }

  public int moneyMade(List<Tower> towers, List<Enemy> enemies){
    return 0;
  }





  /**
   * Returns the coordinates where the tower is placed on the map.
   *
   * @return the tower's position as Coordinates
   */
  @Deprecated //Methode is unnötig
  Coordinates getCoordinates() {
    return null;
  }

  /**
   * Returns the type of this tower, e.g. MELEE, MONEYMAKER and RANGED.
   *
   * @return the TowerType of this tower
   */
  @Deprecated //Methode is unnötig
  TowerType getTowerType() {
    return null;
  }
}
