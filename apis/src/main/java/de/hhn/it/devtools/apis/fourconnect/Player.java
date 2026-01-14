package de.hhn.it.devtools.apis.fourconnect;

/**
 * Record defining a player for read-only identification.
 */
public record Player(

    /**
     * Gets the display name of the player (e.g., "Player 1").
     * @return The player's name.
     */
    String name,

    /**
     * Gets the color assigned to the player (e.g., RED or YELLOW).
     * @return The PlayerColor object of the player.
     */
    PlayerColor color
) {

}