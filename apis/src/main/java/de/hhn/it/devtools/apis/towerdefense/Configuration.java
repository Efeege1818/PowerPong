package de.hhn.it.devtools.apis.towerdefense;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;

/**
 * Record to save general Setting about the game.
 *
 * @param mapSize height and length of the Map
 * @param startingHealth the health of the player at the start of the game
 * @param startingMoney the amount of money the player has at the beginning of the game
 * @param enemyPowerMultiplier a multiplier, that is applied to the enemy power
 * @param enemyHealthMultiplier a multiplier, that is applied to the enemy health
 * @param escalation a multiplier, that increases the enemy health
 *                   in relation to the health of the enemies in the previous wave
 */
public record Configuration(int mapSize,
                            int startingHealth,
                            int startingMoney,
                            float enemyPowerMultiplier,
                            float enemyHealthMultiplier,
                            float escalation) {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(Configuration.class);

  /**
   * Constructor.
   */
  public Configuration {
    if (mapSize < 5
        || startingHealth <= 0
        || startingMoney < 0
        || enemyPowerMultiplier < 1
        || enemyHealthMultiplier <= 0
        || escalation < 1
    ) {
      throw new IllegalArgumentException("Illegal Arguments for Configuration");
    }
  }

  /**
   * Constructor for default values.
   */
  public Configuration() {
    this(Difficulty.NORMAL);
  }

  public Configuration(Difficulty difficulty) {
    this(DEFAULT_MAP_SIZE,

        // Starting Health
        switch (difficulty) {
          case EASY -> 100;
          case NORMAL -> 50;
          case HARD -> 25;
          case IMPOSSIBLE -> 1;
        },

        // Starting Money
        switch (difficulty) {
          case EASY -> 200;
          case NORMAL -> 150;
          case HARD -> 125;
          case IMPOSSIBLE -> 100;
        },

        DEFAULT_ENEMY_POWER_MULTIPLIER,

        // Enemy Health Multiplier
        switch (difficulty) {
          case EASY -> 0.8f;
          case NORMAL -> 1.0f;
          case HARD -> 1.2f;
          case IMPOSSIBLE -> 1.5f;
        },

        // Escalation
        switch (difficulty) {
          case EASY -> 1.1f;
          case NORMAL -> 1.15f;
          case HARD, IMPOSSIBLE -> 1.2f;
        }
    );
    logger.debug("creating Recipe on Difficulty {}", difficulty);
  }

  public static int DEFAULT_MAP_SIZE = 10;
  public static float DEFAULT_ENEMY_POWER_MULTIPLIER = 50.0f;

}
