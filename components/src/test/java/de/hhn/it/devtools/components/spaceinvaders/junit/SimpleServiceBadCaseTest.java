package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SimpleServiceBadCaseTest {

  @Test
  void testStartWhenNotPreparedThrows() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    // ensure gameState is not PREPARED
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    assertThrows(IllegalStateException.class, svc::start);
  }

  @Test
  void testConfigureNullThrows() {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    assertThrows(RuntimeException.class, () -> svc.configure(null));
  }

  @Test
  void testPauseWhenNotRunningThrows() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.PREPARED);
    assertThrows(IllegalStateException.class, svc::pause);
  }

  @Test
  void testResumeWhenNotPausedThrows() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);
    assertThrows(IllegalStateException.class, svc::resume);
  }

  @Test
  void testTriggeredByGameLoopDoesNotCallNextRoundWhenAliensPresent() throws Exception {
    // Ensure that nextRound is NOT invoked when aliens are present at the start
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    java.util.concurrent.atomic.AtomicBoolean nextRoundCalled = new java.util.concurrent.atomic.AtomicBoolean(false);
    svc.addListener(new de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener() {
      @Override public void updateBarrier(de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier barrier) {}
      @Override public void updateAliens(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien[] aliens) {}
      @Override public void updateShip(de.hhn.it.devtools.apis.spaceinvaders.entities.Ship ship) {}
      @Override public void updateProjectile(de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile projectile) {}
      @Override public void damageAlien(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien alien) {}
      @Override public void updateSound(de.hhn.it.devtools.apis.spaceinvaders.Sound sound) {}
      @Override public void changedGameState(de.hhn.it.devtools.apis.spaceinvaders.GameState gameState) {}
      @Override public void updateRound(int round) { nextRoundCalled.set(true); }
      @Override public void gameEnded() {}
      @Override public void updateScore(int score) {}
      @Override public void updateGameConfiguration(de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration configuration) {}
    });

    // stub EntityProvider with at least one alien so getAliens() is non-empty
    de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider stub =
        new de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider(svc) {
          @Override
          public java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> getAliens() {
            java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> map = new java.util.HashMap<>();
            map.put(1, new de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien(
                new de.hhn.it.devtools.apis.spaceinvaders.Coordinate(5,5),
                de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType.BASIC,
                1));
            return map;
          }

          @Override public void updateProjectiles() {}
          @Override public void checkCollision() {}
          @Override public void updateAliens() {}
        };

    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, stub);

    // call the game loop trigger
    svc.triggeredByGameLoop();

    assertFalse(nextRoundCalled.get(), "nextRound should NOT be invoked when aliens are present at start");
  }

  @Test
  void testTriggeredByGameLoopDoesNotAbortWhenPlayerHPPositive() throws Exception {
    // Ensure that abort() is NOT invoked when player HP is positive
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.PREPARED); // abort would be allowed if conditions met

    // stub EntityProvider: getAliens non-empty to avoid nextRound; getPlayer returns ship with positive HP
    de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider stub =
        new de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider(svc) {
          @Override
          public java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> getAliens() {
            java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> map = new java.util.HashMap<>();
            map.put(1, new de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien(new de.hhn.it.devtools.apis.spaceinvaders.Coordinate(2,2), de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType.BASIC, 1));
            return map;
          }

          @Override public void updateProjectiles() {}
          @Override public void checkCollision() {}
          @Override public void updateAliens() {}
          @Override public de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip getPlayer() {
            de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip ship = super.getPlayer();
            ship.setHitPoints(3); // positive HP
            return ship;
          }
        };

    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, stub);

    // call the game loop trigger
    svc.triggeredByGameLoop();

    // assert game state is NOT ABORTED and entityProvider still present
    assertNotEquals(GameState.ABORTED, gs.get(svc), "gameState should not be ABORTED when player HP is positive");
    assertNotNull(ep.get(svc), "entityProvider should still be present when abort is not triggered");
  }


}
