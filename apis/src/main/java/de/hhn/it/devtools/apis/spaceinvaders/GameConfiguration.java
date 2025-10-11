package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Configures a Space Invaders game.
 *
 * @param numberOfBarriers sets the number of barriers on the field
 * @param difficulty       sets the difficulty of the game
 */
public record GameConfiguration(int numberOfBarriers, Difficulty difficulty) {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(GameConfiguration.class);
}
