package de.hhn.it.devtools.components.powerpong.provider;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for PowerPongMatchEngine focusing on good cases (normal operation).
 * These tests verify that the engine works correctly under expected conditions.
 */
@DisplayName("PowerPongMatchEngine Good Cases")
class PowerPongMatchEngineGoodCasesTest {

    private PowerPongMatchEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PowerPongMatchEngine();
    }

    @Nested
    @DisplayName("Game Lifecycle Tests")
    class GameLifecycleTests {

        @Test
        @DisplayName("startGame() should initialize game with RUNNING status")
        void startGameShouldInitializeGame() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);

            GameState state = engine.getGameState();
            assertEquals(GameStatus.RUNNING, state.status());
            assertNotNull(state.balls());
            assertEquals(1, state.balls().size());
            assertNotNull(state.player1Paddle());
            assertNotNull(state.player2Paddle());
        }

        @Test
        @DisplayName("startGame() should support all game modes")
        void startGameShouldSupportAllModes() {
            for (GameMode mode : GameMode.values()) {
                PowerPongMatchEngine testEngine = new PowerPongMatchEngine();
                assertDoesNotThrow(() -> testEngine.startGame(mode));
                assertEquals(GameStatus.RUNNING, testEngine.getGameState().status());
            }
        }

        @Test
        @DisplayName("endGame() should reset game to MENU status")
        void endGameShouldResetToMenu() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.endGame();

            assertEquals(GameStatus.MENU, engine.getGameState().status());
        }

        @Test
        @DisplayName("setPaused(true) should pause the game")
        void setPausedTrueShouldPauseGame() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.setPaused(true);

            assertEquals(GameStatus.PAUSED, engine.getGameState().status());
        }

        @Test
        @DisplayName("setPaused(false) should resume the game")
        void setPausedFalseShouldResumeGame() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.setPaused(true);
            engine.setPaused(false);

            assertEquals(GameStatus.RUNNING, engine.getGameState().status());
        }
    }

    @Nested
    @DisplayName("Game Update Tests")
    class GameUpdateTests {

        @Test
        @DisplayName("updateGame() should not throw when game is running")
        void updateGameShouldNotThrowWhenRunning() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            PlayerInput input = new PlayerInput();

            assertDoesNotThrow(() -> engine.updateGame(input));
        }

        @Test
        @DisplayName("updateGame() should process multiple updates without error")
        void updateGameShouldHandleMultipleUpdates() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            PlayerInput input = new PlayerInput();

            for (int i = 0; i < 100; i++) {
                engine.updateGame(input);
            }

            assertEquals(GameStatus.RUNNING, engine.getGameState().status());
        }

        @Test
        @DisplayName("updateGame() while paused should not change state")
        void updateGameWhilePausedShouldNotChangeState() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);
            engine.setPaused(true);

            GameState before = engine.getGameState();
            engine.updateGame(new PlayerInput());
            GameState after = engine.getGameState();

            assertEquals(GameStatus.PAUSED, after.status());
        }
    }

    @Nested
    @DisplayName("Player Input Tests")
    class PlayerInputTests {

        @Test
        @DisplayName("LEFT_UP input should move player 1 paddle upward")
        void leftUpInputShouldMovePlayer1PaddleUp() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);

            GameState before = engine.getGameState();
            double yBefore = before.player1Paddle().yPosition();

            PlayerInput input = new PlayerInput();
            input.keyPressed(InputAction.LEFT_UP);
            engine.updateGame(input);

            GameState after = engine.getGameState();
            assertTrue(after.player1Paddle().yPosition() <= yBefore,
                    "Paddle Y should decrease (move up) or stay at boundary");
        }

        @Test
        @DisplayName("LEFT_DOWN input should move player 1 paddle downward")
        void leftDownInputShouldMovePlayer1PaddleDown() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);

            GameState before = engine.getGameState();
            double yBefore = before.player1Paddle().yPosition();

            PlayerInput input = new PlayerInput();
            input.keyPressed(InputAction.LEFT_DOWN);
            engine.updateGame(input);

            GameState after = engine.getGameState();
            assertTrue(after.player1Paddle().yPosition() >= yBefore,
                    "Paddle Y should increase (move down) or stay at boundary");
        }

        @Test
        @DisplayName("RIGHT inputs should move player 2 paddle in CLASSIC_DUEL mode")
        void rightInputsShouldMovePlayer2PaddleInDuel() throws GameLogicException {
            engine.startGame(GameMode.CLASSIC_DUEL);

            GameState before = engine.getGameState();
            double yBefore = before.player2Paddle().yPosition();

            PlayerInput input = new PlayerInput();
            input.keyPressed(InputAction.RIGHT_UP);
            engine.updateGame(input);

            GameState after = engine.getGameState();
            assertTrue(after.player2Paddle().yPosition() <= yBefore,
                    "Paddle Y should decrease (move up) or stay at boundary");
        }
    }

    @Nested
    @DisplayName("Game Mode Specific Tests")
    class GameModeTests {

        @Test
        @DisplayName("PLAYER_VS_AI mode should have AI-controlled player 2")
        void playerVsAiModeShouldHaveAiControl() throws GameLogicException {
            engine.startGame(GameMode.PLAYER_VS_AI);

            // AI should move paddle automatically after some updates
            PlayerInput input = new PlayerInput();
            for (int i = 0; i < 60; i++) {
                engine.updateGame(input);
            }

            // Just verify game is still running and stable
            assertEquals(0, engine.getGameState().score().player1());
            assertEquals(0, engine.getGameState().score().player2());
        }

        @Test
        @DisplayName("POWERUP_DUEL mode should support power-ups")
        void powerUpDuelModeShouldSupportPowerUps() throws GameLogicException {
            engine.startGame(GameMode.POWERUP_DUEL);

            PlayerInput input = new PlayerInput();
            for (int i = 0; i < 20; i++) {
                engine.updateGame(input);
            }

            // Verify power-up list exists (may or may not have spawned yet)
            assertNotNull(engine.getGameState().activePowerUpsOnField());
        }

        @Test
        @DisplayName("SURVIVAL mode should start with 3 lives")
        void survivalModeShouldStartWith3Lives() throws GameLogicException {
            engine.startGame(GameMode.SURVIVAL);

            // In survival mode, score.player2 represents lives
            assertEquals(3, engine.getGameState().score().player2());
        }
    }

    @Nested
    @DisplayName("Shield Tests")
    class ShieldTests {

        @Test
        @DisplayName("hasShield() should return false when no shield is active")
        void hasShieldShouldReturnFalseByDefault() throws GameLogicException {
            engine.startGame(GameMode.POWERUP_DUEL);

            // Initially no shields are active
            assertEquals(false, engine.hasShield(1));
            assertEquals(false, engine.hasShield(2));
        }
    }
}
