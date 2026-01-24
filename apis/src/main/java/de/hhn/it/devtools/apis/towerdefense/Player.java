package de.hhn.it.devtools.apis.towerdefense;

/**
 * Record that stores global Values related to the player.
 *
 * @param health the players current health
 * @param money the players current money
 */
public record Player(int health, int money) {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(Player.class);

  /**
   * Constructor.
   */
  public Player {
    logger.debug("creating Tower with health - {} | money - {}",
        health, money);
  }

}
