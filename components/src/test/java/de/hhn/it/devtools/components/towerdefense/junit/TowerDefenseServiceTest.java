package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefenseapi.GameState;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TowerDefenseServiceTest {

  public static SimpleTowerDefenseService service;

  @BeforeAll
  public static void setup() {
    service = new SimpleTowerDefenseService();
  }

  @Test
  public void initialGameStateTest() {
    Assertions.assertEquals(GameState.READY, service.getCurrentGameState());
  }

  @Test
  public void testDefaultStartingMoneyAndHealth() {
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
  public void testUpdateHealthPositive() {
    try {
      service.updateHealth(5);
    } catch (IllegalArgumentException e) {
      return;
    }
    Assertions.fail("Positive Value should throw an exception");
  }

  @Test
  public void testUpdateMoney() {
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

}

