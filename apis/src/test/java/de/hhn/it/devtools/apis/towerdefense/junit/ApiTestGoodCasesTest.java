package de.hhn.it.devtools.apis.towerdefense.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.UUID;

import de.hhn.it.devtools.apis.towerdefense.*;
import org.junit.jupiter.api.Test;

/**
 * Test class that test the api.
 */
public class ApiTestGoodCasesTest {

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

  @Test
  void checkGridValues() {
    Direction[][] directions = new  Direction[][]{{Direction.NONE},{Direction.EAST}};
    Grid grid = new Grid(directions);

    assertEquals(Direction.EAST, grid.grid()[1][0]);
    assertEquals(Direction.NONE, grid.grid()[0][0]);
    assertEquals(2, grid.grid().length);
  }



}
