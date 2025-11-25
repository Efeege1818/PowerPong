package de.hhn.it.devtools.apis.fourconnect;

/**
 * Interface representing a single field on the board.
 * It provides read-only access to the state of a cell.
 */
public interface Field {

    /** Returns the player occupying this field, or null if empty. */
    Player getOccupyingPlayer();

    /** Checks if this field is a permanent toxic zone. */
    boolean isToxicZone();

    /** Returns the remaining decay time for a chip on this field (e.g., 3, 2, 1). */
    int getDecayTime();


    boolean isOccupied();


}