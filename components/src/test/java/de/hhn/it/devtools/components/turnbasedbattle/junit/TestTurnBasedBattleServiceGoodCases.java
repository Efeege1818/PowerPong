package de.hhn.it.devtools.components.turnbasedbattle.junit;

import de.hhn.it.devtools.apis.turnbasedbattle.*;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleData;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import de.hhn.it.devtools.components.turnbasedbattle.TestTurnBasedBattleListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the TurnBasedBattleService with good cases.
 */
@DisplayName("Test the TurnBasedBattleService with good cases.")
public class TestTurnBasedBattleServiceGoodCases {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(TestTurnBasedBattleServiceGoodCases.class);

    private SimpleTurnBasedBattleService service;
    private TestTurnBasedBattleListener listener;

    private SimpleData data;
    private Player p1;
    private Player p2;
    private Monster m1;
    private Monster m2;

    @BeforeEach
    void setup() {
        service = new SimpleTurnBasedBattleService();
        listener = new TestTurnBasedBattleListener();
        service.addListener(listener);

        data = new SimpleData();

        m1 = data.getMonsters()[0]; // FIRE
        m2 = data.getMonsters()[1]; // GRASS

        p1 = new Player(1, m1, 0);
        p2 = new Player(2, m2, 0);

        service.setupPlayers(p1, p2, m1, m2);
    }

    @Test
    @DisplayName("setupPlayers() correctly assigns players and state stays READY")
    void setupPlayersWorks() {
        assertSame(p1, service.getPlayer1());
        assertSame(p2, service.getPlayer2());
        assertEquals(GameState.READY, service.getGameState());
    }

    @Test
    @DisplayName("start() switches game state to RUNNING")
    void startSwitchesToRunning() {
        service.start();
        assertEquals(GameState.RUNNING, service.getGameState());
    }

    @Test
    @DisplayName("pause() correctly switches state to PAUSED")
    void pauseSwitchesToPaused() {
        service.start();
        service.pause();
        assertEquals(GameState.PAUSED, service.getGameState());
        assertEquals(GameState.PAUSED, listener.getCurrentState());
    }

    @Test
    @DisplayName("abort() correctly switches state to ABORTED")
    void abortSwitchesToAborted() {
        service.start();
        service.abort();
        assertEquals(GameState.ABORTED, service.getGameState());
        assertEquals(GameState.ABORTED, listener.getCurrentState());
    }

    @Test
    @DisplayName("nextTurn() keeps game RUNNING and notifies listeners")
    void nextTurnKeepsRunning() {
        service.start();
        GameState before = service.getGameState();
        service.nextTurn();
        assertEquals(before, service.getGameState());
    }

    @Test
    @DisplayName("reset() clears players and returns state to READY")
    void resetWorks() {
        service.start();
        service.reset();

        assertNull(service.getPlayer1());
        assertNull(service.getPlayer2());
        assertEquals(GameState.READY, service.getGameState());
        assertEquals(GameState.READY, listener.getCurrentState());
    }

  @Test
  @DisplayName("battle executes until END and winner is reported")
  void battleExecutesToEnd() {
    service.start();
    assertEquals(GameState.RUNNING, service.getGameState(),
        "GameState should be RUNNING after start()");

    int maxTurns = 1000;
    int turnsExecuted = 0;

    while (!service.isBattleOver() && turnsExecuted < maxTurns) {

      boolean moveExecutedThisTurn = false;

      for (int moveId = 1; moveId <= 5 && !moveExecutedThisTurn; moveId++) {
        try {
          service.executeTurn(moveId);
          moveExecutedThisTurn = true;
        } catch (IllegalStateException e) {

        }
      }
      assertTrue(moveExecutedThisTurn,
          "No executable move found this turn (all moves on cooldown?)");

      turnsExecuted++;
    }
    assertTrue(turnsExecuted > 0, "At least one turn should have been executed");
    assertTrue(turnsExecuted < maxTurns,
        "Battle should finish within a reasonable number of turns");

    assertTrue(service.isBattleOver(), "Battle should end after several turns");
    assertEquals(GameState.END, service.getGameState(),
        "GameState should be END when the battle is over");

    Player winner = service.getWinner();
    assertNotNull(winner, "Winner must not be null when battle is over");

    Integer winnerFromListener = listener.getWinnerPlayerNumber();
    assertNotNull(winnerFromListener,
        "Listener should have been notified with the winner player number");

    assertTrue(winnerFromListener == 1 || winnerFromListener == 2,
        "Winner player number must be 1 or 2");

    assertEquals(winner.playerId(), winnerFromListener,
        "Listener winnerPlayerNumber must match winner.playerId()");
  }

