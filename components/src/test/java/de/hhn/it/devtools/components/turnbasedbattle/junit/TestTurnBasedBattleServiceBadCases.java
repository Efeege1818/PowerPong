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
 * Test the TurnBasedBattleService with bad cases.
 */
@DisplayName("Test the TurnBasedBattleService with bad cases.")
public class TestTurnBasedBattleServiceBadCases {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(TestTurnBasedBattleServiceBadCases.class);

    private SimpleTurnBasedBattleService service;
    private TestTurnBasedBattleListener listener;
    private Data data;

    @BeforeEach
    void setup() {
        service = new SimpleTurnBasedBattleService();
        listener = new TestTurnBasedBattleListener();
        service.addListener(listener);

        data = new Data();
    }

    @Test
    @DisplayName("start() without players throws IllegalStateException")
    void startWithoutPlayersThrows() {
        assertThrows(IllegalStateException.class,
                () -> service.start());
    }

    @Test
    @DisplayName("setupPlayers() when not READY throws IllegalStateException")
    void setupPlayersWhenNotReadyThrows() {
        Monster m1 = data.getMonsters()[0];
        Monster m2 = data.getMonsters()[1];

        Player p1 = new Player(1, m1, 0);
        Player p2 = new Player(2, m2, 0);

        // valid first setup
        service.setupPlayers(p1, p2, m1, m2);
        service.start(); // switches to RUNNING

        assertThrows(IllegalStateException.class,
                () -> service.setupPlayers(p1, p2, m1, m2));
    }

    @Test
    @DisplayName("pause() in wrong state throws IllegalStateException")
    void pauseWrongStateThrows() {
        assertThrows(IllegalStateException.class, () -> service.pause());
    }

    @Test
    @DisplayName("abort() in wrong state throws IllegalStateException")
    void abortWrongStateThrows() {
        assertThrows(IllegalStateException.class, () -> service.abort());
    }

    @Test
    @DisplayName("end() in wrong state throws IllegalStateException")
    void endWrongStateThrows() {
        assertThrows(IllegalStateException.class, () -> service.end());
    }

    @Test
    @DisplayName("executeTurn() in wrong state throws IllegalStateException")
    void executeTurnWrongStateThrows() {
        assertThrows(IllegalStateException.class, () -> service.executeTurn(1));
    }

    @Test
    @DisplayName("addListener(null) throws IllegalArgumentException")
    void addNullListenerThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addListener(null));
    }

    @Test
    @DisplayName("adding same listener twice throws IllegalStateException")
    void addingSameListenerTwiceThrows() {
        assertThrows(IllegalStateException.class,
                () -> service.addListener(listener));
    }

    @Test
    @DisplayName("removeListener() on unknown listener returns false")
    void removeUnknownListenerFalse() {
        TestTurnBasedBattleListener other = new TestTurnBasedBattleListener();
        assertFalse(service.removeListener(other));
    }
}
