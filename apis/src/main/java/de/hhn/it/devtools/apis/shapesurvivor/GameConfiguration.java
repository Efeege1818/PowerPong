package de.hhn.it.devtools.apis.shapesurvivor;
/**
 * Configuration parameters for a ShapeSurvivor game.
 *
 * @param gameDurationSeconds the duration of the game in seconds
 * @param fieldWidth the width of the game field in pixels
 * @param fieldHeight the height of the game field in pixels
 * @param startingPlayerHealth the initial health points of the player
 * @param startingPlayerSpeed the initial movement speed of the player
 * @param startingPlayerDamage the initial damage dealt by the player
 * @param initialWeaponCount the number of weapons the player starts with
 * @param enemySpawnRate the rate multiplier for enemy spawning
 * @param difficultyMultiplier the overall difficulty multiplier
 * @param initialWeapons array of initial weapon types for the player
 */
public record GameConfiguration(
        int gameDurationSeconds,
        int fieldWidth,
        int fieldHeight,
        int startingPlayerHealth,
        double startingPlayerSpeed,
        int startingPlayerDamage,
        int initialWeaponCount,
        double enemySpawnRate,
        double difficultyMultiplier,
        WeaponType[] initialWeapons
) {
}

