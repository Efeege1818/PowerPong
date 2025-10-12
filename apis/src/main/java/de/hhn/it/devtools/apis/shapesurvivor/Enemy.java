package de.hhn.it.devtools.apis.shapesurvivor;

public record Enemy(
        int id,
        Position position,
        int currentHealth,
        int maxHealth,
        double movementSpeed,
        int contactDamage,
        int experienceValue
) {
}
