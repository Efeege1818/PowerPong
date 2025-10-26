package de.hhn.it.devtools.apis.powerPong;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import java.util.EventListener;

/**
 * Observer callback for UI components that want to react to asynchronous in-game events.
 * <p>The UI registers one or more listeners via {@link PowerPongService#addListener(PowerPongListener)}
 * and gets notified without having to poll the service each frame (classic Observer pattern).</p>
 */
public interface PowerPongListener extends EventListener {

    /**
     * Invoked whenever a ball collides with a paddle or boundary.
     *
     * @param updatedState snapshot *after* the collision so the UI can trigger effects.
     */
    void onBallCollision(GameState updatedState);

    /**
     * Invoked when a player scores a point.
     *
     * @param scoringPlayerIndex logical paddle index (1-based) identifying the player.
     * @param updatedScore score after the point has been awarded.
     */
    void onPlayerScored(int scoringPlayerIndex, Score updatedScore);

    /**
     * Invoked once the game reaches a terminal state.
     *
     * @param finalStatus {@link GameStatus#PLAYER_1_WINS} or {@link GameStatus#PLAYER_2_WINS}.
     * @param finalState  final immutable game state (useful for end-screen UI).
     */
    void onGameEnd(GameStatus finalStatus, GameState finalState);

    /**
     * Invoked when a power-up gets collected.
     *
     * @param collectingPlayerIndex logical paddle index.
     * @param powerUpType type of the power-up that was consumed.
     * @throws GameLogicException implementors may throw when post-processing fails.
     */
    void onPowerUpCollected(int collectingPlayerIndex, PowerUpType powerUpType)
            throws GameLogicException;
}
