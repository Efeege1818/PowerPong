package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Makes service between the actual class and enemy operable through new creations of the records.
 */
public class EnemyToolbox {
  // TODO: add exception throws and fix logic/comments when necessary

  /**
   * Reduces the enemy’s health by the given amount and marks it as dead
   * if its health reaches zero or below.
   *
   * @param amount the amount of damage to apply (must be non-negative)
   * @param enemy  this is the object which damageEnemy is called on
   * @throws IllegalArgumentException if the damage amount is negative
   */
  public Enemy damageEnemy(int amount, Enemy enemy) {
    // TODO: Check throwable and logic here
    // TODO: Check if enemy is dead in towerToolbox or Service
    // TODO: Money in towerToolbox when enemy dies

    if (enemy.health() - amount <= 0) {
      return new Enemy(enemy.id(), enemy.coordinates(), enemy.type(),
              0, enemy.index());
    }

    return new Enemy(enemy.id(), enemy.coordinates(), enemy.type(),
            enemy.health() - amount, enemy.index());
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
      case SMALL -> 10;
      case MEDIUM -> 8;
      case LARGE -> 5;
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
    // TODO: add good money value
    return switch (type) {
      case SMALL -> 1;
      case MEDIUM -> 2;
      case LARGE -> 3;
      default -> throw new NoSuchElementException();
    };
  }

  /**
   * Deals damage to the player when the enemy reaches the end of its path.
   *
   * <p>The amount of damage is determined by the enemy type.
   *
   * @return the amount of damage dealt to the player
   */
  public int damagePlayer(ArrayList<Enemy> enemyList) {
    // TODO: Check logic and add throwable + params
    // TODO: Implement logic for how much damage the enemy causes the player
    return 0;
  }

  /**
   * Takes the enemyList and gives the player money for each dead enemy.
   *
   * @param enemyList list of all current enemies including dead ones
   * @return the amount of money to the player
   */
  public int moneyPerEnemy(ArrayList<Enemy> enemyList) {
    // TODO: implement
    //getMoney(enemy.type())
    return 0;
  }

  /**
   * Moves the enemy forward along its path based on its speed.
   * The enemy gets deleted when it is currently on the end.
   *
   * <p>The implementation should calculate the next position
   * using the current path progress and enemy speed.
   *
   * @param enemyList       list of all current enemies
   * @param coordinatesList list of all coordinates associated with the index
   * @return updates enemies and their coordinate and removes if they reached the end
   */
  public ArrayList<Enemy> progress(ArrayList<Enemy> enemyList,
                                   ArrayList<Coordinates> coordinatesList) {

//    ArrayList<Enemy> newList = new ArrayList<>();
//
//    for (Enemy enemy : enemyList) {
//      if (!(coordinatesList.size() <= enemy.index() + 1)) {
//        newList.add(new Enemy(enemy.id(),
//                coordinatesList.get(enemy.index() + getSpeed(enemy.type())),
//                enemy.type(), enemy.health(), enemy.index() + getSpeed(enemy.type())));
//      }
//    }
//    return newList;
    return null;
  }

  /**
   * Called when the enemy successfully reaches the end of its path.
   *
   * <p>Typically, this triggers damage to the player and removal of the enemy.
   */
  @Deprecated
  public void endReached() {
    // TODO: Implement behavior for when the enemy reaches the end of the path

  }
}
