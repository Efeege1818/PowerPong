package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


import static org.junit.jupiter.api.Assertions.*;

class SimpleServiceGoodCaseTest {

  @Test
  void testConfigureAndGetConfiguration() {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    GameConfiguration cfg = new GameConfiguration(5, Difficulty.NORMAL);
    svc.configure(cfg);
    assertEquals(cfg, svc.getConfiguration());
  }

  @Test
  void testStartSetsRunningAndNotifiesListener() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    // set private gameState to PREPARED so start() is allowed
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.PREPARED);

    AtomicBoolean seen = new AtomicBoolean(false);
    svc.addListener(new SpaceInvadersListener() {
      @Override public void updateBarrier(de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier barrier) {}
      @Override public void updateAliens(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien[] aliens) {}
      @Override public void updateShip(de.hhn.it.devtools.apis.spaceinvaders.entities.Ship ship) {}
      @Override public void updateProjectile(de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile projectile) {}
      @Override public void damageAlien(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien alien) {}
      @Override public void updateSound(de.hhn.it.devtools.apis.spaceinvaders.Sound sound) {}
      @Override public void changedGameState(GameState gameState) { seen.set(true); }
      @Override public void updateRound(int round) {}
      @Override public void gameEnded() {}
      @Override public void updateScore(int score) {}
      @Override public void updateGameConfiguration(de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration configuration) {}
    });

    svc.start();

    // after start the gameState field should be RUNNING
    assertEquals(GameState.RUNNING, gs.get(svc));
    // listener should have seen the change
    assertTrue(seen.get());

    // stop the started game loop to avoid background threads
    Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
    loop.setAccessible(true);
    Object simpleGameLoop = loop.get(svc);
    if (simpleGameLoop != null) {
      simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
    }
  }

  @Test
  void testTriggeredByGameLoopInvokesNextRoundWhenAliensEmpty() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    // set gameState to RUNNING so nextRound can be called
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    AtomicBoolean nextRoundCalled = new AtomicBoolean(false);
    svc.addListener(new SpaceInvadersListener() {
      @Override public void updateBarrier(de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier barrier) {}
      @Override public void updateAliens(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien[] aliens) {}
      @Override public void updateShip(de.hhn.it.devtools.apis.spaceinvaders.entities.Ship ship) {}
      @Override public void updateProjectile(de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile projectile) {}
      @Override public void damageAlien(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien alien) {}
      @Override public void updateSound(de.hhn.it.devtools.apis.spaceinvaders.Sound sound) {}
      @Override public void changedGameState(GameState gameState) {if (gameState == GameState.PAUSED) nextRoundCalled.set(true); }
      @Override public void updateRound(int round) {}
      @Override public void gameEnded() {}
      @Override public void updateScore(int score) {}
      @Override public void updateGameConfiguration(de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration configuration) {}
    });

    // inject an EntityProvider stub whose getAliens returns empty map
    de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider stub =
        new de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider(svc) {
          @Override
          public java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> getAliens() {
            return new java.util.HashMap<>();
          }
        };

    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, stub);

    // call the game loop trigger
    svc.triggeredByGameLoop();

    assertTrue(nextRoundCalled.get(), "nextRound should be invoked when alien map is empty");

    // cleanup: stop game loop if created
    Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
    loop.setAccessible(true);
    Object simpleGameLoop = loop.get(svc);
    if (simpleGameLoop != null) simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
  }

  @Test
  void testTriggeredByGameLoopAbortsWhenPlayerZeroHP_New() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();


    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    AtomicReference<GameState> seenState = new AtomicReference<>();
    AtomicBoolean ended = new AtomicBoolean(false);
    svc.addListener(new SpaceInvadersListener() {
      @Override public void updateBarrier(de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier barrier) {}
      @Override public void updateAliens(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien[] aliens) {}
      @Override public void updateShip(de.hhn.it.devtools.apis.spaceinvaders.entities.Ship ship) {}
      @Override public void updateProjectile(de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile projectile) {}
      @Override public void damageAlien(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien alien) {}
      @Override public void updateSound(de.hhn.it.devtools.apis.spaceinvaders.Sound sound) {}
      @Override public void changedGameState(GameState gameState) { seenState.set(gameState); }
      @Override public void updateRound(int round) {}
      @Override public void gameEnded() { ended.set(true); }
      @Override public void updateScore(int score) {}
      @Override public void updateGameConfiguration(de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration configuration) {}
    });

    // stub EntityProvider with at least one alien and player with 0 HP
    de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider stub =
        new de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider(svc) {
          @Override
          public java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> getAliens() {
            java.util.HashMap<Integer, de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien> map = new java.util.HashMap<>();
            map.put(1, new de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien(
                new de.hhn.it.devtools.apis.spaceinvaders.Coordinate(0,0),
                de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType.BASIC,
                1));
            return map;
          }

          @Override public void updateProjectiles() {}
          @Override public void checkCollision() {}
          @Override public void updateAliens() {}
          @Override public de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip getPlayer() {
            de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip ship = super.getPlayer();
            ship.setHitPoints(0);
            return ship;
          }
        };

    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, stub);

    // invoke
    svc.triggeredByGameLoop();

    // assertions: state, provider cleared, listeners notified
    assertEquals(GameState.ABORTED, gs.get(svc));
    assertNull(ep.get(svc));
    assertEquals(GameState.ABORTED, seenState.get());
    assertTrue(ended.get());

    // cleanup: stop any game loop
    Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
    loop.setAccessible(true);
    Object simpleGameLoop = loop.get(svc);
    if (simpleGameLoop != null) simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
  }


}
