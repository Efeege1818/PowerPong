package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.*;
import de.hhn.it.devtools.components.towerdefense.EnemyToolbox;
import de.hhn.it.devtools.components.towerdefense.SimpleTowerDefenseService;
import de.hhn.it.devtools.components.towerdefense.TowerToolbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.UUID;

public class TowerToolboxTest {

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(TowerToolboxTest.class);

  SimpleTowerDefenseService service;
  TowerToolbox towerToolbox;
  EnemyToolbox enemyToolbox;

  @BeforeEach
  void setup() {
    service = new SimpleTowerDefenseService();
    towerToolbox = service.getTowerToolbox();
    enemyToolbox = service.getEnemyToolbox();
  }

  @AfterEach
  void tearDown() {

  }

  @Test
  void testGetRange() {
    assertEquals(5, TowerToolbox.getRange(TowerType.MELEE));
    assertEquals(10, TowerToolbox.getRange(TowerType.RANGED));
    assertEquals(0, TowerToolbox.getRange(TowerType.MONEYMAKER));
  }

  @Test
  void testGetDamage() {
    assertEquals(40, TowerToolbox.getDamage(TowerType.MELEE));
    assertEquals(30, TowerToolbox.getDamage(TowerType.RANGED));
    assertEquals(0, TowerToolbox.getDamage(TowerType.MONEYMAKER));
  }

  @Test
  void testGetCost() {
    assertEquals(10, TowerToolbox.getCost(TowerType.MELEE));
    assertEquals(15, TowerToolbox.getCost(TowerType.RANGED));
    assertEquals(20, TowerToolbox.getCost(TowerType.MONEYMAKER));
  }

  @Test
  void testAddTowerAndGetTowers() {
    Tower tower = new Tower(new UUID(0, 0), new Coordinates(0, 0), TowerType.MELEE);

    towerToolbox.addTower(tower);

    Map<Coordinates, Tower> map = towerToolbox.getTowers();

    assertEquals(1, map.size());
    assertTrue(map.containsKey(new Coordinates(0, 0)));
  }

  @Test
  void testAttackDamagesEnemyInRange() {
    Coordinates towerPos = new Coordinates(0, 0);
    Coordinates enemyPos = new Coordinates(1, 1);

    Tower tower = new Tower(new UUID(1, 0), towerPos, TowerType.MELEE);

    Enemy enemy = new Enemy(new UUID(2, 0), enemyPos, EnemyType.SMALL, 50, 1);

    towerToolbox.addTower(tower);
    enemyToolbox.addEnemy(enemy);

    towerToolbox.attack();

    Enemy damaged = enemyToolbox.getEnemies().getFirst();

    assertTrue(damaged.currentHealth() < 50);
  }

  @Test
  void testMoneyMade() {
    Tower moneyTower1 = new Tower(new UUID(1, 0), new Coordinates(0, 0), TowerType.MONEYMAKER);

    Tower moneyTower2 = new Tower(new UUID(2, 0), new Coordinates(1, 1), TowerType.MONEYMAKER);

    towerToolbox.addTower(moneyTower1);
    towerToolbox.addTower(moneyTower2);

    int money = towerToolbox.moneyMade();

    assertEquals(2, money);
  }

  @Test
  void testSaveAndLoadData() {
    Tower tower = new Tower(new UUID(3, 0), new Coordinates(2, 2), TowerType.RANGED);

    towerToolbox.addTower(tower);
    towerToolbox.saveData();

    towerToolbox.getTowers().clear();
    towerToolbox.loadData();

    assertEquals(1, towerToolbox.getTowers().size());
  }
}
