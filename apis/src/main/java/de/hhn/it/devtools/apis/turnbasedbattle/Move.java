package de.hhn.it.devtools.apis.turnbasedbattle;

public abstract class Move {

    /**
     * The elemental type of the move.
     */
    private Element element;

    /**
     * The Category the move belongs to
     */
    private MoveCategory moveCategory;

    /**
     * The amount of damage the move dose
     */
    private int damage;
}
