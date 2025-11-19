package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Move;
import de.hhn.it.devtools.apis.turnbasedbattle.MoveType;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuffTracker {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(BuffTracker.class);

    private Map<Integer, Move> buffsMonster1 = new HashMap<>();
    private Map<Integer, Move> buffsMonster2 = new HashMap<>();
    private HashMap<SimpleMonster,Map<Integer, Move>> buffs = new HashMap<>(2);
    private SimpleMonster player1Monster;
    private SimpleMonster player2Monster;

    public BuffTracker(SimpleMonster player1Monster, SimpleMonster player2Monster) {
        this.player1Monster = player1Monster;
        this.player2Monster = player2Monster;
        buffs.put(this.player1Monster, buffsMonster1);
        buffs.put(this.player2Monster, buffsMonster2);
    }

    public void addBuff(Move move, int currentPlayerId) {

        if (move.type() == MoveType.BUFF) {
            SimpleMonster index;
            if (currentPlayerId == 1) {
                index = player1Monster;
            } else {
                index = player2Monster;
            }
            buffs.get(index).put(move.duration(), move);
            logger.debug("Player {} added buff move {}", currentPlayerId, move);
        }
        else if (move.type() == MoveType.DEBUFF) {
            SimpleMonster index;
            if (currentPlayerId == 1) {
                index = player2Monster;
            } else {
                index = player1Monster;
            }
            buffs.get(index).put(move.duration(), move);
            logger.debug("Player {} added debuff move {} to monster {}", currentPlayerId, move, index);
        }
        else {
            logger.warn("Player {} added invalid move type {}", currentPlayerId, move.type());
            throw new IllegalArgumentException("Invalid move type: " + move.type());
        }
    }

    public Map<Integer, Move> getBuffs(int currentPlayerId) {
        SimpleMonster index;
        if (currentPlayerId == 1) {
            index = player1Monster;
        } else {
            index = player2Monster;
        }
        return buffs.get(index);
    }

    public void tickBuffs() {
        HashMap<Integer, Move> newBuffs0 = new HashMap<>();
        buffs.get(this.player1Monster).forEach((duration, move) -> {
            if (duration > 0) {  // Only move buffs that haven't expired
                newBuffs0.put(duration - 1, move);
                logger.debug("Player 1 buff ticked: {} and has now {} turns duration", move, duration - 1);
            } else if (duration == 0) {
                if (move.type() == MoveType.BUFF) {
                    logger.debug("Player 1 buff expired: {}", move);
                    player1Monster.removeBuff(move);
                } else {
                    logger.debug("Player 1 debuff expired: {}", move);
                    player1Monster.removeDebuff(move);
                }
            }
            // Buffs at duration 0 are discarded (expired)
        });
        buffs.put(this.player1Monster, newBuffs0);

        HashMap<Integer, Move> newBuffs1 = new HashMap<>();
        buffs.get(this.player2Monster).forEach((duration, move) -> {
            if (duration > 0) {  // Only move buffs that haven't expired
                newBuffs1.put(duration - 1, move);
                logger.debug("Player 2 buff ticked: {} and has now {} turns duration", move, duration - 1);
            } else if (duration == 0) {
                if (move.type() == MoveType.BUFF) {
                    logger.debug("Player 2 buff expired: {}", move);
                    player2Monster.removeBuff(move);
                } else {
                    logger.debug("Player 2 debuff expired: {}", move);
                    player2Monster.removeDebuff(move);
                }
            }
            // Buffs at duration 0 are discarded (expired)
        });
        buffs.put(this.player2Monster, newBuffs1);

        logger.debug("Player 1 buffs: {}", buffs.get(this.player1Monster));
        logger.debug("Player 2 buffs: {}", buffs.get(this.player2Monster));


    }
}

