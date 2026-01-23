package de.hhn.it.devtools.apis.powerPong;

import java.util.List;

/**
 * An (immutable) snapshot of the entire game state. The UI uses this object to render itself.
 * Here we use Java 'record' for compact, immutable data classes.
 */

public record GameState(
    /** The current state of the game (e.g., Running, Paused, Won). */
    GameStatus status,

    /** The current state of player1's paddle */
    PaddleState player1Paddle,

    /** The current state of player2's paddle */
    PaddleState player2Paddle,

    /**
     * Eine Liste aller Bälle auf dem Spielfeld.
     * (Eine Liste, um das "Double Ball" Power-Up zu unterstützen)
     */
    List<BallState> balls,

    /** The current score. */
    Score score,

    /** A list of all power-ups that are currently visible on the field and can be collected. */
    List<PowerUpState> activePowerUpsOnField
){}


