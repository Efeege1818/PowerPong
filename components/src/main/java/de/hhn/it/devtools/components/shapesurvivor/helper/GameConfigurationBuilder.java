package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.GameConfiguration;
import de.hhn.it.devtools.apis.shapesurvivor.WeaponType;

/**
 * Factory class for creating GameConfiguration instances.
 */
public class GameConfigurationBuilder {

  private static final int DEFAULT_GAME_DURATION = 900; // 15 minutes in seconds
  private static final int DEFAULT_STARTING_HEALTH = 100;
  private static final double DEFAULT_STARTING_SPEED = 5.0;
  private static final int DEFAULT_STARTING_DAMAGE = 10;
  private static final int DEFAULT_INITIAL_WEAPON_COUNT = 1;
  private static final double DEFAULT_ENEMY_SPAWN_RATE = 1.0;

  /**
   * Creates a GameConfiguration based on difficulty level.
   *
   * @param difficulty The difficulty level ("Easy", "Normal", "Hard", "Nightmare")
   * @param weapon The starting weapon
   * @param fieldWidth The game field width
   * @param fieldHeight The game field height
   * @return A configured GameConfiguration instance
   */
  public static GameConfiguration fromDifficulty(
          String difficulty,
          WeaponType weapon,
          int fieldWidth,
          int fieldHeight) {

    double difficultyMultiplier = calculateDifficultyMultiplier(difficulty);

    return new GameConfiguration(
            DEFAULT_GAME_DURATION,
            fieldWidth,
            fieldHeight,
            DEFAULT_STARTING_HEALTH,
            DEFAULT_STARTING_SPEED,
            DEFAULT_STARTING_DAMAGE,
            DEFAULT_INITIAL_WEAPON_COUNT,
            DEFAULT_ENEMY_SPAWN_RATE,
            difficultyMultiplier,
            new WeaponType[]{weapon}
    );
  }

  /**
   * Calculates the difficulty multiplier based on the difficulty string.
   * This is business logic that belongs in the service layer.
   *
   * @param difficulty The difficulty level string
   * @return The multiplier value
   */
  private static double calculateDifficultyMultiplier(String difficulty) {
    return switch (difficulty) {
      case "Easy" -> 0.75;
      case "Hard" -> 1.5;
      case "Nightmare" -> 2.0;
      default -> 1.0;
    };
  }

  /**
   * Creates a custom GameConfiguration with specific parameters.
   *
   * @param gameDuration Game duration in seconds
   * @param fieldWidth Field width
   * @param fieldHeight Field height
   * @param startingHealth Starting player health
   * @param startingSpeed Starting player speed
   * @param startingDamage Starting player damage
   * @param difficultyMultiplier Enemy difficulty multiplier
   * @param initialWeapons Array of starting weapons
   * @return A configured GameConfiguration instance
   */
  public static GameConfiguration custom(
          int gameDuration,
          int fieldWidth,
          int fieldHeight,
          int startingHealth,
          double startingSpeed,
          int startingDamage,
          double difficultyMultiplier,
          WeaponType... initialWeapons) {

    return new GameConfiguration(
            gameDuration,
            fieldWidth,
            fieldHeight,
            startingHealth,
            startingSpeed,
            startingDamage,
            initialWeapons.length,
            DEFAULT_ENEMY_SPAWN_RATE,
            difficultyMultiplier,
            initialWeapons
    );
  }

  /**
   * Creates a default GameConfiguration for testing or quick start.
   */
  public static GameConfiguration defaultConfiguration(int fieldWidth, int fieldHeight) {
    return fromDifficulty("Normal", WeaponType.SWORD, fieldWidth, fieldHeight);
  }
}