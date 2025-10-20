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

    /**
     * Updates the position of both paddles based on player input.
     * Called once per frame during the game loop.
     */
    void updatePaddle();

    /**
     * Updates the position of the ball based on its current velocity and direction.
     * Handles collisions with paddles and walls, and detects when a player scores.
     * This method should be called once per frame as part of the game loop.
     *
     * @throws GameLogicException if the ball update fails due to invalid state.
     */
    void updateBall() throws GameLogicException;

    /**
     * Updates the current score based on the game state.
     * Typically called when a player scores (e.g., the ball leaves the field).
     * May also apply custom scoring rules depending on the game mode.
     *
     * @throws GameLogicException if the score cannot be updated due to an invalid state.
     */
    void updateScore() throws GameLogicException;
}