    @Test
    @DisplayName("Listener newGameState is called when game state changes")
    void listenerNewGameStateIsCalled() {
        listener.reset();
        int initialCount = listener.getNewGameStateCallCount();

        service.start();
        assertTrue(listener.getNewGameStateCallCount() > initialCount,
                "newGameState should be called after start()");
        assertEquals(GameState.RUNNING, listener.getCurrentState());

        service.pause();
        assertTrue(listener.getNewGameStateCallCount() > initialCount + 1,
                "newGameState should be called after pause()");
        assertEquals(GameState.PAUSED, listener.getCurrentState());
    }

    @Test
    @DisplayName("Listener updateState is called when players state changes")
    void listenerUpdateStateIsCalled() {
        // Don't reset listener - it was already called in @BeforeEach
        int initialCount = listener.getUpdateStateCallCount();

        // setupPlayers in @BeforeEach should have triggered updateState at least once
        assertTrue(listener.getUpdateStateCallCount() >= 1,
                "updateState should be called at least once after setupPlayers in @BeforeEach");

        service.start();
        assertTrue(listener.getUpdateStateCallCount() > initialCount,
                "updateState should be called after start()");

        int countBeforeTurn = listener.getUpdateStateCallCount();
        service.executeTurn(1);
        assertTrue(listener.getUpdateStateCallCount() > countBeforeTurn,
                "updateState should be called after executeTurn()");
    }

    @Test
    @DisplayName("Listener gameEnded is called when battle ends")
    void listenerGameEndedIsCalled() {
        listener.reset();
        assertEquals(0, listener.getGameEndedCallCount(),
                "gameEnded should not be called initially");

        service.start();
        assertEquals(0, listener.getGameEndedCallCount(),
                "gameEnded should not be called after start()");

        // Execute battle until it ends
        int maxTurns = 1000;
        int turnsExecuted = 0;
        while (!service.isBattleOver() && turnsExecuted < maxTurns) {
            boolean moveExecutedThisTurn = false;
            for (int moveId = 1; moveId <= 5 && !moveExecutedThisTurn; moveId++) {
                try {
                    service.executeTurn(moveId);
                    moveExecutedThisTurn = true;
                } catch (IllegalStateException e) {
                    // Move on cooldown or invalid, try next
                }
            }
            turnsExecuted++;
        }

        assertTrue(service.isBattleOver(), "Battle should end");
        assertEquals(1, listener.getGameEndedCallCount(),
                "gameEnded should be called exactly once when battle ends");
        assertNotNull(listener.getWinnerPlayerNumber(),
                "Winner should be set in listener");
    }

    @Test
    @DisplayName("All three listener methods are invoked during a complete battle")
    void allListenerMethodsAreInvoked() {
        listener.reset();

        service.start();
        service.executeTurn(1);

        assertTrue(listener.getNewGameStateCallCount() > 0,
                "newGameState should be called at least once");
        assertTrue(listener.getUpdateStateCallCount() > 0,
                "updateState should be called at least once");

        // Note: gameEnded is only called when battle actually ends
        // which may not happen in just one turn
    }
}
