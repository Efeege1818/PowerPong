package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.*;
import de.hhn.it.devtools.components.towerdefense.SimpleTowerDefenseService;
import de.hhn.it.devtools.apis.towerdefense.Tower;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TowerDefenseServiceBadCasesTest {

  public SimpleTowerDefenseService service;

  @BeforeEach
  public void setup() {
    service = new SimpleTowerDefenseService();
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
  public void testStartGameTwiceDoesThrow() {
    service.startGame();
    Assertions.assertThrows(IllegalStateException.class, () -> service.startGame());
  }

  @Test
  public void testStartNextRoundNotAllowedWhenReady() {
    Assertions.assertThrows(IllegalStateException.class, () -> service.startNextRound());
  }

  @Test
  public void testRetryNotAllowedWhenNotGameOver() {
    service.startGame();
    Assertions.assertThrows(IllegalStateException.class, () -> service.retry());
  }

  @Test
  public void testPlaceTowerNullThrowsException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.placeTower(null));
  }

  @Test
  public void testPlaceTowerNotEnoughMoney() {
    service.editConfiguration(new Configuration(10, 50, 199, 1, 1, 1));
    service.startGame();
    Tower expensiveTower = new Tower(new Coordinates(0, 0), TowerType.MONEYMAKER);
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.placeTower(expensiveTower));
  }

  @Test
  public void testUpdateMoneyBelowZeroThrowsException() {
    service.editConfiguration(new Configuration(10, 50, 100, 1, 1, 1));
    service.startGame();
    Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateMoney(-101));
  }
}
