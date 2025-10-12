package de.hhn.it.devtools.apis.shapesurvivor;
/**
 * Represents the player in the game.
 *
 * @param position current position of the player
 * @param currentHealth current health points
 * @param maxHealth maximum health points
 * @param movementSpeed movement speed
 * @param baseDamage base damage dealt by weapons
 * @param attackSpeed attack speed multiplier
 * @param damageResistance damage resistance percentage
 * @param level current player level
 * @param experience current experience points
 * @param experienceToNextLevel experience needed to reach next level
 * @param equippedWeapons array of equipped weapons
 */
public record Player(
        Position position,
        int currentHealth,
        int maxHealth,
        double movementSpeed,
        int baseDamage,
        double attackSpeed,
        double damageResistance,
        int level,
        int experience,
        int experienceToNextLevel,
        Weapon[] equippedWeapons
) {
}
