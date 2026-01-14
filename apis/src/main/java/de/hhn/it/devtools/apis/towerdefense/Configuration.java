package de.hhn.it.devtools.apis.towerdefense;

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

  /**
   * Constructor.
   */
  public Configuration {
    if (mapSize < 5
        || startingHealth <= 0
        || startingMoney < 0
        || enemyPowerMultiplier < 1
        || enemyHealthMultiplier < 1
        || escalation < 1
    ) {
      throw new IllegalArgumentException("Illegal Arguments for Configuration");
    }
  }

  /**
   * Constructor for default values.
   */
  public Configuration() {
    this(DEFAULT_MAP_SIZE,
        DEFAULT_STARTING_HEALTH,
        DEFAULT_STARTING_MONEY,
        DEFAULT_ENEMY_POWER_MULTIPLIER,
        DEFAULT_ENEMY_HEALTH_MULTIPLIER,
        DEFAULT_ESCALATION);
  }

  public static int DEFAULT_MAP_SIZE = 10;
  public static int DEFAULT_STARTING_HEALTH = 50;
  public static int DEFAULT_STARTING_MONEY = 100;
  public static float DEFAULT_ENEMY_POWER_MULTIPLIER = 10.0f;
  public static float DEFAULT_ENEMY_HEALTH_MULTIPLIER = 1.0f;
  public static float DEFAULT_ESCALATION = 1.1f;

}
