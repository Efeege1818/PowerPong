package de.hhn.it.devtools.apis.turnbasedbattle.move;

/**
 * Enum to represent different types of moves.
 */

public enum MoveType {

    /**
     * The move is an attack.
     * Damages the opponent's monster.
     */
    ATTACK,

    /**
     * The move is a buff.
     * Increases the stats of the user's monster.
     */
    BUFF,

    /**
     * The move is a debuff.
     * Decreases the stats of the opponent's monster.
     */
    DEBUFF,

    /**
     * The move applies damage-over-time (DOT) to the opponent's monster.
     */
    DOT,

    /**
     * The move is a stance.
     * Changes the stance of the user's monster.
     */
    STANCE,

    /**
     * The move is a counterattack.
     * A counterattack move that only deals damage if the opponent
     * performs an attack during the same turn.
     * If the opponent does not attack, this move has no effect.
     */
    COUNTERATTACK,
}
