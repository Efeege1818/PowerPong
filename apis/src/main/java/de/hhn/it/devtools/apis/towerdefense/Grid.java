package de.hhn.it.devtools.apis.towerdefense;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data Structure that stores the positions on the Board.
 *
 * @param grid two-dimensional Array that stores the types of tiles on the Game Grid
 */
public record Grid(Direction[][] grid) {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(Grid.class);

  /**
   * Constructor.
   */
  public Grid {
    logger.debug("creating Grid {}", Arrays.deepToString(grid));
    Objects.requireNonNull(grid);
  }
}
