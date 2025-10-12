package de.hhn.it.devtools.apis.shapesurvivor;
/**
 * Contains statistical information about the current game.
 *
 * @param enemiesKilled total number of enemies killed
 * @param damageDealt total damage dealt by the player
 * @param damageTaken total damage taken by the player
 * @param wavesCompleted number of enemy waves survived
 * @param highestLevel the highest level reached by the player
 * @param totalExperienceGained total experience points gained
 * @param gameTimeElapsedSeconds time elapsed in the current game in seconds
 */
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
