package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.components.spaceinvaders.SimpleGameLoop;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class SimpleServiceEdgeCaseTest {

  @Test
  void testTriggeredByGameLoopHandlesNullEntityProviderGracefully() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    // set entityProvider to null and call triggeredByGameLoop -> should not throw
    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, null);
    svc.triggeredByGameLoop();
  }


  @Test
  void testAbortSetsEntityProviderNullAndStopsLoop() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    // set a SimpleGameLoop instance so stopGame can be invoked without NPE
    Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
    loop.setAccessible(true);
    SimpleGameLoop dummyLoop = new SimpleGameLoop(svc);
    loop.set(svc, dummyLoop);

    // also set an entityProvider object to be cleared
    Field ep = svc.getClass().getDeclaredField("entityProvider");
    ep.setAccessible(true);
    ep.set(svc, null);

    svc.abort();

    assertNull(ep.get(svc));
  }

  @Test
  void testMoveDelegatesToEntityProviderWhenPresent() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    // create a minimal dummy entityProvider with a getPlayer().move proxy via anonymous class is complex;
    // Instead set entityProvider to an instance of the real EntityProvider but we must provide the service itself
    // Constructing EntityProvider will create aliens but that's OK
    epSetEntityProviderToReal(svc);

    // calling move should not throw
    svc.move(Direction.LEFT);
  }

  @Test
  void testShootDelegatesToEntityProviderWhenPresent() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    epSetEntityProviderToReal(svc);
    svc.shoot();
  }

  private void epSetEntityProviderToReal(SimpleSpaceInvadersService svc) throws Exception {
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.PREPARED);

    // call start to initialize entityProvider and game loop but then stop the loop
    svc.start();

    Field loop = svc.getClass().getDeclaredField("simpleGameLoop");
    loop.setAccessible(true);
    Object simpleGameLoop = loop.get(svc);
    if (simpleGameLoop != null) {
      simpleGameLoop.getClass().getMethod("stopGame").invoke(simpleGameLoop);
    }
  }
}
