package de.hhn.it.devtools.apis.shapesurvivor;

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

