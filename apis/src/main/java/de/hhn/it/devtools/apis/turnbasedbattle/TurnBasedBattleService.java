package de.hhn.it.devtools.apis.turnbasedbattle;

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


}
