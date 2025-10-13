package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Models the capabilities of a TurnBasedBattleListener.
 */
public interface TurnBasedBattleListener {

    /**
     * Informs the listener that the game state has changed.
     *
     * @param gameState new state of the game
     */
    void newGameState(GameState gameState);

    /**
     * Informs the players that the game has ended.
     */
    void gameEnded();

    /**
     * Informs the listener that the health has changed.
     */
    void updateHealth(int health);
}
