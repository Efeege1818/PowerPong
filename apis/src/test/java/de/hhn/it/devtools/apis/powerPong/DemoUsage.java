package de.hhn.it.devtools.apis.powerPong;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import javafx.scene.input.KeyCode;

/**
 * This class demonstrates the intended use of the PowerPongService interface.
 * This code cannot be executed yet because the interface implementation
 * is still missing. It serves only to validate the API design and
 * to find logical flaws in the design.
 */
public class DemoUsage {

    // We declare the variable even though we don't have an instance yet.
    // In the real application, the UI would receive this instance via dependency injection.
    private PowerPongService powerPongService;

    /**
     * Demonstrates starting the game.
     */
    public void demonstrateGameStart() throws GameLogicException {
        // The UI (e.g., in the main menu) would make this call
        // when the "Start" button is clicked.
        powerPongService.startGame(GameMode.POWERUP_DUEL);
        System.out.println("Game started in Power-Up mode.");
    }

    /**
     * Demonstrates a single frame of the game loop.
     * In the real application this would happen ~60 times per second.
     */
    public void demonstrateGameLoopFrame() throws GameLogicException {

        // --- 1. Collect inputs (done by the UI) ---
        // (Here we simulate that Player 1 presses 'W' and Player 2 presses 'ArrowDown')
        PlayerInput inputs = new PlayerInput();
        inputs.keyPressed(KeyCode.W);
        inputs.keyPressed(KeyCode.DOWN);

        // --- 2. Update game logic ---
        // The UI "ticks" the game logic and passes in the inputs.
        powerPongService.updateGame(inputs);

        // --- 3. Retrieve the new state ---
        // The UI fetches the new "snapshot" of the game.
        GameState currentState = powerPongService.getGameState();

        // --- 4. Render state (done by the UI) ---
        // The UI would now draw based on 'currentState'.
        // We simulate this with console outputs.

        if (currentState.status() == GameStatus.RUNNING) {

            // Draw paddles
            double p1_Y = currentState.player1Paddle().yPosition();
            double p2_Y = currentState.player2Paddle().yPosition();
            System.out.println("Draw P1 paddle at Y=" + p1_Y);
            System.out.println("Draw P2 paddle at Y=" + p2_Y);

            // Draw balls
            for (BallState ball : currentState.balls()) {
                System.out.println("Draw ball at X=" + ball.xPosition() + ", Y=" + ball.yPosition());
            }

            // Draw score
            int score1 = currentState.score().player1();
            int score2 = currentState.score().player2();
            System.out.println("Score: " + score1 + " - " + score2);

        } else if (currentState.status() == GameStatus.PLAYER_1_WINS) {

            // Show end screen
            System.out.println("Player 1 wins!");
            // (Here the UI would show buttons like "Play again", etc.)
        }

        // --- 5. Game gets terminated by the user (e.g., ESC in menu) ---
        powerPongService.endGame();
    }
}
