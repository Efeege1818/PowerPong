package de.hhn.it.devtools.components.powerpong.demo;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.BallState;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.components.powerpong.provider.PowerPongMatchEngine;

/**
 * This class demonstrates the intended use of the PowerPongService interface.
 * It is now executable and validates the component implementation.
 */
public class DemoUsage {

    // Dependency injection simulation
    private PowerPongService powerPongService;

    public DemoUsage() {
        // In a real app, this would be injected. Here we instantiate it directly.
        this.powerPongService = new PowerPongMatchEngine();
    }

    public static void main(String[] args) {
        DemoUsage demo = new DemoUsage();
        try {
            demo.demonstrateGameStart();
            // Run a few frames
            for (int i = 0; i < 5; i++) {
                demo.demonstrateGameLoopFrame();
                Thread.sleep(100); // Simulate frame time
            }
        } catch (GameLogicException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates starting the game.
     */
    public void demonstrateGameStart() throws GameLogicException {
        // The UI would make this call when the "Start" button is clicked.
        powerPongService.startGame(GameMode.POWERUP_DUEL);
        System.out.println("Game started in Power-Up mode.");
    }

    /**
     * Demonstrates a single frame of the game loop.
     * In the real application this would happen ~60 times per second.
     */
    public void demonstrateGameLoopFrame() throws GameLogicException {

        // --- 1. Collect inputs ---
        // Here we simulate that Player 1 presses 'W' and Player 2 presses 'ArrowDown'
        PlayerInput inputs = new PlayerInput();
        inputs.keyPressed(InputAction.LEFT_UP);
        inputs.keyPressed(InputAction.RIGHT_DOWN);

        // --- 2. Update game logic ---
        // The UI "ticks" the game logic and passes in the inputs.
        powerPongService.updateGame(inputs);

        // --- 3. Retrieve the new state ---
        // The UI fetches the new "snapshot" of the game.
        GameState currentState = powerPongService.getGameState();

        // --- 4. Render state ---
        // The UI would now draw based on 'currentState'.
        // We simulate this with console outputs.

        if (currentState.status() == GameStatus.RUNNING) {
            System.out.println("--- Frame ---");
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
            // Here the UI would show buttons like "Play again"
        }
    }
}