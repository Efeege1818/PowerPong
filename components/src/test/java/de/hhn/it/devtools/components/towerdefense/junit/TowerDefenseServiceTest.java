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
  public void testUpdateHealth() {
    service.updateHealth(-8);
    service.updateHealth(-5);
    service.updateHealth(-10);
    Assertions.assertEquals(23, 0);

  }


}

