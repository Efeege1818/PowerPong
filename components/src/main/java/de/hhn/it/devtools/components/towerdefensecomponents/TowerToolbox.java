package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import java.util.List;
import java.util.NoSuchElementException;

// LOCKED : S.Arsenovici

/**
 * A class that provides general functionality for the Management of Towers.
 */
public class TowerToolbox {


  /**
   * Returns the default range values for different tower types.
   *
   * @param type the TowerType of the Tower
   * @return the range for a tower of the given type
   * @throws NoSuchElementException if the given TowerType isn't supported
   */
  public static int getRange(TowerType type) throws NoSuchElementException {
    return switch (type) {
      case MELEE -> 5;
      case RANGED -> 10;
      case MONEYMAKER -> 0;
      default -> throw new NoSuchElementException();
    };
  }

  /**
   * Returns the default damage values for different tower types.
   *
   * @param type the TowerType of the Tower
   * @return the damage for a tower of the given type
   * @throws NoSuchElementException if the given TowerType isn't supported
   */
  public static int getDamage(TowerType type) throws NoSuchElementException {
    return switch (type) {
      case MELEE -> 20;
      case RANGED -> 10;
      case MONEYMAKER -> 0;
      default -> throw new NoSuchElementException();
    };
  }


  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @param enemies as list of enemies
   * @param towers  as list of towers
   * @return {@code ArrayList<Enemy>} with updated enemies
   * @throws IllegalArgumentException if towers or enemies do not exist.
   */
  public static List<Enemy> action(List<Tower> towers, List<Enemy> enemies)
          throws IllegalArgumentException {
    for (Tower tower : towers) {
      if (tower.type() != TowerType.MONEYMAKER) {
        int furthestEnemy = -1;
        Enemy enemyToBeAttacked = null;
        for (Enemy enemy : enemies) {
          double testDistance = Math.abs(Math.pow((tower.coordinates().x()
                  - enemy.coordinates().x()), 2) + Math.pow((tower.coordinates().y()
                  - enemy.coordinates().y()), 2));
          if ((testDistance < getRange(tower.type()))
                  && (furthestEnemy == -1 || enemy.index() > furthestEnemy)) {
            furthestEnemy = enemy.index();
            enemyToBeAttacked = enemy;
          }
        }
        if (enemyToBeAttacked != null) {
          enemies.set(enemies.indexOf(enemyToBeAttacked),
                  EnemyToolbox.damageEnemy(getDamage(tower.type()), enemyToBeAttacked));
        }
      }
    }
    return enemies;
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
