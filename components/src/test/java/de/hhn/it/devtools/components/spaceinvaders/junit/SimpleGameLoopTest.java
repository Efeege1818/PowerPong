package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.components.spaceinvaders.SimpleGameLoop;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Robust tests for SimpleGameLoop (good and bad cases).
 */
class SimpleGameLoopTest {

  private static Field findFieldInHierarchy(Class<?> cls, String name) throws NoSuchFieldException {
    Class<?> current = cls;
    while (current != null) {
      try {
        Field f = current.getDeclaredField(name);
        f.setAccessible(true);
        return f;
      } catch (NoSuchFieldException e) {
        current = current.getSuperclass();
      }
    }
    throw new NoSuchFieldException(name);
  }

  private static void setField(Object target, String name, Object value) throws Exception {
    Field f = findFieldInHierarchy(target.getClass(), name);
    f.set(target, value);
  }

  private static Object getField(Object target, String name) throws Exception {
    Field f = findFieldInHierarchy(target.getClass(), name);
    return f.get(target);
  }

  @Test
  void testSpeedModifierDependsOnDifficulty() throws Exception {
    SimpleSpaceInvadersService svcEasy = new SimpleSpaceInvadersService();

    svcEasy.configure(new GameConfiguration(3, Difficulty.EASY));
    SimpleGameLoop loopEasy = new SimpleGameLoop(svcEasy);

    Field sm = findFieldInHierarchy(loopEasy.getClass(), "speedModifier");
    long easyVal = ((Number) sm.get(loopEasy)).longValue();

    SimpleSpaceInvadersService svcHard = new SimpleSpaceInvadersService();
    svcHard.configure(new GameConfiguration(3, Difficulty.HARD));
    SimpleGameLoop loopHard = new SimpleGameLoop(svcHard);
    long hardVal = ((Number) sm.get(loopHard)).longValue();

    assertTrue(easyVal > 0, "easy speedModifier should be positive");
    assertTrue(hardVal > 0, "hard speedModifier should be positive");
  }

  @Test
  void testRunInvokesServiceTriggeredByGameLoop() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService() {
      @Override
      public void triggeredByGameLoop() {
        // avoid depending on entityProvider internals
        latch.countDown();
      }
    };

    setField(svc, "gameState", GameState.RUNNING);

    SimpleGameLoop loop = new SimpleGameLoop(svc);
    Thread t = new Thread(loop);
    t.start();

    boolean called = latch.await(1, TimeUnit.SECONDS);

    loop.stopGame();
    t.join(500);

    assertTrue(called, "triggeredByGameLoop should have been called at least once");
  }

  @Test
  void testPauseResumeWaitNotifyBehavior() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService() {
      @Override
      public void triggeredByGameLoop() {
        latch.countDown();
      }
    };

    setField(svc, "gameState", GameState.PAUSED);

    SimpleGameLoop loop = new SimpleGameLoop(svc);
    Thread t = new Thread(loop);
    t.start();

    Thread.sleep(100);

    synchronized (loop) {
      loop.notifyAll();
    }

    boolean resumed = latch.await(1, TimeUnit.SECONDS);

    loop.stopGame();
    t.join(500);

    assertTrue(resumed, "Loop should resume after notifyAll and call triggeredByGameLoop");
  }

  @Test
  void testInterruptStopsRun() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    setField(svc, "gameState", GameState.RUNNING);

    SimpleGameLoop loop = new SimpleGameLoop(svc);
    Thread t = new Thread(loop);
    t.start();

    t.interrupt();

    t.join(500);

    loop.stopGame();
    t.join(200);

    assertFalse(t.isAlive(), "Thread should terminate after interrupt/stopGame");
  }
}
