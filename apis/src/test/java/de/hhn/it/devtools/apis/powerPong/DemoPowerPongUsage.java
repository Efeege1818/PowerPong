package de.hhn.it.devtools.apis.powerPong;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates the usage of the PowerPong component facade.
 * This demo shows the sequence of API calls required to use the PowerPong game
 * engine.
 *
 * <p>
 * Note: This code can only compile but cannot be executed, as we haven't
 * implemented the component yet. It demonstrates in which sequence calls
 * should be made against the component to reach the goal of the component.
 */
public class DemoPowerPongUsage {

    private static final Logger logger = LoggerFactory.getLogger(DemoPowerPongUsage.class);

    /**
     * Main entry point for the demo.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Step 1: Obtain a reference to the PowerPong service
        // In a real application, this would be provided by dependency injection
        // or a service locator. Here we use null as a placeholder.
        PowerPongService service = null;
        logger.info("PowerPongService obtained (placeholder)");

        // Step 2: Create and register a listener for game events (Observer Pattern)
        PowerPongListener listener = new DemoPowerPongListener();
        service.addListener(listener);
        logger.info("Listener registered successfully");

        try {
            // Step 3: Start a new game with a selected game mode
            // Available modes: CLASSIC_DUEL, POWERUP_DUEL, PLAYER_VS_AI, SURVIVAL
            service.startGame(GameMode.CLASSIC_DUEL);
            logger.info("Game started in CLASSIC_DUEL mode");

            // Step 4: Game loop - this would run continuously in the actual game
            // For demo purposes, we simulate a few frames
            for (int frame = 0; frame < 5; frame++) {
                // Create input for this frame
                PlayerInput input = new PlayerInput();

                // Player 1 controls (left paddle)
                if (frame % 2 == 0) {
                    input.keyPressed(InputAction.LEFT_UP);
                } else {
                    input.keyPressed(InputAction.LEFT_DOWN);
                }

                // Player 2 controls (right paddle) - in VS AI mode, this is ignored
                input.keyPressed(InputAction.RIGHT_UP);

                // Update game with input and delta time (16ms = ~60 FPS)
                service.updateGame(input, 0.016);

                // Get current game state for rendering
                GameState state = service.getGameState();
                logger.info("Frame {}: Balls on field: {}, Score: {} - {}",
                        frame,
                        state.balls() != null ? state.balls().size() : 0,
                        state.score().player1(),
                        state.score().player2());
            }

            // Step 5: Pause the game (e.g., when user opens menu)
            service.setPaused(true);
            logger.info("Game paused");

            // Check game state while paused
            GameState pausedState = service.getGameState();
            logger.info("Game status while paused: {}", pausedState.status());

            // Resume the game
            service.setPaused(false);
            logger.info("Game resumed");

            // Step 6: Check for active power-up shields
            boolean player1HasShield = service.hasShield(1);
            boolean player2HasShield = service.hasShield(2);
            logger.info("Shield status - Player 1: {}, Player 2: {}",
                    player1HasShield, player2HasShield);

            // Step 7: End the game (e.g., when user quits or game ends naturally)
            service.endGame();
            logger.info("Game ended");

        } catch (GameLogicException e) {
            logger.error("Game logic error occurred: {}", e.getMessage());
        }

        // Step 8: Clean up - remove listener when done
        service.removeListener(listener);
        logger.info("Listener removed, cleanup complete");
    }

    /**
     * Demo implementation of PowerPongListener that logs all events.
     * In a real application, this would update the UI.
     */
    private static class DemoPowerPongListener implements PowerPongListener {

        private static final Logger listenerLogger = LoggerFactory.getLogger(DemoPowerPongListener.class);

        @Override
        public void onPlayerScored(int player, Score newScore) {
            listenerLogger.info("EVENT: Player {} scored! New score: {} - {}",
                    player, newScore.player1(), newScore.player2());
        }

        @Override
        public void onGameEnd(GameStatus finalStatus, GameState finalState) {
            listenerLogger.info("EVENT: Game ended with status: {}", finalStatus);
            listenerLogger.info("Final score: {} - {}",
                    finalState.score().player1(), finalState.score().player2());
        }

        @Override
        public void onBallCollision(GameState updatedState) {
            listenerLogger.debug("EVENT: Ball collision detected");
        }

        @Override
        public void onPowerUpCollected(int player, PowerUpType type) {
            listenerLogger.info("EVENT: Player {} collected power-up: {}", player, type);
        }
    }
}
