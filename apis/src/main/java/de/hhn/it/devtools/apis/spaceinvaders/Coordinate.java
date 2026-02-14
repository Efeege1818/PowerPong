package de.hhn.it.devtools.apis.spaceinvaders;

import java.util.Objects;

/**
 * Used for 2D coordinates. Coordinates are immutable.
 *
 * @param x The x coordinate.
 * @param y The y coordinate.
 */

public record Coordinate(int x, int y) {
  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Coordinate that = (Coordinate) o;
    return x == that.x && y == that.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }
}
