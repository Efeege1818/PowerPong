package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.components.spaceinvaders.SimpleGameLoop;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class SimpleServiceResumeResetTest {

  private static class TestLoop extends SimpleGameLoop {
    private final AtomicBoolean stopCalled;

    TestLoop(SimpleSpaceInvadersService svc, AtomicBoolean stopCalled) {
      super(svc);
      this.stopCalled = stopCalled;
    }

    @Override
    public void stopGame() {
      stopCalled.set(true);
    }
  }

  @Test
  void testResetStopsGameLoopAndSetsPrepared() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    // set state to RUNNING so reset is sensible
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    AtomicBoolean stopCalled = new AtomicBoolean(false);
    TestLoop loop = new TestLoop(svc, stopCalled);

    Field loopField = svc.getClass().getDeclaredField("simpleGameLoop");
    loopField.setAccessible(true);
    loopField.set(svc, loop);

    // call reset
    svc.reset();

    assertEquals(GameState.PREPARED, gs.get(svc));
    assertTrue(stopCalled.get(), "reset() should call stopGame() on the game loop");
  }

  @Test
  void testResumeFromPausedNotifiesAndWakesLoop() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();

    // set private gameState to PAUSED so resume() is allowed
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.PAUSED);

    // create a loop instance to synchronize on
    Field loopField = svc.getClass().getDeclaredField("simpleGameLoop");
    loopField.setAccessible(true);
    SimpleGameLoop loopInstance = new SimpleGameLoop(svc);
    loopField.set(svc, loopInstance);

    // prepare a waiter thread that waits on the loopInstance
    AtomicBoolean awakened = new AtomicBoolean(false);
    Thread waiter = new Thread(() -> {
      synchronized (loopInstance) {
        try {
          loopInstance.wait(2000);
          awakened.set(true);
        } catch (InterruptedException e) {
          // ignore
        }
      }
    });
    waiter.start();

    // small pause to ensure waiter is waiting
    Thread.sleep(50);

    TestSpaceInvadersListener listener = new TestSpaceInvadersListener();
    svc.addListener(listener);

    // call resume; this should notify the waiting thread and set gameState to RUNNING
    svc.resume();

    // wait for waiter to finish
    waiter.join(1000);

    assertEquals(GameState.RUNNING, gs.get(svc));
    assertEquals(GameState.RUNNING, listener.lastState.get());
    assertTrue(awakened.get(), "resume() should wake up a thread waiting on the game loop object");
  }

  @Test
  void testResumeWhenNotPausedThrows() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    Field gs = svc.getClass().getDeclaredField("gameState");
    gs.setAccessible(true);
    gs.set(svc, GameState.RUNNING);

    assertThrows(IllegalStateException.class, svc::resume);
  }
}
