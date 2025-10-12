package de.hhn.it.devtools.apis.shapesurvivor;
/**
 * Represents an enemy in the game.
 *
 * @param id unique identifier for the enemy
 * @param position current position of the enemy
 * @param currentHealth current health points
 * @param maxHealth maximum health points
 * @param movementSpeed movement speed units per second
 * @param contactDamage damage dealt on contact with player
 * @param experienceValue experience points awarded when killed
 */
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
