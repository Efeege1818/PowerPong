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
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(TestTurnBasedBattleListener.class);

    private volatile GameState currentState = GameState.READY;
    private volatile int player1Health;
    private volatile int player2Health;
    private volatile Integer winnerPlayerNumber;

    // Counters to track listener method invocations
    private volatile int newGameStateCallCount = 0;
    private volatile int updateStateCallCount = 0;
    private volatile int gameEndedCallCount = 0;

    /**
     * Stores the current game state so tests can assert on it.
     *
     * @param gameState the new game state; must not be null
     * @throws IllegalArgumentException if gameState is null
     */
    @Override
    public void newGameState(final GameState gameState) {
        logger.info("TestTurnBasedBattleListener: newGameState, gameState = {}", gameState);

        if (gameState == null) {
            throw new IllegalArgumentException("gameState must not be null");
        }
        this.currentState = gameState;
        this.newGameStateCallCount++;
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
        logger.info("TestTurnBasedBattleListener: updateState, player1 = {}, player2 = {}",
                player1 != null ? player1.playerId() : "null",
                player2 != null ? player2.playerId() : "null");

        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("player1 and player2 must not be null");
        }
        if (player1.monster() == null || player2.monster() == null) {
            throw new IllegalArgumentException("Each player must have an assigned monster");
        }

        // Ich habs auskommentiert weil es falsch ist da der monster ein record ist und deshalb es keinen currentHp gibt
        //player1Health = player1.monster().currentHp();
        //player2Health = player2.monster().currentHp();
        this.updateStateCallCount++;
    }

    /**
     * Stores the winner player number for later assertions.
     *
     * @param winnerPlayerNumber the number of the player who won (1 or 2)
     * @throws IllegalArgumentException if winnerPlayerNumber is not 1 or 2
     */
    @Override
    public void gameEnded(final int winnerPlayerNumber) {
        logger.info("TestTurnBasedBattleListener: gameEnded, winnerPlayerNumber = {}", winnerPlayerNumber);

        if (winnerPlayerNumber != 1 && winnerPlayerNumber != 2) {
            throw new IllegalArgumentException("winnerPlayerNumber must be 1 or 2");
        }
        this.winnerPlayerNumber = winnerPlayerNumber;
        this.gameEndedCallCount++;
    }


    public GameState getCurrentState() {
        logger.debug("TestTurnBasedBattleListener: getCurrentState, currentState = {}", currentState);
        return currentState;
    }

    public int getPlayer1Health() {
        logger.debug("TestTurnBasedBattleListener: getPlayer1Health, player1Health = {}", player1Health);
        return player1Health;
    }

    public int getPlayer2Health() {
        logger.debug("TestTurnBasedBattleListener: getPlayer2Health, player2Health = {}", player2Health);
        return player2Health;
    }

    public Integer getWinnerPlayerNumber() {
        logger.debug("TestTurnBasedBattleListener: getWinnerPlayerNumber, winnerPlayerNumber = {}", winnerPlayerNumber);
        return winnerPlayerNumber;
    }

    public int getNewGameStateCallCount() {
        logger.debug("TestTurnBasedBattleListener: getNewGameStateCallCount, count = {}", newGameStateCallCount);
        return newGameStateCallCount;
    }

    public int getUpdateStateCallCount() {
        logger.debug("TestTurnBasedBattleListener: getUpdateStateCallCount, count = {}", updateStateCallCount);

        return updateStateCallCount;
    }

    public int getGameEndedCallCount() {
        logger.debug("TestTurnBasedBattleListener: getGameEndedCallCount, count = {}", gameEndedCallCount);

        return gameEndedCallCount;
    }

    /**
     * Helper to reuse the same listener instance across multiple test cases.
     */
    public void reset() {
        logger.info("TestTurnBasedBattleListener: reset");

        currentState = GameState.READY;
        player1Health = 0;
        player2Health = 0;
        winnerPlayerNumber = null;
        newGameStateCallCount = 0;
        updateStateCallCount = 0;
        gameEndedCallCount = 0;
    }
}
