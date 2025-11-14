package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * The main service interface for managing a turn-based battle game.
 * This service is responsible for managing game state, players, and listeners.
 */
public interface TurnBasedBattleService {

    /**
     * Resets the game. The new game state is READY.
     * Reset means reset, no matter in which GameState the game currently is.
     */
    void reset();

    /**
     * Starts the game if the GameState is READY. The new GameState is RUNNING.
     *
     * @throws IllegalStateException if the GameState was not READY or players not set.
     */
    void start() throws IllegalStateException;

    /**
     * Pauses the current game if the GameState is RUNNING. The new GameState is PAUSED.
     *
     * @throws IllegalStateException if the GameState is not RUNNING.
     */
    void pause() throws IllegalStateException;

    /**
     * Aborts the current game if the GameState is RUNNING. The new GameState is ABORTED.
     *
     * @throws IllegalStateException if the GameState is not RUNNING.
     */
    void abort() throws IllegalStateException;

    /**
     * Ends the current game if the GameState is RUNNING. The new GameState is END.
     *
     * @throws IllegalStateException if the GameState is not RUNNING.
     */
    void end() throws IllegalStateException;

    /**
     * Adds a listener for game updates. Each listener can only be added once.
     *
     * @param listener listener to be added.
     * @return true if the listener could be added. Otherwise, false.
     * @throws IllegalArgumentException if the given listener is null.
     * @throws IllegalStateException if the listener has already been added before.
     */
    boolean addListener(TurnBasedBattleListener listener)
            throws IllegalArgumentException, IllegalStateException;

    /**
     * Removes a listener from the list of listeners.
     *
     * @param listener listener to be removed.
     * @return true if the listener could be removed. Otherwise, false.
     */
    boolean removeListener(TurnBasedBattleListener listener);

    /**
     * Notifies all registered listeners that the turn has changed.
     */
    void notifyListenersTurnChanged();

    /**
     * Notifies all registered listeners that the battle has ended.
     */
    void notifyListenersBattleEnded();

    /**
     * Returns the current game state.
     *
     * @return the current GameState of the battle.
     */
    GameState getGameState();

    /**
     * Initializes the two players and their respective monsters for the battle.
     * Can only be called when the game is in the READY state.
     *
     * @param player1 the first player.
     * @param player2 the second player.
     * @throws IllegalStateException if the game is not in the READY state.
     */
    void setupPlayers(Player player1, Player player2)
            throws IllegalStateException;

    /**
     * Executes the currently selected move for the active player's monster.
     *
     * @param move index of the move to execute.
     * @throws IllegalStateException if the game is not running or the move cannot be executed.
     */
    void executeTurn(int move) throws IllegalStateException;

    /**
     * Switches to the next player's turn.
     *
     * @throws IllegalStateException if the game is not running or the battle is over.
     */
    void nextTurn() throws IllegalStateException;

    /**
     * Returns the player who is currently allowed to act.
     *
     * @return the Player whose turn it is.
     */
    Player getCurrentPlayer();

    /**
     * Returns the Player 1 object.
     *
     * @return Player 1
     */
    Player getPlayer1();

    /**
     * Returns the Player 2 object.
     *
     * @return Player 2
     */
    Player getPlayer2();

    /**
     * Checks whether the battle has ended.
     *
     * @return true if the battle is over, otherwise false.
     */
    boolean isBattleOver();

    /**
     * Returns the current turn count.
     *
     * @return current turn
     */
    int getTurnCount();

    /**
     * Returns the winning player of the battle.
     * If the battle is not yet over, this method returns null.
     *
     * @return the Player who won the battle, or null if still ongoing.
     */
    Player getWinner();

    Player determineStartingPlayer();
}
