package de.hhn.it.devtools.apis.shapesurvivor;

public record GameStatistics(
        int enemiesKilled,
        int damageDealt,
        int damageTaken,
        int wavesCompleted,
        int highestLevel,
        int totalExperienceGained,
        long gameTimeElapsedSeconds
) {
}
