package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Record for creating a new monster.
 * This allows for creating a new monster with fully customizable stats.
 */
public record Monster(int maxHp, int attack, int defense, double evasionChance, double critChance, Element element) {
}
