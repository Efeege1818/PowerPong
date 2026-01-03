package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.GameLoopService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleGameLoopService implements GameLoopService {

    private static final int DEFAULT_UPDATES_PER_SECOND = 60;

    private final Runnable updateCallback;
    private final Lock stateLock = new ReentrantLock();

    private ScheduledExecutorService executor;
    private ScheduledFuture<?> scheduledTask;

    private volatile boolean running;
    private volatile boolean paused;
    private int updatesPerSecond = DEFAULT_UPDATES_PER_SECOND;

    public SimpleGameLoopService(Runnable updateCallback) {
        if (updateCallback == null) {
            throw new IllegalArgumentException("Update callback cannot be null");
        }
        this.updateCallback = updateCallback;
    }

    @Override
    public void startLoop() {
        stateLock.lock();
        try {
            if (running) {
                throw new IllegalStateException("Game loop is already running");
            }

            running = true;
            paused = false;

            executor = Executors.newSingleThreadScheduledExecutor(r ->
                    new Thread(r, "GameLoop-Executor"));

            scheduleTask();
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void stopLoop() {
        stateLock.lock();
        try {
            if (!running) {
                throw new IllegalStateException("Game loop is not running");
            }

            running = false;
            paused = false;

            if (scheduledTask != null) {
                scheduledTask.cancel(true);
            }
            if (executor != null) {
                executor.shutdownNow();
            }

            scheduledTask = null;
            executor = null;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void pauseLoop() {
        stateLock.lock();
        try {
            if (!running) {
                throw new IllegalStateException("Game loop is not running");
            }
            if (paused) {
                throw new IllegalStateException("Game loop is already paused");
            }
            paused = true;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void resumeLoop() {
        stateLock.lock();
        try {
            if (!paused) {
                throw new IllegalStateException("Game loop is not paused");
            }
            paused = false;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setUpdateRate(int updatesPerSecond) {
        if (updatesPerSecond <= 0) {
            throw new IllegalArgumentException("Updates per second must be > 0");
        }

        stateLock.lock();
        try {
            this.updatesPerSecond = updatesPerSecond;

            // Reschedule if currently running
            if (running) {
                scheduledTask.cancel(false);
                scheduleTask();
            }
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public void executeSingleUpdate() {
        stateLock.lock();
        try {
            if (running) {
                throw new IllegalStateException("Cannot execute single update while loop is running");
            }
            updateCallback.run();
        } finally {
            stateLock.unlock();
        }
    }

    private void scheduleTask() {
        long periodNs = 1_000_000_000L / updatesPerSecond;

        scheduledTask = executor.scheduleAtFixedRate(() -> {
            if (!paused) {
                try {
                    updateCallback.run();
                } catch (Exception e) {
                    System.err.println("Error during game update: " + e.getMessage());
                }
            }
        }, 0, periodNs, TimeUnit.NANOSECONDS);
    }
}
