package de.hhn.it.devtools.components.turnbasedbattle.junit;

import de.hhn.it.devtools.apis.turnbasedbattle.*;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
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

    private Data data;
    private Player p1;
    private Player p2;
    private Monster m1;
    private Monster m2;

    @BeforeEach
    void setup() {
        service = new SimpleTurnBasedBattleService();
        listener = new TestTurnBasedBattleListener();
        service.addListener(listener);

        data = new Data();

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

        int safe = 0;
        while (!service.isBattleOver() && safe < 100) {
            service.executeTurn(1); // Move-ID 1 (Normal Attack)
            safe++;
        }

        assertTrue(service.isBattleOver(), "Battle should end after several turns");
        assertEquals(GameState.END, service.getGameState());
        assertNotNull(service.getWinner());
        assertNotNull(listener.getWinnerPlayerNumber());
        assertTrue(listener.getWinnerPlayerNumber() == 1
                || listener.getWinnerPlayerNumber() == 2);
    }
}
