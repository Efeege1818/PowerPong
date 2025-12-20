package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Direction;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import java.util.List;

// LOCKED : L.Alischer

/**
 * Class for initiating all default map-related tasks, not including enemies and towers.
 */
public class MapToolbox {

  Grid grid;
  List<Coordinates> path;
  int originSize;

  /**
   * Generates a bare Grid.
   *
   * @param size of the Grid (must be positive)
   */
  public void generateMap(int size) {
    this.originSize = size;
    Direction[][] map = new Direction[size][size];

    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        map[row][col] = Direction.NONE;
      }
    }
    // Call for instantiating the path
    generatePath();
    this.grid = new Grid(map);
  }

  /**
   * Getter for the already generated Grid.
   *
   * @return the Grid
   * @throws IllegalStateException when the Grid has not been generated yet.
   */
  public Grid getGrid() throws IllegalStateException {
    if (this.grid == null) {
      throw new IllegalStateException("No grid has been generated.");
    }
    return this.grid;
  }

  /**
   * Returns the enemy's path of the generated grid.
   *
   * @return the path of the grid as a list of coordinates
   * @throws IllegalArgumentException if no grid or path has been generated
   */
  public List<Coordinates> getPath() throws IllegalStateException {
    if (this.path == null) {
      throw new IllegalStateException("No path has been generated.");
    }
    return this.path;
  }

  /**
   * Checks if a coordinate is a valid empty field to use.
   *
   * @param coordinates that is to be checked for possibility
   * @return true if coordinate is an empty field, otherwise false
   * @throws IllegalArgumentException if coordinates are not on the grid
   */
  public boolean isAllowed(Coordinates coordinates) throws IllegalStateException {
    if (this.path == null || this.grid == null) {
      throw new IllegalStateException("No path or grid has been generated.");
    } else if (coordinates == null
            || coordinates.x() > originSize
            || coordinates.y() > originSize
            || coordinates.x() < 0
            || coordinates.y() < 0) {
      throw new IllegalArgumentException("Invalid coordinates provided.");
    }

    return grid.grid()[(int) coordinates.x()][(int) coordinates.y()] == Direction.NONE;
  }

  /**
   * Calculates the Euclidean (2-Dimensional) distance between two coordinate objects.
   *
   * @param coordinate1 first coordinate
   * @param coordinate2 second coordinate
   * @return the distance between the two coordinates as a double
   * @throws IllegalArgumentException if one or both coordinates are invalid
   */
  public double calcDistance(Coordinates coordinate1, Coordinates coordinate2)
          throws IllegalArgumentException {
    if (coordinate1 == null || coordinate2 == null) {
      throw new IllegalArgumentException("Coordinates must not be null.");
    }

    return calcDistance(coordinate1.x() - coordinate2.x(), coordinate1.y() - coordinate2.y());
  }

  /**
   * Calculates the absolute distance between two float values.
   *
   * @param coordinate1 first value
   * @param coordinate2 second value
   * @return the absolute distance between the two values as a float
   * @throws IllegalArgumentException if one or both values are invalid
   */
  public float calcDistance(float coordinate1, float coordinate2) throws IllegalArgumentException {

    return Math.abs(coordinate1 - coordinate2);
  }

  /**
   * Adds a path on the grid using an implementation of the Dijkstra's algorithm.
   *
   * @throws RuntimeException when something went wrong in the algorithm
   */
  private void generatePath() throws RuntimeException {
    // TODO: implement dijkstra
  }
}
