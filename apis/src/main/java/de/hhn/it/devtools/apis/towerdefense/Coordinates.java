package de.hhn.it.devtools.apis.towerdefense;

/**
 * Holds Coordinates for the positioning of Towers and Enemies.
 *
 * @param x Position on the ordinate
 * @param y Position on the abscissa
 */
public record Coordinates(float x, float y) {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(Coordinates.class);

  /**
   * Constructor.
   */
  public Coordinates {
    logger.trace("creating Coordinates ({}|{})", x, y);
  }
}
