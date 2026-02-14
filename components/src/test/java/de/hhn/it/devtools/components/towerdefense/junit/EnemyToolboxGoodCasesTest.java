package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.apis.towerdefense.Enemy;
import de.hhn.it.devtools.apis.towerdefense.EnemyType;
import de.hhn.it.devtools.components.towerdefense.EnemyToolbox;
import de.hhn.it.devtools.components.towerdefense.SimpleTowerDefenseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;

public class EnemyToolboxGoodCasesTest {

  private SimpleTowerDefenseService service;
  private EnemyToolbox toolbox;

  @BeforeEach
  public void setup() {
    service = new SimpleTowerDefenseService();
    toolbox = new EnemyToolbox(service);
  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  void testGetSpeed() {
    assertEquals(3, EnemyToolbox.getSpeed(EnemyType.SMALL));
    assertEquals(2, EnemyToolbox.getSpeed(EnemyType.MEDIUM));
    assertEquals(1, EnemyToolbox.getSpeed(EnemyType.LARGE));
  }

  @Test
  void testGetDamage() {
    assertEquals(1, EnemyToolbox.getDamage(EnemyType.SMALL));
    assertEquals(2, EnemyToolbox.getDamage(EnemyType.MEDIUM));
    assertEquals(3, EnemyToolbox.getDamage(EnemyType.LARGE));
  }

  @Test
  void testGetMoney() {
    assertEquals(5, EnemyToolbox.getMoney(EnemyType.SMALL));
    assertEquals(10, EnemyToolbox.getMoney(EnemyType.MEDIUM));
    assertEquals(15, EnemyToolbox.getMoney(EnemyType.LARGE));
  }

  @Test
  void testGetWeight() {
    assertEquals(10, EnemyToolbox.getWeight(EnemyType.SMALL));
    assertEquals(5, EnemyToolbox.getWeight(EnemyType.MEDIUM));
    assertEquals(3, EnemyToolbox.getWeight(EnemyType.LARGE));
  }

  @Test
  void testGetMaxHealth() {
    assertEquals(50, EnemyToolbox.getMaxHealth(EnemyType.SMALL));
    assertEquals(100, EnemyToolbox.getMaxHealth(EnemyType.MEDIUM));
    assertEquals(150, EnemyToolbox.getMaxHealth(EnemyType.LARGE));
  }

  @Test
  void testDamageEnemySurvives() {
    Enemy enemy = new Enemy(new UUID(0, 0), new Coordinates(0, 0), EnemyType.SMALL, 10, 0);
    Enemy damaged = EnemyToolbox.damageEnemy(3, enemy);
    assertEquals(7, damaged.currentHealth());
    assertEquals(enemy.id(), damaged.id());
    assertEquals(enemy.type(), damaged.type());
  }

  @Test
  void testDamageEnemyDies() {
    Enemy enemy = new Enemy(new UUID(0, 0), new Coordinates(0, 0), EnemyType.SMALL, 3, 0);
    Enemy damaged = EnemyToolbox.damageEnemy(3, enemy);
    assertEquals(0, damaged.currentHealth());
  }

  @Test
  void testDamagedPlayerEnemyReachedEnd() {
    int pathLength = service.getMapToolbox().getExtendedPath().size();

    Enemy enemy = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getLast(), EnemyType.SMALL, 3,
            pathLength);
    toolbox.addEnemy(enemy);
    int damageToPlayer = toolbox.damagePlayer();
    assertEquals(1, damageToPlayer);

    Enemy enemy1 = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getLast(), EnemyType.MEDIUM, 3,
            pathLength);
    toolbox.addEnemy(enemy1);
    int damageToPlayer1 = toolbox.damagePlayer();
    assertEquals(3, damageToPlayer1);

    Enemy enemy2 = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getLast(), EnemyType.LARGE, 3,
            pathLength);
    toolbox.addEnemy(enemy2);
    int damageToPlayer2 = toolbox.damagePlayer();
    assertEquals(6, damageToPlayer2);
  }

  @Test
  void testMoneyPerEnemyOnlyDeadEnemiesCount() {
    Enemy deadEnemy = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getFirst(), EnemyType.LARGE,
            0, 1);
    Enemy aliveEnemy = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getFirst(), EnemyType.SMALL,
            10, 1);
    toolbox.addEnemy(deadEnemy);
    toolbox.addEnemy(aliveEnemy);
    int money = toolbox.moneyPerEnemy();
    assertEquals(15, money);
  }

  @Test
  void testProgressMovesEnemyForward() {
    Enemy enemy = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getFirst(), EnemyType.SMALL, 10,
            0);
    toolbox.addEnemy(enemy);
    toolbox.progress();
    Enemy moved = toolbox.getEnemies().getFirst();
    assertEquals(3, moved.index());
  }

  @Test
  void testProgressRemovesDeadEnemy() {
    Enemy deadEnemy = new Enemy(new UUID(0, 0), service.getMapToolbox().getExtendedPath().getFirst(), EnemyType.SMALL,
            0, 0);
    toolbox.addEnemy(deadEnemy);
    toolbox.progress();
    assertTrue(toolbox.getEnemies().isEmpty());
  }
}
