package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.GameState;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;
import de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleListener;

/**
 * Test helper implementation of TurnBasedBattleListener.
 *
 * This listener is intended to be used in component tests to
 * observe changes in game state, player health and the winner.
 */
public class TestTurnBasedBattleListener implements TurnBasedBattleListener {

    private volatile GameState currentState = GameState.READY;
    private volatile int player1Health;
    private volatile int player2Health;
    private volatile Integer winnerPlayerNumber;

    /**
     * Stores the current game state so tests can assert on it.
     *
     * @param gameState the new game state; must not be null
     * @throws IllegalArgumentException if gameState is null
     */
    @Override
    public void newGameState(final GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("gameState must not be null");
        }
        this.currentState = gameState;
    }

    /**
     * Updates the health snapshot of both players.
     *
     * @param player1 the first player, must not be  null
     * @param player2 the second player, must not be null
     * @throws IllegalArgumentException if a player is null or has no monster
     */
    @Override
    public void updateState(final Player player1, final Player player2)
            throws IllegalArgumentException {

        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("player1 and player2 must not be null");
        }
        if (player1.monster() == null || player2.monster() == null) {
            throw new IllegalArgumentException("Each player must have an assigned monster");
        }

        player1Health = player1.monster().currentHp();
        player2Health = player2.monster().currentHp();
    }

    /**
     * Stores the winner player number for later assertions.
     *
     * @param winnerPlayerNumber the number of the player who won (1 or 2)
     * @throws IllegalArgumentException if winnerPlayerNumber is not 1 or 2
     */
    @Override
    public void gameEnded(final int winnerPlayerNumber) {
        if (winnerPlayerNumber != 1 && winnerPlayerNumber != 2) {
            throw new IllegalArgumentException("winnerPlayerNumber must be 1 or 2");
        }
        this.winnerPlayerNumber = winnerPlayerNumber;
    }


    public GameState getCurrentState() {
        return currentState;
    }

    public int getPlayer1Health() {
        return player1Health;
    }

    public int getPlayer2Health() {
        return player2Health;
    }

    public Integer getWinnerPlayerNumber() {
        return winnerPlayerNumber;
    }

    /**
     * Helper to reuse the same listener instance across multiple test cases.
     */
    public void reset() {
        currentState = GameState.READY;
        player1Health = 0;
        player2Health = 0;
        winnerPlayerNumber = null;
    }
}
