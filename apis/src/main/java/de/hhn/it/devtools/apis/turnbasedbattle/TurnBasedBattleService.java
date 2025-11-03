package de.hhn.it.devtools.apis.turnbasedbattle;
/**
 * The main service interface for managing a turn-based battle game.
 */
public interface TurnBasedBattleService {

    /**
     * Resets the game. The new game state is READY. Reset means reset, no matter in which
     * GameState the game is.
     */
    void reset();

    /**
     * Starts the game if the GameState is READY. The new GameState is RUNNING.
     *
     * @throws IllegalStateException if the GameState was not READY.
     */
    void start() throws IllegalStateException;

    /**
     * Pauses the current game if the GameState is RUNNING. The new GameState is PAUSED.
     *
     * @throws IllegalStateException if the GameState is READY.
     */
    void pause() throws IllegalStateException;

    /**
     * Aborts the current game if the GameState is RUNNING. The new GameState is ABORTED.
     *
     * @throws IllegalStateException if the GameState is READY.
     */
    void abort() throws IllegalStateException;

    /**
     * Ends the current game if the GameState is RUNNING. The new GameState is END.
     *
     * @throws IllegalStateException if the GameState is READY.
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
     * This method should be called whenever the active player/monster switches
     * to update UI, logics, or other observers of the new turn state.
     */
    void notifyListenersTurnChanged();

    /**
     * Notifies all registered listeners that the battle has ended.
     * This method should be called when the battle reaches a terminal state,
     * for example when one monster has fainted, so listeners can update UI,
     * declare a winner, or perform cleanup tasks.
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
     * Executes the currently selected actions for the active player’s monster.
     * This method applies the chosen move or command, calculates its effects,
     * and updates the state of both monsters accordingly.
     *
     * @param move which move is played.
     * @throws IllegalStateException if the game is not running or if the turn
     *                               cannot be executed at the current time.
     */
    void executeTurn(int move) throws IllegalStateException;

    /**
     * Switches to the next player's turn.
     * If a turn has ended successfully, this method sets the next player as the
     * current player, unless the battle has already finished.
     *
     * @throws IllegalStateException if the game is not running or the battle is already over.
     */
    void nextTurn() throws IllegalStateException;

    /**
     * Returns the player who is currently allowed to act.
     *
     * @return the Player whose turn it is.
     */
    Player getCurrentPlayer();

    /**
     * Returns the Player 1 Object
     * @return PLayer 1
     */
    Player getPlayer1();

    /**
     * Returns the Player 2 Object
     * @return PLayer 2
     */
    Player getPlayer2();

    /**
     * Checks whether the battle has ended.
     * A battle is considered over when one of the monsters has fainted or when
     * an end condition is reached.
     *
     * @return true if the battle is over, otherwise false.
     */
    boolean isBattleOver();

    /**
     * Returns the winning player of the battle.
     * If the battle is not yet over, this method returns null.
     *
     * @return the Player who won the battle, or null if the
     *         battle is still ongoing.
     */
    Player getWinner();
}
