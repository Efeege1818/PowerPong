package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Record that allows creation of a new Monster with fully customizable stats.
 *
 * @param maxHp maximum amount of health the monster has
 * @param currentHp current amount of health the monster has
 * @param attack increases damage dealt
 * @param defense reduces damage taken
 * @param evasionChance chance to not take any damage
 * @param critChance chance to inflict double damage
 * @param element element of the monster
 */
public record Monster(int maxHp,int currentHp, int attack, int defense, double evasionChance, double critChance, Element element) {
}
