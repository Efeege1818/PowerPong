package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Interface representing a single field on the board.
 */
public interface Field {
    /**
     * Returns the player occupying this field, or null if empty.
     */
    Player getOccupyingPlayer();

    /**
     * Returns whether the field is toxic.
     */
    boolean isToxic();
}
