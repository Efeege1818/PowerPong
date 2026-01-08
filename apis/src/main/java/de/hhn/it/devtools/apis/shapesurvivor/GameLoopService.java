package de.hhn.it.devtools.apis.shapesurvivor;

/**
 * Interface for managing the game loop thread that continuously updates game state.
 */
public interface GameLoopService {

    /**
     * Starts the game loop thread.
     * The loop will run continuously, updating game state at a fixed rate until stopped.
     *
     * @throws IllegalStateException if the game loop is already running
     */
    void startLoop() throws IllegalStateException;

    /**
     * Stops the game loop thread.
     *
     * @throws IllegalStateException if the game loop is not currently running
     */
    void stopLoop() throws IllegalStateException;

    /**
     * Pauses the game loop execution while keeping the thread alive.
     *
     * @throws IllegalStateException if the game loop is not running or already paused
     */
    void pauseLoop() throws IllegalStateException;

    /**
     * Resumes a paused game loop.
     *
     * @throws IllegalStateException if the game loop is not paused
     */
    void resumeLoop() throws IllegalStateException;

    /**
     * Checks if the game loop is currently running.
     *
     * @return true if the loop is actively executing, false otherwise
     */
    boolean isRunning();

    /**
     * Checks if the game loop is paused.
     *
     * @return true if the loop is paused, false otherwise
     */
    boolean isPaused();

    /**
     * Sets the target update rate for the game loop in updates per second.
     *
     * @param updatesPerSecond the desired number of updates per second
     * @throws IllegalArgumentException if updatesPerSecond is less than or equal to 0
     */
    void setUpdateRate(int updatesPerSecond) throws IllegalArgumentException;

    /**
     * Forces a single update cycle to execute immediately.
     * Useful for testing or manual stepping through game logic.
     *
     * @throws IllegalStateException if the game loop is currently running
     */
    void executeSingleUpdate() throws IllegalStateException;
}