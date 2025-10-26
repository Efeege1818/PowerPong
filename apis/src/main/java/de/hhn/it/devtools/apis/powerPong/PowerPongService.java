package de.hhn.it.devtools.apis.powerPong;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;

public interface PowerPongService {
    /**
     * Starts a new game or resets the current one.
     *
     * @param mode The selected game mode (e.g., Classic, PowerUp).
     * @throws GameLogicException if the game cannot be started.
     */
    void startGame(GameMode mode) throws GameLogicException;

    /**
     * Calculates the next game state (frame).
     * This method should ideally be called by the UI
     * on every frame of the game loop (e.g., from a JavaFX AnimationTimer's handle method).
     *
     * @param input The current player inputs (key presses) for both paddles, typically updated via
     *              JavaFX {@code Scene#setOnKeyPressed} / {@code setOnKeyReleased} handlers.
     * @throws GameLogicException if an error occurs during game state calculation.
     */
    void updateGame(PlayerInput input) throws GameLogicException;

    /**
     * Retrieves the *current* state of the entire game.
     * The UI uses this object to render the playing field,
     * paddles, ball, and score.
     *
     * @return An (immutable) GameState object representing
     * the complete state of the game.
     */
    GameState getGameState();

    /**
     * Pauses or resumes the game (nice-to-have feature).
     *
     * @param isPaused true to pause the game, false to resume it.
     */
    void setPaused(boolean isPaused);

    /**
     * Registers a {@link PowerPongListener} so the UI can react to events (Observer pattern).
     *
     * @param listener listener instance; silently ignored when null or already registered.
     */
    void addListener(PowerPongListener listener);

    /**
     * Removes a previously registered {@link PowerPongListener}.
     *
     * @param listener listener instance to remove.
     */
    void removeListener(PowerPongListener listener);
}
