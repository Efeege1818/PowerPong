package de.hhn.it.devtools.apis.shapesurvivor;
/**
 * Represents a weapon equipped by the player.
 *
 * @param type the type of weapon
 * @param level current upgrade level of the weapon
 * @param damage damage dealt by the weapon
 * @param attackSpeed attack speed multiplier
 * @param range effective range of the weapon
 * @param isActive whether the weapon is currently active
 */
public record Weapon(
        WeaponType type,
        String description,
        int level,
        int damage,
        double attackSpeed,
        double range,
        boolean isActive
) {
}
