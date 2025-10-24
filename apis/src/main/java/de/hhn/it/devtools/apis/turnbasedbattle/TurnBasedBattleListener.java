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
     * Informs the listener that the state has to be updated
     *
     * @param player1 gives the Player object of the first player to get necessary values
     * @param player2 gives the Player object of the second player to get necessary values
     *
     * @throws IllegalArgumentException if the provided player objects are null
     *
     */
    void updateState(Player player1, Player player2) throws IllegalArgumentException;
}
