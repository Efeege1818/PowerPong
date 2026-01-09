package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A class that provides general functionality for the management of enemies.
 */
public class EnemyToolbox {
  private final List<Enemy> enemies = new ArrayList<>();
  private final SimpleTowerDefenseService service;

  /**
   * Creates a new EnemyToolbox.
   *
   * @param service that uses this Toolbox
   */
  public  EnemyToolbox(SimpleTowerDefenseService service) {
    this.service = service;
  }

  /**
   * Returns the default speed values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the speed for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getSpeed(EnemyType type) throws NoSuchElementException {
    return switch (type) {
      case SMALL -> 3;
      case MEDIUM -> 2;
      case LARGE -> 1;
      default -> throw new NoSuchElementException();
    };
  }

  /**
   * Returns the default damage values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the damage for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getDamage(EnemyType type) throws NoSuchElementException {
    return switch (type) {
      case SMALL -> 1;
      case MEDIUM -> 2;
      case LARGE -> 3;
      default -> throw new NoSuchElementException();
    };
  }

  /**
   * Returns the default money values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the default money for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getMoney(EnemyType type) throws NoSuchElementException {
    return switch (type) {
      case SMALL -> 1;
      case MEDIUM -> 2;
      case LARGE -> 3;
      default -> throw new NoSuchElementException();
    };
  }

  /**
   * Returns the weight of the EnemyType.
   * This determines how rare the enemies are in relation to each other
   * and is used for creating balanced waves.
   * A higher value means, that the enemy is more common.
   *
   * @param type the EnemyType of the Enemy
   * @return the weight for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public static int getWeight(EnemyType type) throws NoSuchElementException {
    // TODO: replace these dummy values with balanced ones
    return switch (type) {
      case SMALL -> 10;
      case MEDIUM -> 5;
      case LARGE -> 3;
      default -> throw new NoSuchElementException();
    };
  }

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

  /**
   * Reduces the enemy’s health by the given amount and marks it as dead
   * if its health reaches zero or below.
   *
   * @param amount the amount of damage to apply (must be non-negative)
   * @param enemy  this is the object which damageEnemy is called on
   * @throws IllegalArgumentException if the damage amount is negative
   */
  public static Enemy damageEnemy(int amount, Enemy enemy) {
    if (enemy.currentHealth() - amount <= 0) {
      return new Enemy(enemy.id(), enemy.coordinates(), enemy.type(),
              0, enemy.index());
    }

    return new Enemy(enemy.id(), enemy.coordinates(), enemy.type(),
            enemy.currentHealth() - amount, enemy.index());
  }

  /**
   * Deals damage to the player when the enemy reaches the end of its path.
   *
   * @return the amount of damage dealt to the player
   */
  public int damagePlayer() {
    int pathLength = service.getMapToolbox().getExtendedPath().size();

    int damage = 0;
    for (Enemy enemy : enemies) {
      if (enemy.index() >= pathLength) {
        damage += getDamage(enemy.type());
      }
    }
    return damage;
  }

  /**
   * Takes the enemyList and gives the player money for each dead enemy.
   *
   * @return the amount of money to the player
   */
  public int moneyPerEnemy() {
    int money = 0;
    for (Enemy enemy : enemies) {
      if (enemy.currentHealth() <= 0) {
        money += getMaxHealth(enemy.type());
      }
    }
    return money;
  }

  /**
   * Moves the enemy forward along its path based on its speed.
   */
  public void progress() {

    List<Coordinates> coordinatesList = service.getMapToolbox().getExtendedPath();
    List<Enemy> toBeRemoved = new ArrayList<>();

    for (Enemy enemy : enemies) {
      if (enemy.currentHealth() <= 0) {
        toBeRemoved.add(enemy);
        continue;
      }
      if ((enemy.index() + getSpeed(enemy.type())) > (coordinatesList.size() - 1)) {
        enemies.set(enemies.indexOf(enemy), new Enemy(enemy.id(), coordinatesList.getLast(), enemy.type(), enemy.currentHealth(), enemy.index()
                + getSpeed(enemy.type())));
      } else {
        enemies.set(enemies.indexOf(enemy), new Enemy(enemy.id(), coordinatesList.get(enemy.index()
                + getSpeed(enemy.type())), enemy.type(), enemy.currentHealth(), enemy.index()
                + getSpeed(enemy.type())));
      }
    }
    enemies.removeAll(toBeRemoved);
    if (!enemies.isEmpty()) {
      System.out.println(enemies);
    }
  }

  /**
   * Adds an enemy to the list.
   */
  public void addEnemy(Enemy newEnemy) {
    enemies.add(newEnemy);
  }

  public List<Enemy> getEnemies() {
    return enemies;
  }
}
