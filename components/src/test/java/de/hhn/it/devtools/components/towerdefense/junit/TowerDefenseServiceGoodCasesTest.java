package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.*;
import de.hhn.it.devtools.components.towerdefense.SimpleTowerDefenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TowerDefenseServiceGoodCasesTest {

  public SimpleTowerDefenseService service;

  @BeforeEach
  public void setup() {
    service = new SimpleTowerDefenseService();
  }

  @Test
  public void initialGameStateTest() {
    Assertions.assertEquals(GameState.READY, service.getCurrentGameState());
  }

  @Test
  public void testDefaultStartingMoneyAndHealth() {
    service.editConfiguration(new Configuration(10, 50, 100, 1, 1, 1));
    service.startGame();
    Assertions.assertEquals(100, service.getPlayer().money());
    Assertions.assertEquals(50, service.getPlayer().health());
  }

  @Test
  public void testUpdateHealth() {
    service.updateHealth(-8);
    service.updateHealth(-5);
    service.updateHealth(-10);
    Assertions.assertEquals(27, service.getPlayer().health());
  }

  @Test
  public void testUpdateMoney() {
    service.editConfiguration(new Configuration(10, 50, 100, 1, 1, 1));
    service.startGame();
    service.updateMoney(10);
    service.updateMoney(7);
    service.updateMoney(13);
    Assertions.assertEquals(130, service.getPlayer().money());
  }

  @Test
  public void testStartNextRound() {
    service.startGame();
    service.startNextRound();
  }

  @Test
  public void testEnemyToolboxExists() {
    Assertions.assertNotNull(service.getEnemyToolbox());
  }

  @Test
  public void testTowerToolboxExists() {
    Assertions.assertNotNull(service.getTowerToolbox());
  }

  @Test
  public void testMapToolboxExists() {
    Assertions.assertNotNull(service.getMapToolbox());
  }

  @Test
  public void testStartNextRoundOnlyWhenRunning() {
    service.startGame();
    Assertions.assertDoesNotThrow(() -> service.startNextRound());
  }

  @Test
  public void testMoneyIncreasesAfterRound() {
    service.startGame();
    int before = service.getPlayer().money();
    service.startNextRound();
    int after = service.getPlayer().money();
    Assertions.assertTrue(after >= before);
  }

  @Test
  public void testStartGameDoesNotThrow() {
    Assertions.assertDoesNotThrow(() -> service.startGame());
  }

  @Test
  public void testStartNextRoundAfterStartGameDoesNotThrow() {
    service.startGame();
    Assertions.assertDoesNotThrow(() -> service.startNextRound());
  }

  @Test
  public void testPlayerNotNullAfterActions() {
    service.startGame();
    service.updateMoney(5);
    service.updateHealth(-5);
    Assertions.assertNotNull(service.getPlayer());
  }

  @Test
  public void testUpdateHealthChangesValue() {
    int before = service.getPlayer().health();
    service.updateHealth(-1);
    int after = service.getPlayer().health();
    Assertions.assertEquals(before - 1, after);
  }

  @Test
  public void testUpdateMoneyChangesValue() {
    int before = service.getPlayer().money();
    service.updateMoney(1);
    int after = service.getPlayer().money();
    Assertions.assertEquals(before + 1, after);
  }

  @Test
  public void testStartGameSetsPausedState() {
    service.startGame();
    Assertions.assertEquals(GameState.PAUSED, service.getCurrentGameState());
  }

  @Test
  public void testAbortGameResetsStateAndPlayer() {
    service.editConfiguration(new Configuration(10, 50, 80, 1, 1, 1));
    service.startGame();
    service.updateMoney(20);
    service.updateHealth(-10);
    service.abortGame();
    Assertions.assertEquals(GameState.READY, service.getCurrentGameState());
    Assertions.assertEquals(50, service.getPlayer().health());
    Assertions.assertEquals(80, service.getPlayer().money());
    Assertions.assertEquals(0, service.getCurrentRound());
  }

  @Test
  public void testStartNextRoundIncrementsRound() {
    service.startGame();
    service.startNextRound();
    Assertions.assertEquals(1, service.getCurrentRound());
    Assertions.assertEquals(GameState.RUNNING, service.getCurrentGameState());
  }
}
