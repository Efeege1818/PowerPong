package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that provides general functionality for the Management of Towers.
 */
public class TowerToolbox {


  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @param enemies as list of enemies
   * @param towers as list of towers
   * @return {@code true} if attack was successful
   *         {@code false} otherwise
   * @throws IllegalArgumentException if towers or enemies do not exist.
   */
  public ArrayList<Enemy> action(List<Tower> towers, List<Enemy> enemies, EnemyToolbox enemyBox)
      throws IllegalArgumentException {
    return null;
  }


  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @param towers as list of towers
   * @return {@code int} how much money was made
   * @throws IllegalArgumentException if towers or enemies do not exist.
   */
  public int moneyMade(List<Tower> towers) {
    int money = 0;
    for (Tower tower : towers) {
      if (tower.type().equals(TowerType.MONEYMAKER)) {
        money += 50;
      }
    }

    return money;
  }
}
