package towerdefense;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;
import de.hhn.it.devtools.apis.towerdefenseapi.Player;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import org.junit.jupiter.api.Test;

/**
 * Test class that test the api.
 */
public class Tests {

  @Test
  void checkPlayerHealthAndMoney() {
    Player player = new Player(10, 20);

    assertEquals(10, player.health());
    assertEquals(20, player.money());
  }

  @Test
  void checkEnemyAttributesWork() {
    Coordinates pos = new Coordinates(3, 4);
    Coordinates position = new Coordinates(3,4);
    Enemy enemy = new Enemy(1, pos, EnemyType.SMALL, 50, 0);

    assertEquals(1, enemy.id());
    assertEquals(pos, enemy.coordinates());
    assertEquals(EnemyType.SMALL, enemy.type());
    assertEquals(50, enemy.currentHealth());
    assertEquals(0, enemy.index());
  }

  @Test
  void checkTowerValues() {
    Coordinates pos = new Coordinates(3, 4);
    Tower tower = new Tower(10, pos, TowerType.RANGED);

    assertEquals(10, tower.id());
    assertEquals(pos, tower.coordinates());
    assertEquals(TowerType.RANGED, tower.type());
  }

}
