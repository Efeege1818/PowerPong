package de.hhn.it.devtools.apis.powerPong;

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
     * @param input The current player inputs (key presses) for both players.
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

}

