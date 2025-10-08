package de.hhn.it.devtools.apis.turnbasedbattle;
/**
 * Enum to represent different the Category of a move.
 */
public enum MoveCategory {

    /**
     *The Move is in the Category ATTACK
     * ATTACK moves do damage
     */
    ATTACK,

    /**
     *The Move is in the Category BUFF
     * BUFF moves increase a stat of the one Monster
     */
    BUFF,

    /**
     *The Move is in the Category DEBUFF
     * DEBUFF moves decrease a stat of the enemy Monster
     */
    DEBUFF
}
