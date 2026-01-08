package de.hhn.it.devtools.apis.shapesurvivor;

public record UpgradeOption(
        UpgradeType type,
        String name,
        String description,
        WeaponType weaponType,
        PlayerAttribute attribute,
        double value,
        boolean isMultiplier
) {
}
