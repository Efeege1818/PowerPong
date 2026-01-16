package de.hhn.it.devtools.components.shapesurvivor.junit;

import de.hhn.it.devtools.apis.shapesurvivor.GameLoopService;
import de.hhn.it.devtools.components.shapesurvivor.SimpleGameLoopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SimpleGameLoopServiceTest {

    private AtomicInteger updateCounter;
    private GameLoopService gameLoop;

    @BeforeEach
    void setUp() {
        updateCounter = new AtomicInteger(0);
        gameLoop = new SimpleGameLoopService(() -> updateCounter.incrementAndGet());
    }

    @AfterEach
    void tearDown() {
        // Ensure loop is stopped after each test
        try {
            if (gameLoop.isRunning()) {
                gameLoop.stopLoop();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    @Test
    @DisplayName("Game loop initializes in stopped state")
    void testInitialState() {
        assertFalse(gameLoop.isRunning());
        assertFalse(gameLoop.isPaused());
    }

    @Test
    @DisplayName("Cannot create game loop with null callback")
    void testNullCallback() {
        assertThrows(IllegalArgumentException.class,
            () -> new SimpleGameLoopService(null));
    }

    @Test
    @DisplayName("Game loop can be started")
    void testStartLoop() throws InterruptedException {
        gameLoop.startLoop();

        assertTrue(gameLoop.isRunning());
        assertFalse(gameLoop.isPaused());

        Thread.sleep(100);
        assertTrue(updateCounter.get() > 0, "Update callback should have been called");
    }

    @Test
    @DisplayName("Cannot start loop that is already running")
    void testStartLoopAlreadyRunning() {
        gameLoop.startLoop();
        assertThrows(IllegalStateException.class, () -> gameLoop.startLoop());
    }

    @Test
    @DisplayName("Game loop can be stopped")
    void testStopLoop() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(50);

        int countBeforeStop = updateCounter.get();
        gameLoop.stopLoop();

        assertFalse(gameLoop.isRunning());
        assertFalse(gameLoop.isPaused());

        Thread.sleep(100);
        assertEquals(countBeforeStop, updateCounter.get(),
            "No updates should occur after stopping");
    }

    @Test
    @DisplayName("Cannot stop loop that is not running")
    void testStopLoopNotRunning() {
        assertThrows(IllegalStateException.class, () -> gameLoop.stopLoop());
    }

    @Test
    @DisplayName("Game loop can be paused")
    void testPauseLoop() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(50);

        gameLoop.pauseLoop();

        assertTrue(gameLoop.isRunning());
        assertTrue(gameLoop.isPaused());

        int countAfterPause = updateCounter.get();
        Thread.sleep(100);

        assertEquals(countAfterPause, updateCounter.get(),
            "Update counter should not increase while paused");
    }

    @Test
    @DisplayName("Cannot pause loop that is not running")
    void testPauseLoopNotRunning() {
        assertThrows(IllegalStateException.class, () -> gameLoop.pauseLoop());
    }

    @Test
    @DisplayName("Cannot pause loop that is already paused")
    void testPauseLoopAlreadyPaused() {
        gameLoop.startLoop();
        gameLoop.pauseLoop();
        assertThrows(IllegalStateException.class, () -> gameLoop.pauseLoop());
    }

    @Test
    @DisplayName("Game loop can be resumed after pause")
    void testResumeLoop() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(50);

        gameLoop.pauseLoop();
        int countAfterPause = updateCounter.get();

        Thread.sleep(50);
        assertEquals(countAfterPause, updateCounter.get());

        gameLoop.resumeLoop();

        assertTrue(gameLoop.isRunning());
        assertFalse(gameLoop.isPaused());

        Thread.sleep(50);
        assertTrue(updateCounter.get() > countAfterPause,
            "Updates should resume after calling resume");
    }

    @Test
    @DisplayName("Cannot resume loop that is not paused")
    void testResumeLoopNotPaused() {
        gameLoop.startLoop();
        assertThrows(IllegalStateException.class, () -> gameLoop.resumeLoop());
    }

    @Test
    @DisplayName("Update rate can be changed")
    void testSetUpdateRate() throws InterruptedException {
        gameLoop.setUpdateRate(10);
        gameLoop.startLoop();

        Thread.sleep(500);
        int count = updateCounter.get();

        gameLoop.stopLoop();

        assertTrue(count >= 3 && count <= 8,
            "Expected ~5 updates at 10 Hz, got " + count);
    }

    @Test
    @DisplayName("Update rate can be changed while running")
    void testSetUpdateRateWhileRunning() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(50);

        gameLoop.setUpdateRate(10);
        updateCounter.set(0);

        Thread.sleep(500);
        int count = updateCounter.get();

        gameLoop.stopLoop();

        assertTrue(count >= 3 && count <= 8,
            "Expected ~5 updates at 10 Hz after rate change, got " + count);
    }

    @Test
    @DisplayName("Cannot set invalid update rate")
    void testSetInvalidUpdateRate() {
        assertThrows(IllegalArgumentException.class, () -> gameLoop.setUpdateRate(0));
        assertThrows(IllegalArgumentException.class, () -> gameLoop.setUpdateRate(-1));
    }

    @Test
    @DisplayName("Single update can be executed when loop is stopped")
    void testExecuteSingleUpdate() {
        assertEquals(0, updateCounter.get());

        gameLoop.executeSingleUpdate();
        assertEquals(1, updateCounter.get());

        gameLoop.executeSingleUpdate();
        gameLoop.executeSingleUpdate();
        assertEquals(3, updateCounter.get());
    }

    @Test
    @DisplayName("Cannot execute single update while loop is running")
    void testExecuteSingleUpdateWhileRunning() {
        gameLoop.startLoop();
        assertThrows(IllegalStateException.class, () -> gameLoop.executeSingleUpdate());
    }

    @Test
    @DisplayName("Game loop handles exceptions in callback gracefully")
    void testExceptionHandling() throws InterruptedException {
        AtomicInteger callCount = new AtomicInteger(0);

        GameLoopService faultyLoop = new SimpleGameLoopService(() -> {
            callCount.incrementAndGet();
            if (callCount.get() % 2 == 0) {
                throw new RuntimeException("Test exception");
            }
        });

        faultyLoop.startLoop();
        Thread.sleep(100);
        faultyLoop.stopLoop();

        assertTrue(callCount.get() > 2, "Loop should continue after exceptions");
    }

    @Test
    @DisplayName("Multiple pause/resume cycles work correctly")
    void testMultiplePauseResumeCycles() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(50);

        for (int i = 0; i < 3; i++) {
            int countBeforePause = updateCounter.get();

            gameLoop.pauseLoop();
            Thread.sleep(50);
            assertEquals(countBeforePause, updateCounter.get());

            gameLoop.resumeLoop();
            Thread.sleep(50);
            assertTrue(updateCounter.get() > countBeforePause);
        }
    }

    @Test
    @DisplayName("Game loop cleanup is complete after stop")
    void testCleanupAfterStop() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(100);
        gameLoop.stopLoop();

        int previousCount = updateCounter.get();

        gameLoop.startLoop();
        Thread.sleep(100);
        gameLoop.stopLoop();

        assertTrue(updateCounter.get() > previousCount,
            "Loop should work correctly after restart");
    }
}