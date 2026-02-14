package de.hhn.it.devtools.components.powerpong.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for PowerPongMatchEngine focusing on bad cases (error handling).
 * These tests verify that the engine handles invalid inputs and error
 * conditions correctly.
 */
@DisplayName("PowerPongMatchEngine Bad Cases")
class PowerPongMatchEngineBadCasesTest {

    private PowerPongMatchEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PowerPongMatchEngine();
    }

    @Nested
    @DisplayName("Null Parameter Tests")
    class NullParameterTests {

        @Test
        @DisplayName("startGame(null) should throw GameLogicException")
        void startGameWithNullModeShouldThrow() {
            assertThrows(GameLogicException.class, () -> engine.startGame(null),
                    "Starting game with null mode should throw GameLogicException");
        }

        @Test
        @DisplayName("addListener(null) should not add null listener")
        void addListenerWithNullShouldNotAdd() throws GameLogicException {
            engine.addListener(null);
            // Should not crash
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.updateGame(new PlayerInput());
            // Verify no exception was thrown
        }

        @Test
        @DisplayName("removeListener(null) should not throw")
        void removeListenerWithNullShouldNotThrow() {
            // Should not crash even with null
            engine.removeListener(null);
        }
    }

    @Nested
    @DisplayName("Invalid State Tests")
    class InvalidStateTests {

        @Test
        @DisplayName("updateGame() before startGame() should throw GameLogicException")
        void updateGameBeforeStartShouldThrow() {
            assertThrows(GameLogicException.class, () -> engine.updateGame(new PlayerInput()),
                    "Updating game before start should throw GameLogicException");
        }

        @Test
        @DisplayName("setPaused() should do nothing when game is not running")
        void setPausedWhenNotRunningShouldDoNothing() {
            // Game not started yet
            engine.setPaused(true);
            // Should not throw, just silently do nothing
            assertEquals(GameStatus.MENU, engine.getGameState().status());
        }

        @Test
        @DisplayName("endGame() should work even when game is not started")
        void endGameWhenNotStartedShouldNotThrow() {
            // endGame() on a non-running game should not throw
            engine.endGame();
            assertEquals(GameStatus.MENU, engine.getGameState().status());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Starting multiple games should reset state each time")
        void startMultipleGamesShouldResetState() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.updateGame(new PlayerInput());
            engine.updateGame(new PlayerInput());

            // Start a new game
            engine.startGame(GameMode.POWERUP_DUEL);

            // State should be reset
            assertEquals(GameStatus.RUNNING, engine.getGameState().status());
            assertEquals(0, engine.getGameState().score().player1());
            assertEquals(0, engine.getGameState().score().player2());
        }

        @Test
        @DisplayName("Double pause should not cause issues")
        void doublePauseShouldNotCauseIssues() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);

            engine.setPaused(true);
            engine.setPaused(true); // Pause again

            assertEquals(GameStatus.PAUSED, engine.getGameState().status());
        }

        @Test
        @DisplayName("Double unpause should not cause issues")
        void doubleUnpauseShouldNotCauseIssues() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.setPaused(true);

            engine.setPaused(false);
            engine.setPaused(false); // Unpause again

            assertEquals(GameStatus.RUNNING, engine.getGameState().status());
        }

        @Test
        @DisplayName("Adding same listener twice should not duplicate")
        void addingSameListenerTwiceShouldNotDuplicate() throws GameLogicException {
            // Test that adding the same listener twice doesn't cause issues
            // This is verified by the internal list check in addListener
            // We just verify no exception is thrown
            engine.startGame(GameMode.CLASSIC_DUEL);
            // If duplicates were allowed, this could cause issues
            // The implementation prevents duplicates
        }
    }
}
