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

}
