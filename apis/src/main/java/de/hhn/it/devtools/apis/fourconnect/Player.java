package de.hhn.it.devtools.apis.fourconnect;

/**
 * Defines the state of a player.
 * This interface is strictly read-only. It provides information about the player
 * (e.g., name, color, score) to the UI without allowing the UI to modify these values directly.
 * Any changes to the score must be handled centrally through the ConnectFourService.
 */
public interface Player {

    /**
     * Returns the display name of the player (e.g., "Player 1").
     * @return The name of the player.
     */
    String getName();

    /**
     * Returns the color of the player (e.g., BLUE or YELLOW).
     * @return The PlayerColor object of the player.
     */
    PlayerColor getColor();

    /**
     * Returns the current score of the player.
     * @return The current score.
     */
    int getScore();
}
