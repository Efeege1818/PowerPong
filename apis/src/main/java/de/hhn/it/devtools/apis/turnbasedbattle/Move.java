package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Abstract class representing a move in the turn-based battle system.
 */
public abstract class Move {

    /**
     * The elemental type of the move.
     */
    private Element element;

    /**
     * The category the move belongs to.
     */
    private MoveCategory moveCategory;

    /**
     * The amount of damage the move does.
     */
    private int damage;
}
