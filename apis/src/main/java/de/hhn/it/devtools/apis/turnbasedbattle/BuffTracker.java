package de.hhn.it.devtools.apis.turnbasedbattle;

import java.util.Map;

/**
 * Tracks, removes and excecut effects of Buffs and Debuffs
 */
public interface BuffTracker {

    /**
     * Add a buff or debuff to a monster.
     *
     * @param move Move containing the buff or debuff
     * @param currentPlayerId id of the player currently executing this move
     */
    void addBuff(Move move, int currentPlayerId);


    /**
     * Returns all active buffs for current player.
     *
     * @param currentPlayerId id of the player currently in control
     * @return all active buffs
     */
    Map<Integer, Move> getBuffs(int currentPlayerId);

    /**
     * Buff duration is reduced by 1 each turn.
     * A buff with a duration of 0 is removed.
     */
    void tickBuffs();
}
