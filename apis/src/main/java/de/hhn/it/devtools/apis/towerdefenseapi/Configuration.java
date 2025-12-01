package de.hhn.it.devtools.apis.towerdefenseapi;


/**
 * Record to save general Setting about the game.
 *
 * @param mapSize height and length of the Map
 * @param playerStartingHealth the health of the player at the start of the game
 * @param enemyPowerMultiplier a multiplier, that is applied to the enemy power
 * @param startingMoney the amount of money the player has at the beginning of the game
 * @param moneyRate the default amount of money, that is awarded for every enemy kill
 */
public record Configuration(int mapSize,
                            int playerStartingHealth,
                            float enemyPowerMultiplier,
                            int startingMoney,
                            int moneyRate) {


  static int DEFAULT_MAP_SIZE = 10;
  static int DEFAULT_PLAYER_STARTING_HEALTH = 50;
  static float DEFAULT_ENEMY_POWER_MULTIPLIER = 1.5f;
  static int DEFAULT_STARTING_MONEY = 100;
  static int DEFAULT_MONEY_RATE = 1; // how much money per enemy kill

}
