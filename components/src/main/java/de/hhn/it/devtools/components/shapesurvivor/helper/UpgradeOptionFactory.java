package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.PlayerAttribute;
import de.hhn.it.devtools.apis.shapesurvivor.UpgradeOption;
import de.hhn.it.devtools.apis.shapesurvivor.WeaponType;
import de.hhn.it.devtools.apis.shapesurvivor.UpgradeType;

/**
 * Factory class for creating UpgradeOption instances.
 * Contains the business logic for generating upgrade options with appropriate
 * names, descriptions, and values.
 */
public class UpgradeOptionFactory {

    /**
     * Creates a weapon upgrade option for an existing weapon.
     *
     * @param weaponType the type of weapon to upgrade
     * @return an UpgradeOption for upgrading the weapon
     */
    public static UpgradeOption createWeaponUpgrade(WeaponType weaponType) {
        return new UpgradeOption(
                UpgradeType.WEAPON,
                weaponType.name() + " Level Up",
                "Upgrade " + weaponType.name() + " to next level",
                weaponType,
                null,
                0,
                false
        );
    }

    /**
     * Creates a new weapon unlock option.
     *
     * @param weaponType the type of weapon to unlock
     * @return an UpgradeOption for unlocking a new weapon
     */
    public static UpgradeOption createNewWeapon(WeaponType weaponType) {
        String description = switch (weaponType) {
            case SWORD -> "Circles the player and damages enemies on contact";
            case AURA -> "Deals damage in a circle around the player";
            case WHIP -> "Whips enemies in front and behind the player";
        };

        return new UpgradeOption(
                UpgradeType.NEW_WEAPON,
                "Unlock " + weaponType.name(),
                description,
                weaponType,
                null,
                0,
                false
        );
    }

    /**
     * Creates an attribute upgrade option.
     *
     * @param attribute the attribute to upgrade
     * @param value the value to apply
     * @param isMultiplier whether the value is a multiplier
     * @return an UpgradeOption for upgrading a player attribute
     */
    public static UpgradeOption createAttributeUpgrade(
            PlayerAttribute attribute,
            double value,
            boolean isMultiplier
    ) {
        String name = generateAttributeName(attribute, value, isMultiplier);
        String description = generateAttributeDescription(attribute);

        return new UpgradeOption(
                UpgradeType.ATTRIBUTE,
                name,
                description,
                null,
                attribute,
                value,
                isMultiplier
        );
    }

    private static String generateAttributeName(
            PlayerAttribute attribute,
            double value,
            boolean isMultiplier
    ) {
        return switch (attribute) {
            case MAX_HEALTH -> isMultiplier
                    ? "+" + ((int)((value - 1) * 100)) + "% Max Health"
                    : "+" + (int)value + " Max Health";
            case MOVEMENT_SPEED -> isMultiplier
                    ? "+" + ((int)((value - 1) * 100)) + "% Movement Speed"
                    : "+" + value + " Movement Speed";
            case DAMAGE -> isMultiplier
                    ? "+" + ((int)((value - 1) * 100)) + "% Damage"
                    : "+" + (int)value + " Damage";
            case ATTACK_SPEED -> isMultiplier
                    ? "+" + ((int)((value - 1) * 100)) + "% Attack Speed"
                    : "+" + value + " Attack Speed";
            case DAMAGE_RESISTANCE -> "+" + ((int)(value * 100)) + "% Damage Resistance";
            case HEALTH_REGENERATION -> isMultiplier
                    ? "+" + ((int)((value - 1) * 100)) + "% Health Regen"
                    : "+" + value + " Health Regen";
            case EXPERIENCE_MULTIPLIER -> "+" + ((int)((value - 1) * 100)) + "% XP Gain";
        };
    }

    private static String generateAttributeDescription(PlayerAttribute attribute) {
        return switch (attribute) {
            case MAX_HEALTH -> "Increase maximum health and heal to full";
            case MOVEMENT_SPEED -> "Move faster across the battlefield";
            case DAMAGE -> "Deal more damage with all attacks";
            case ATTACK_SPEED -> "Attack more frequently";
            case DAMAGE_RESISTANCE -> "Take less damage from enemies";
            case HEALTH_REGENERATION -> "Slowly regenerate health over time";
            case EXPERIENCE_MULTIPLIER -> "Gain more experience from kills";
        };
    }
}