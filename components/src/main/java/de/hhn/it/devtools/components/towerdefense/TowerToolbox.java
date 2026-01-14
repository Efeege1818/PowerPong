package de.hhn.it.devtools.components.towerdefense;

import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.apis.towerdefense.Enemy;
import de.hhn.it.devtools.apis.towerdefense.Tower;
import de.hhn.it.devtools.apis.towerdefense.TowerType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A class that provides general functionality for the management of towers.
 */
public class TowerToolbox {
  private List<Tower> towers = new ArrayList<>();
  private List<Tower> savedTowers = new ArrayList<>();
  private final SimpleTowerDefenseService service;

  /**
   * Creates a new TowerToolbox.
   *
   * @param service that uses this Toolbox
   * */
  public  TowerToolbox(SimpleTowerDefenseService service) {
    this.service = service;
  }

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
      case MELEE -> 40;
      case RANGED -> 30;
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
  public static int getCost(TowerType type) throws NoSuchElementException {
    return switch (type) {
      case MELEE -> 10;
      case RANGED -> 15;
      case MONEYMAKER -> 20;
      default -> throw new NoSuchElementException();
    };
  }

  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @throws IllegalArgumentException if towers or enemies do not exist.
   */
  public void attack() {
    int pathLength = service.getMapToolbox().getExtendedPath().size();
    List<Enemy> enemies = service.getEnemyToolbox().getEnemies();
    List<Enemy> toBeRemoved = new ArrayList<>();
    for (Enemy enemy : enemies) {
      if (enemy.index() >= pathLength) {
        toBeRemoved.add(enemy);
      }
    }
    enemies.removeAll(toBeRemoved);
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
  }


  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @return how much money was made
   */
  public int moneyMade()
          throws IllegalArgumentException {

    int money = 0;
    for (Tower tower : towers) {
      if (tower.type().equals(TowerType.MONEYMAKER)) {
        money += 1;
      }
    }
    return money;
  }

  /**
   * Add a tower to the list.
   */
  public void addTower(Tower tower) {
    towers.add(tower);
  }

  /**
   * Attacks the enemy in range, that has advanced the furthest on the path.
   *
   * @return map of the current towers.
   */
  public Map<Coordinates, Tower> getTowers() {
    Map<Coordinates, Tower> map = new HashMap<>();
    for (Tower tower : towers) {
      map.put(tower.coordinates(), tower);
    }
    return map;
  }

  /**
   * Saves the current tower list to load it back if the player loses.
   */
  public void saveData() {
    savedTowers.clear();
    savedTowers.addAll(towers);
  }

  /**
   * Loads the latest saved tower list.
   */
  public void loadData() {
    towers.clear();
    towers.addAll(savedTowers);
  }
}
