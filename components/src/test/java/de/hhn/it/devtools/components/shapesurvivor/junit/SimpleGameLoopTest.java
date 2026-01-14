package de.hhn.it.devtools.components.shapesurvivor.junit;

import de.hhn.it.devtools.apis.shapesurvivor.GameLoopService;
import de.hhn.it.devtools.components.shapesurvivor.SimpleGameLoopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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

        // Wait for some updates
        Thread.sleep(100);

        assertTrue(updateCounter.get() > 0, "Update callback should have been called");

        gameLoop.stopLoop();
    }

    @Test
    @DisplayName("Cannot start loop that is already running")
    void testStartLoopAlreadyRunning() {
        gameLoop.startLoop();

        assertThrows(IllegalStateException.class, () -> gameLoop.startLoop());

        gameLoop.stopLoop();
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

        // Wait a bit to ensure no more updates
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

        // Counter should not increase while paused
        assertEquals(countAfterPause, updateCounter.get(),
                "Update counter should not increase while paused");

        gameLoop.stopLoop();
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

        gameLoop.stopLoop();
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

        gameLoop.stopLoop();
    }

    @Test
    @DisplayName("Cannot resume loop that is not paused")
    void testResumeLoopNotPaused() {
        gameLoop.startLoop();

        assertThrows(IllegalStateException.class, () -> gameLoop.resumeLoop());

        gameLoop.stopLoop();
    }

    @Test
    @DisplayName("Update rate can be changed")
    void testSetUpdateRate() throws InterruptedException {
        gameLoop.setUpdateRate(10); // 10 updates per second
        gameLoop.startLoop();

        Thread.sleep(500);
        int countAtLowRate = updateCounter.get();

        gameLoop.stopLoop();

        // Should be approximately 5 updates (10 per second * 0.5 seconds)
        assertTrue(countAtLowRate >= 3 && countAtLowRate <= 7,
                "Expected ~5 updates at 10 Hz, got " + countAtLowRate);
    }

    @Test
    @DisplayName("Update rate can be changed while running")
    void testSetUpdateRateWhileRunning() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(50);

        int countBefore = updateCounter.get();

        gameLoop.setUpdateRate(10); // Slow down
        Thread.sleep(200);

        int countAfter = updateCounter.get();
        int updatesAfterChange = countAfter - countBefore;

        // Should be approximately 2 updates (10 per second * 0.2 seconds)
        assertTrue(updatesAfterChange >= 1 && updatesAfterChange <= 4,
                "Expected ~2 updates at reduced rate, got " + updatesAfterChange);

        gameLoop.stopLoop();
    }

    @Test
    @DisplayName("Cannot set invalid update rate")
    void testSetInvalidUpdateRate() {
        assertThrows(IllegalArgumentException.class,
                () -> gameLoop.setUpdateRate(0));
        assertThrows(IllegalArgumentException.class,
                () -> gameLoop.setUpdateRate(-1));
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

        assertThrows(IllegalStateException.class,
                () -> gameLoop.executeSingleUpdate());

        gameLoop.stopLoop();
    }

    @Test
    @DisplayName("Game loop handles exceptions in callback gracefully")
    void testExceptionHandling() throws InterruptedException {
        AtomicInteger successfulUpdates = new AtomicInteger(0);
        AtomicInteger callCount = new AtomicInteger(0);

        GameLoopService faultyLoop = new SimpleGameLoopService(() -> {
            callCount.incrementAndGet();
            if (callCount.get() % 2 == 0) {
                throw new RuntimeException("Test exception");
            }
            successfulUpdates.incrementAndGet();
        });

        faultyLoop.startLoop();
        Thread.sleep(100);
        faultyLoop.stopLoop();

        // Loop should continue despite exceptions
        assertTrue(callCount.get() > 2, "Loop should continue after exceptions");
        assertTrue(successfulUpdates.get() > 0, "Some updates should succeed");
    }

    @Test
    @DisplayName("Game loop runs at approximately correct frequency")
    void testUpdateFrequency() throws InterruptedException {
        gameLoop.setUpdateRate(60); // 60 FPS

        long startTime = System.currentTimeMillis();
        gameLoop.startLoop();

        Thread.sleep(1000); // Run for 1 second

        gameLoop.stopLoop();
        long endTime = System.currentTimeMillis();

        int updates = updateCounter.get();
        long duration = endTime - startTime;

        // Should be approximately 60 updates in 1 second
        // Allow 20% tolerance for timing variance
        assertTrue(updates >= 48 && updates <= 72,
                "Expected ~60 updates in 1 second, got " + updates);
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

            // Should not increase while paused
            assertEquals(countBeforePause, updateCounter.get());

            gameLoop.resumeLoop();
            Thread.sleep(50);

            // Should increase after resume
            assertTrue(updateCounter.get() > countBeforePause);
        }

        gameLoop.stopLoop();
    }

    @Test
    @DisplayName("Game loop cleanup is complete after stop")
    void testCleanupAfterStop() throws InterruptedException {
        gameLoop.startLoop();
        Thread.sleep(100);
        gameLoop.stopLoop();

        // Should be able to start again after stopping
        int previousCount = updateCounter.get();

        gameLoop.startLoop();
        Thread.sleep(100);
        gameLoop.stopLoop();

        assertTrue(updateCounter.get() > previousCount,
                "Loop should work correctly after restart");
    }

    @Test
    @DisplayName("High frequency updates work correctly")
    void testHighFrequencyUpdates() throws InterruptedException {
        gameLoop.setUpdateRate(120); // 120 FPS
        gameLoop.startLoop();

        Thread.sleep(500);
        gameLoop.stopLoop();

        int updates = updateCounter.get();

        // Should be approximately 60 updates (120 per second * 0.5 seconds)
        // Allow wider tolerance for high frequency
        assertTrue(updates >= 40 && updates <= 80,
                "Expected ~60 updates at 120 Hz, got " + updates);
    }

    @Test
    @DisplayName("Low frequency updates work correctly")
    void testLowFrequencyUpdates() throws InterruptedException {
        gameLoop.setUpdateRate(5); // 5 FPS
        gameLoop.startLoop();

        Thread.sleep(1000);
        gameLoop.stopLoop();

        int updates = updateCounter.get();

        // Should be approximately 5 updates (5 per second * 1 second)
        assertTrue(updates >= 3 && updates <= 7,
                "Expected ~5 updates at 5 Hz, got " + updates);
    }

    @Test
    @DisplayName("Paused state persists through multiple checks")
    void testPausedStatePersistence() throws InterruptedException {
        gameLoop.startLoop();
        gameLoop.pauseLoop();

        for (int i = 0; i < 5; i++) {
            assertTrue(gameLoop.isPaused());
            assertTrue(gameLoop.isRunning());
            Thread.sleep(20);
        }

        gameLoop.stopLoop();
    }
}