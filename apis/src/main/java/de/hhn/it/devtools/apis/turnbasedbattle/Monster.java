package de.hhn.it.devtools.apis.turnbasedbattle;

import java.util.HashMap;

/**
 * Record that allows creation of a new Monster with fully customizable stats.
 *
 * @param maxHp maximum amount of health the monster has.
 * @param currentHp current amount of health the monster has.
 * @param attack increases damage dealt.
 * @param defense reduces damage taken.
 * @param evasionChance chance to not take any damage.
 * @param critChance chance to inflict double damage.
 * @param element element of the monster.
 * @param moves array with all moves the monster is able to execute.
 */
public record Monster(int maxHp,int currentHp, int attack, int defense, double evasionChance, double critChance, Element element, HashMap<Integer, Move> moves) {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Monster.class);

    /**
     * Checks if the monster's parameters are valid.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    public Monster {
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp must be positive");
        }
        if (currentHp < 0 || currentHp > maxHp) {
            throw new IllegalArgumentException("currentHp must be between 0 and maxHp");
        }
        if (attack < 0 || defense < 0) {
            throw new IllegalArgumentException("attack and defense must be non-negative");
        }
        if (evasionChance < 0 || evasionChance > 1 || critChance < 0 || critChance > 1) {
            throw new IllegalArgumentException("chances must be between 0 and 1");
        }
        if (moves.size() < 5) {
            throw new IllegalArgumentException("at least 5 moves should be specified");
        }
    }

    /**
     * Constructor for creating monsters without current health.
     *
     * @param maxHp maximum amount of health the monster has.
     * @param attack increases damage dealt.
     * @param defense reduces damage taken.
     * @param evasionChance chance to not take any damage.
     * @param critChance chance to inflict double damage.
     * @param element element of the monster.
     *
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Monster(int maxHp, int attack, int defense, double evasionChance, double critChance, Element element,  HashMap<Integer, Move> moves) {
        this(maxHp, maxHp, attack, defense, evasionChance, critChance, element,  moves);
    }
}
