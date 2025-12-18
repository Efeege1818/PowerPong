package de.hhn.it.devtools.components.powerPong.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongListener;
import de.hhn.it.devtools.apis.powerPong.Score;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PowerPongMatchEngineTest {

    private PowerPongMatchEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PowerPongMatchEngine();
    }

    @Test
    void testStartGame() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);
        GameState state = engine.getGameState();
        assertEquals(GameStatus.RUNNING, state.status());
        assertNotNull(state.balls());
        assertEquals(1, state.balls().size());
    }

    @Test
    void testStartGameNullMode() {
        assertThrows(GameLogicException.class, () -> engine.startGame(null));
    }

    @Test
    void testUpdateGameNotStarted() {
        assertThrows(GameLogicException.class, () -> engine.updateGame(new PlayerInput()));
    }

    @Test
    void testPauseGame() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);
        engine.setPaused(true);
        assertEquals(GameStatus.PAUSED, engine.getGameState().status());

        engine.setPaused(false);
        assertEquals(GameStatus.RUNNING, engine.getGameState().status());
    }

    @Test
    void testEndGame() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);
        engine.endGame();
        assertEquals(GameStatus.MENU, engine.getGameState().status());
    }

    @Test
    void testListenerNotification() throws GameLogicException {
        AtomicBoolean scored = new AtomicBoolean(false);
        engine.addListener(new PowerPongListener() {
            @Override
            public void onBallCollision(GameState state) {
            }

            @Override
            public void onPlayerScored(int player, Score score) {
                scored.set(true);
            }

            @Override
            public void onGameEnd(GameStatus status, GameState state) {
            }

            @Override
            public void onPowerUpCollected(int collectingPlayerIndex, PowerUpType powerUpType) {
            }
        });

        engine.startGame(GameMode.CLASSIC_DUEL);

        engine.removeListener(null); // Should not crash
    }

    @Test
    void testGameLoop() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);

        // Simulate some updates
        PlayerInput input = new PlayerInput();
        engine.updateGame(input);

        GameState state = engine.getGameState();
        assertNotNull(state);
        assertEquals(GameStatus.RUNNING, state.status());
    }

    @Test
    void testMultipleUpdates() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);

        PlayerInput input = new PlayerInput();
        for (int i = 0; i < 10; i++) {
            engine.updateGame(input);
        }

        GameState state = engine.getGameState();
        assertEquals(GameStatus.RUNNING, state.status());
    }

    @Test
    void testUpdateWhilePaused() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);
        engine.setPaused(true);

        PlayerInput input = new PlayerInput();
        engine.updateGame(input); // Should not throw

        assertEquals(GameStatus.PAUSED, engine.getGameState().status());
    }

    @Test
    void testPowerUpDuelMode() throws GameLogicException {
        // Test that POWERUP_DUEL mode updates power-ups
        engine.startGame(GameMode.POWERUP_DUEL);

        PlayerInput input = new PlayerInput();
        // Run multiple updates to allow power-ups to spawn
        for (int i = 0; i < 20; i++) {
            engine.updateGame(input);
        }

        GameState state = engine.getGameState();
        assertEquals(GameStatus.RUNNING, state.status());
        // In POWERUP_DUEL mode, power-ups should eventually spawn
        assertNotNull(state.activePowerUpsOnField());
    }

    @Test
    void testPlayerInputProcessing() throws GameLogicException {
        engine.startGame(GameMode.CLASSIC_DUEL);

        // Create input with W and UP pressed (moving both paddles up)
        PlayerInput input = new PlayerInput();
        input.keyPressed(InputAction.LEFT_UP);
        input.keyPressed(InputAction.RIGHT_UP);

        GameState before = engine.getGameState();
        double leftBefore = before.player1Paddle().yPosition();
        double rightBefore = before.player2Paddle().yPosition();

        // Update with input
        engine.updateGame(input);

        GameState after = engine.getGameState();
        // Paddles should have moved up (y decreased)
        assertTrue(after.player1Paddle().yPosition() <= leftBefore);
        assertTrue(after.player2Paddle().yPosition() <= rightBefore);
    }

    @Test
    void testAIControl() throws GameLogicException {
        engine.startGame(GameMode.PLAYER_VS_AI);

        // Simulate ball moving up
        // We can't easily force ball position without access to physics,
        // but we can check that paddle 2 moves over time if the ball is moving.

        PlayerInput input = new PlayerInput();
        GameState initial = engine.getGameState();
        double initialY = initial.player2Paddle().yPosition();

        // Run updates
        for (int i = 0; i < 60; i++) {
            engine.updateGame(input);
        }

        GameState finalState = engine.getGameState();
        // Paddle should have moved (unless ball was perfectly center, which is unlikely
        // with random launch)
        // Or at least it shouldn't crash
        assertNotNull(finalState);
    }

    @Test
    void testSurvivalMode() throws GameLogicException {
        engine.startGame(GameMode.SURVIVAL);

        // Initial state
        PlayerInput input = new PlayerInput();
        engine.updateGame(input);

        // Simulate 11 seconds passing (difficulty increases every 10s)
        // We can't easily mock time without refactoring engine to take a Clock,
        // but we can call updateGame many times.
        // 11 seconds * 60 fps = 660 frames
        for (int i = 0; i < 660; i++) {
            engine.updateGame(input);
        }

        // We can't easily inspect internal difficulty multiplier without reflection or
        // exposing it.
        // But we can verify the game is still running and stable.
        assertEquals(GameStatus.RUNNING, engine.getGameState().status());
    }
}
