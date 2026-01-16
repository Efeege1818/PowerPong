package de.hhn.it.devtools.apis.towerdefense.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.apis.towerdefense.Enemy;
import de.hhn.it.devtools.apis.towerdefense.EnemyType;
import de.hhn.it.devtools.apis.towerdefense.Player;
import de.hhn.it.devtools.apis.towerdefense.Tower;
import de.hhn.it.devtools.apis.towerdefense.TowerType;
import org.junit.jupiter.api.Test;

/**
 * Test class that test the api.
 */
public class ApiTest {

  @Test
  void checkPlayerHealthAndMoney() {
    Player player = new Player(10, 20);

    assertEquals(10, player.health());
    assertEquals(20, player.money());
  }

  @Test
  void checkEnemyAttributesWork() {
    Coordinates pos = new Coordinates(3, 4);
    Enemy enemy = new Enemy(UUID.randomUUID(), pos, EnemyType.SMALL, 50, 0);

    assertEquals(pos, enemy.coordinates());
    assertEquals(EnemyType.SMALL, enemy.type());
    assertEquals(50, enemy.currentHealth());
    assertEquals(0, enemy.index());
  }

  @Test
  void checkTowerValues() {
    Coordinates pos = new Coordinates(3, 4);
    Tower tower = new Tower(pos, TowerType.RANGED);

    assertEquals(pos, tower.coordinates());
    assertEquals(TowerType.RANGED, tower.type());
  }

}
