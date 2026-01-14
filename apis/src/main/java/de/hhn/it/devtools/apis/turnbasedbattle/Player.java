package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Record to allow creation of Player object.
 *
 * @param playerId id of the player.
 * @param monster monster assigned to this player.
 * @param score amount of rounds this player has won.
 */
public record Player(int playerId, Monster monster, int score) {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Player.class);
}
