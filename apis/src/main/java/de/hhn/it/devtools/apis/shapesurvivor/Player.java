package de.hhn.it.devtools.apis.shapesurvivor;

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
