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
     * Informs the listener that the game has ended.
     *
     * @param winnerPlayerNumber the number of the player who won the game.
     *                           Must be 1 or 2.
     * @throws IllegalArgumentException if the provided player number is not 1 or 2.
     */
    void gameEnded(int winnerPlayerNumber) throws IllegalArgumentException;

    /**
     * Informs the listener that the health has changed.
     *
     * @param health the updated health value of the affected monster
     */
    void updateHealth(int health);
}
