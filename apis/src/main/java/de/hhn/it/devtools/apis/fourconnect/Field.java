package de.hhn.it.devtools.apis.fourconnect;

/**
 * Interface representing a single field on the board.
 */
public interface Field {
    Player getOccupyingPlayer();

    /**
     * Returns the player occupying this field, or null if empty.
     */
    Player getOccupyingPlayer();

    boolean isToxicZone();

    int getDecayTime();

    Player getOccupyingPlayer(Player player);

    /**
     * Returns whether the field is toxic.
     */
    boolean isToxic();
}
