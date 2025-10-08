package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Enum to represent the different categories of a move.
 */
public enum MoveCategory {

    /**
     * The move is in the ATTACK category.
     * ATTACK moves deal damage.
     */
    ATTACK,

    /**
     * The move is in the BUFF category.
     * BUFF moves increase one of the user's monster's stats.
     */
    BUFF,

    /**
     * The move is in the DEBUFF category.
     * DEBUFF moves decrease one of the enemy monster's stats.
     */
    DEBUFF
}
