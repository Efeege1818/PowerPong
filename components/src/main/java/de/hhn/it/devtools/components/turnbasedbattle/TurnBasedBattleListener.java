package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.GameState;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;

/**
 * Component implementation of the API listener.
 * Observes the state of both players and keeps track of
 * their health points, the overall game state, and the winner.
 */
public class TurnBasedBattleListener implements de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleListener {

    private volatile GameState currentState = GameState.READY;
    private volatile int player1Health;
    private volatile int player2Health;

    /**
     * Updates the current game state and logs the change.
     *
     * @param gameState the new game state.
     */
    @Override
    public void newGameState(final GameState gameState) {
        this.currentState = gameState;
        System.out.println("GameState changed -> " + gameState);
    }

    /**
     * Updates the state of both players, including their health points.
     *
     * @param player1 the first player
     * @param player2 the second player
     * @throws IllegalArgumentException if either player is null
     *                                  or one of them has no assigned monster.
     */
    @Override
    public void updateState(final Player player1, final Player player2) throws IllegalArgumentException {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("player1 and player2 must not be null");
        }
        if (player1.monster() == null || player2.monster() == null) {
            throw new IllegalArgumentException("Each player must have an assigned monster");
        }

        player1Health = player1.monster().currentHp();
        player2Health = player2.monster().currentHp();

        System.out.printf("updateState: Player1 HP=%d, Player2 HP=%d%n", player1Health, player2Health);
    }

    /**
     * Marks the game as finished and stores the winner.
     *
     * @param winnerPlayerNumber the number of the player who won (must be 1 or 2)
     * @throws IllegalArgumentException if winnerPlayerNumber is not 1 or 2.
     */
    @Override
    public void gameEnded(final int winnerPlayerNumber) {
        if (winnerPlayerNumber != 1 && winnerPlayerNumber != 2) {
            throw new IllegalArgumentException("winnerPlayerNumber must be 1 or 2");
        }
        System.out.println("Game ended. Winner = Player " + winnerPlayerNumber);
    }
}
