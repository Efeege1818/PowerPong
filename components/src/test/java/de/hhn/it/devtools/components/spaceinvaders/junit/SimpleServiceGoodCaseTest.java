package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.components.spaceinvaders.SimpleGameLoop;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;



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

    TestSpaceInvadersListener listener = new TestSpaceInvadersListener();
    svc.addListener(listener);

    try {
      svc.start();

      // after start the gameState field should be RUNNING
      assertEquals(GameState.RUNNING, gs.get(svc));
      // listener should have seen the change
      assertEquals(GameState.RUNNING, listener.lastState.get());
    } finally {
      // stop the started game loop to avoid background threads
      Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
      loop.setAccessible(true);
      Object simpleGameLoop = loop.get(svc);
      if (simpleGameLoop != null) {
        simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
      }
    }
  }

  @Test
  void testTriggeredByGameLoopInvokesNextRoundWhenAliensEmpty() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    // set gameState to RUNNING so nextRound can be called
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    TestSpaceInvadersListener listener = new TestSpaceInvadersListener();
    svc.addListener(listener);

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

    try {
      // call the game loop trigger
      svc.triggeredByGameLoop();

      assertEquals(GameState.PAUSED, listener.lastState.get(), "nextRound should be invoked when alien map is empty (PAUSED expected)");
    } finally {
      // cleanup: stop game loop if created
      Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
      loop.setAccessible(true);
      Object simpleGameLoop = loop.get(svc);
      if (simpleGameLoop != null) simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
    }
  }

  @Test
  void testTriggeredByGameLoopAbortsWhenPlayerZeroHP_New() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();


    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    TestSpaceInvadersListener listener = new TestSpaceInvadersListener();
    svc.addListener(listener);

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

    try {
      // invoke
      svc.triggeredByGameLoop();

      // assertions: state, provider cleared, listeners notified
      assertEquals(GameState.ABORTED, gs.get(svc));
      assertNull(ep.get(svc));
      assertEquals(GameState.ABORTED, listener.lastState.get());
      assertTrue(listener.gameEnded.get());
    } finally {
      // cleanup: stop any game loop
      Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
      loop.setAccessible(true);
      Object simpleGameLoop = loop.get(svc);
      if (simpleGameLoop != null) simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
    }
  }

  @Test
  void testNextRoundTransitionsAndCallsEntityProvider() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    // set private gameState to PAUSED so nextRound() is allowed
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.PAUSED);

    // ensure simpleGameLoop is non-null to avoid NPE when nextRound synchronizes on it
    Field loopField = svc.getClass().getDeclaredField("simpleGameLoop");
    loopField.setAccessible(true);
    SimpleGameLoop loopInstance = new SimpleGameLoop(svc);
    loopField.set(svc, loopInstance);

    // stub EntityProvider to capture calls
    AtomicBoolean generateAliensCalled = new AtomicBoolean(false);
    AtomicBoolean clearProjectilesCalled = new AtomicBoolean(false);

    de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider stub =
        new de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider(svc) {
          @Override
          public void generateAliens() {
            generateAliensCalled.set(true);
          }

          @Override
          public void clearProjectiles() {
            clearProjectilesCalled.set(true);
          }
        };

    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, stub);

    // listener to capture state change and round number
    TestSpaceInvadersListener listener = new TestSpaceInvadersListener();
    svc.addListener(listener);

    // read initial round
    Field roundField = svc.getClass().getDeclaredField("round");
    roundField.setAccessible(true);
    int initialRound = (int) roundField.get(svc);

    // invoke nextRound
    svc.nextRound();

    // assertions: gameState -> RUNNING, round incremented, listener called and entityProvider methods invoked
    assertEquals(GameState.RUNNING, gs.get(svc));
    assertEquals(initialRound + 1, (int) roundField.get(svc));
    assertEquals(GameState.RUNNING, listener.lastState.get(), "changedGameState should have been called");
    assertEquals(initialRound + 1, listener.lastRound.get(), "updateRound should be called with incremented round");
    assertTrue(generateAliensCalled.get(), "generateAliens() should be called");
    assertTrue(clearProjectilesCalled.get(), "clearProjectiles() should be called");
  }

  @Test
  void testRemoveListenerStopsNotifications() {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    TestSpaceInvadersListener listenerA = new TestSpaceInvadersListener();
    TestSpaceInvadersListener listenerB = new TestSpaceInvadersListener();

    svc.addListener(listenerA);
    svc.addListener(listenerB);

    // remove listenerA and trigger a sound event
    assertTrue(svc.removeListener(listenerA), "removeListener should return true when removing an existing listener");

    svc.playSound(de.hhn.it.devtools.apis.spaceinvaders.Sound.SHOOT);

    // listenerA should NOT be notified, listenerB should
    assertFalse(listenerA.soundUpdated.get(), "Removed listener should not receive notifications");
    assertTrue(listenerB.soundUpdated.get(), "Remaining listener should receive notifications");
  }

}
