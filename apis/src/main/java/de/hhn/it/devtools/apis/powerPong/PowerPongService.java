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
     * on every frame of the game loop.
     *
     * @param input        The current player inputs.
     * @param deltaSeconds The time elapsed since the last frame in seconds.
     * @throws GameLogicException if an error occurs during game state calculation.
     */
    void updateGame(PlayerInput input, double deltaSeconds) throws GameLogicException;

    /**
     * Legacy update method assuming fixed 60Hz.
     * 
     * @deprecated Use {@link #updateGame(PlayerInput, double)} instead.
     */
    @Deprecated
    void updateGame(PlayerInput input) throws GameLogicException;

    /**
     * Retrieves the *current* state of the entire game.
     * The UI uses this object to render the playing field,
     * paddles, ball, and score.
     *
     * @return An (immutable) GameState object representing
     *         the complete state of the game.
     */
    GameState getGameState();

    /**
     * Pauses or resumes the game (nice-to-have feature).
     *
     * @param isPaused true to pause the game, false to resume it.
     */
    void setPaused(boolean isPaused);

    /**
     * Forces the game to stop immediately and transitions into a terminal
     * {@link GameStatus}.
     * Implementations should use this when the user quits from the menu or the
     * application closes.
     */
    void endGame();

    /**
     * Registers a {@link PowerPongListener} so the UI can react to events (Observer
     * pattern).
     *
     * @param listener listener instance; silently ignored when null or already
     *                 registered.
     */
    void addListener(PowerPongListener listener);

    /**
     * Removes a previously registered {@link PowerPongListener}.
     *
     * @param listener listener instance to remove.
     */
    void removeListener(PowerPongListener listener);

    /**
     * Checks if a player currently has an active shield.
     * 
     * @param player 1 for left player, 2 for right player
     * @return true if shield is active
     */
    boolean hasShield(int player);
}
