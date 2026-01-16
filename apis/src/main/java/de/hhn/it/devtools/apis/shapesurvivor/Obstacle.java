package de.hhn.it.devtools.apis.shapesurvivor;

/**
 * Represents an obstacle on the map.
 */
public record Obstacle(
        int xpos,
        int ypos,
        int width,
        int height,
        ObstacleType type
) {

  /**
   * Types of obstacles.
   */
  public enum ObstacleType {
    ROCK,
    TREE,
    WALL,
    PILLAR,
    BUSH
  }

  /**
   * Checks collision with a circular entity.
   */
  public boolean collidesWith(Position pos, int radius) {
    return pos.x() + radius > xpos
            && pos.x() - radius < xpos + width
            && pos.y() + radius > ypos
            && pos.y() - radius < ypos + height;
  }
}
