package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Direction;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

// LOCKED : L.Alischer

// TODO: Fix generative algorithm
// TODO: Implement tests

/**
 * Utility class responsible for generating and managing the game map
 * for the tower defense component.
 *
 * <p>This class encapsulates all logic related to grid creation,
 * path generation and validation of grid coordinates. It does not
 * handle enemies, towers or game logic.</p>
 */
public class MapToolbox {

  private Grid grid;
  private List<Coordinates> path;
  private int originSize;

  /**
   * Initializes a new grid of the given size and triggers
   * the generation of a valid enemy path.
   *
   * @param size the width and height of the grid; must be greater than zero
   * @throws IllegalArgumentException if {@code size} is less than or equal to zero
   */

  public void generateMap(int size) {
    if (size <= 0) {
      throw new IllegalArgumentException("Grid size must be greater than zero.");
    }
    this.originSize = size;
    Direction[][] map = new Direction[size][size];

    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        map[row][col] = Direction.NONE;
      }
    }

    this.grid = new Grid(map);
    generatePath();
  }

  /**
   * Getter for the already generated Grid, even without a valid path on it.
   *
   * @return the generated {@link Grid}
   * @throws IllegalStateException if the grid has not been initialized yet
   */
  public Grid getGrid() throws IllegalStateException {
    if (this.grid == null) {
      throw new IllegalStateException("No grid has been generated.");
    }
    return this.grid;
  }

  /**
   * Returns the generated enemy path.
   *
   * @return an ordered list of {@link Coordinates} representing the path
   * @throws IllegalStateException if no path has been generated yet
   */
  public List<Coordinates> getPath() throws IllegalStateException {
    if (this.path == null || this.path.isEmpty()) {
      throw new IllegalStateException("No path has been generated.");
    }
    return this.path;
  }

  /**
   * Checks whether a given coordinate represents a valid and free grid field.
   *
   * @param coordinates the coordinate to be checked
   * @return {@code true} if the coordinate can be used, {@code false} otherwise
   * @throws IllegalStateException    if the grid or path has not been generated
   *                                  or the size is negative
   * @throws IllegalArgumentException if the coordinate is {@code null} or out of bounds
   */
  public boolean isAllowed(Coordinates coordinates) throws IllegalStateException {
    if (this.path == null || this.grid == null || originSize <= 0 || this.path.isEmpty()) {
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
   * Calculates the two-dimensional Euclidean distance between two coordinates.
   *
   * @param coordinate1 the first coordinate
   * @param coordinate2 the second coordinate
   * @return the Euclidean distance between the two coordinates
   * @throws IllegalArgumentException if either coordinate is {@code null}
   */
  public double calcDistance(Coordinates coordinate1, Coordinates coordinate2)
          throws IllegalArgumentException {
    if (coordinate1 == null || coordinate2 == null) {
      throw new IllegalArgumentException("Coordinates must not be null.");
    }

    return calcDistance(coordinate1.x() - coordinate2.x(), coordinate1.y() - coordinate2.y());
  }

  /**
   * Calculates the absolute distance between two scalar values.
   *
   * <p>This method serves as a helper for distance calculations
   * and returns the absolute difference of the two values.</p>
   *
   * @param coordinate1 the first value
   * @param coordinate2 the second value
   * @return the absolute distance between the two values
   */
  public float calcDistance(float coordinate1, float coordinate2) throws IllegalArgumentException {

    return Math.abs(coordinate1 - coordinate2);
  }

  /**
   * Returns the starting coordinate of the generated enemy path.
   *
   * <p>The start point corresponds to the first coordinate in the internally
   * stored path and is located on the left border of the grid.</p>
   *
   * @return the start coordinate of the enemy path
   * @throws IllegalStateException if no path has been generated
   */
  public Coordinates getStartPoint() throws IllegalStateException {
    if (this.path == null || this.grid == null || this.path.isEmpty()) {
      throw new IllegalStateException("No path or grid has been generated.");
    }
    return this.path.getFirst();
  }

  /**
   * Returns the end coordinate of the generated enemy path.
   *
   * <p>The end point corresponds to the last coordinate in the internally
   * stored path and is located on the right border of the grid.</p>
   *
   * <p>The goal always points {@link Direction#EAST}.</p>
   *
   * @return the goal coordinate of the enemy path
   * @throws IllegalStateException if no path has been generated
   */
  public Coordinates getEndPoint() {
    if (this.path == null || this.grid == null || this.path.isEmpty()) {
      throw new IllegalStateException("No path or grid has been generated.");
    }
    return path.getLast();
  }

  /**
   * Generates a valid enemy path on the grid.
   *
   * <p>The method creates a path consisting of three segments:
   * the start point on the left border,
   * a randomly chosen midpoint on either the top or bottom border,
   * and a goal point on the right border.
   * The full path is calculated using the Dijkstra algorithm.</p>
   *
   * <p>The resulting path is written into the grid as {@link Direction} values
   * and stored internally as a list of {@link Coordinates}.</p>
   *
   * @throws IllegalStateException if the grid has not been initialized
   *                               or the grid size is invalid or too small
   * @throws RuntimeException      if no valid path can be generated between the required points
   */
  private void generatePath() {
    // TODO: Tests
    // TODO: Logic-check
    if (grid == null) {
      throw new IllegalStateException("No grid has been generated for a path to be generated.");
    }
    if (originSize <= 0) {
      throw new IllegalStateException("Grid size must be greater than zero.");
    }
    int size = originSize;
    Random random = new Random();

    Coordinates midPoint = random.nextBoolean()
            ? new Coordinates(random.nextInt(size), 0)
            : new Coordinates(random.nextInt(size), size - 1);

    Coordinates start = new Coordinates(0, random.nextInt(size));

    List<Coordinates> path1 = dijkstra(start, midPoint, null);

    if (path1.isEmpty()) {
      throw new RuntimeException("No path found from start to midpoint.");
    }

    boolean[][] blockedTiles = new boolean[size][size];
    for (Coordinates c : path1) {
      blockedTiles[(int) c.y()][(int) c.x()] = true;
    }

    Coordinates goal = new Coordinates(size - 1, random.nextInt(size));

    blockedTiles[(int) midPoint.y()][(int) midPoint.x()] = false;
    List<Coordinates> path2 = dijkstra(midPoint, goal, blockedTiles);

    if (path2.isEmpty()) {
      throw new RuntimeException("No path found from midpoint to goal.");
    }

    if (path1.size() < 2) {
      throw new RuntimeException("Path to midpoint is too short to merge.");
    }
    path1.removeLast();

    List<Coordinates> fullPath = new ArrayList<>();
    fullPath.addAll(path1);
    fullPath.addAll(path2);

    this.path = fullPath;
    // Important! this makes an active reference not a copy!
    Direction[][] map = grid.grid();

    for (int i = 0; i < fullPath.size() - 1; i++) {
      Coordinates from = fullPath.get(i);
      Coordinates to = fullPath.get(i + 1);

      int fx = (int) from.x();
      int fy = (int) from.y();
      int tx = (int) to.x();
      int ty = (int) to.y();

      if (tx == fx + 1) {
        map[fy][fx] = Direction.EAST;
      } else if (tx == fx - 1) {
        map[fy][fx] = Direction.WEST;
      } else if (ty == fy + 1) {
        map[fy][fx] = Direction.SOUTH;
      } else if (ty == fy - 1) {
        map[fy][fx] = Direction.NORTH;
      }

      Coordinates beforeGoal = fullPath.get(fullPath.size() - 2);
      Coordinates goal2 = fullPath.getLast();

      int bx = (int) beforeGoal.x();
      int by = (int) beforeGoal.y();
      int gx = (int) goal2.x();
      int gy = (int) goal2.y();

      map[gy][gx] = Direction.EAST;
    }
  }

  /**
   * Computes the shortest path between two coordinates using the Dijkstra algorithm.
   *
   * <p>The algorithm operates on a two-dimensional grid with uniform movement
   * costs. Optionally, specific tiles can be blocked to prevent traversal.</p>
   *
   * <p>The resulting path includes both the start and goal coordinates.
   * If no path can be found, an empty list is returned.</p>
   *
   * @param start        the starting coordinate
   * @param goal         the target coordinate
   * @param blockedTiles a grid of blocked tiles; may be {@code null} for the first path
   * @return a list of coordinates representing the path, or an empty list if no path exists
   */
  private List<Coordinates> dijkstra(Coordinates start,
                                     Coordinates goal,
                                     boolean[][] blockedTiles) {
    int size = originSize;
    double[][] distance = new double[size][size];

    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        distance[y][x] = Double.POSITIVE_INFINITY;
      }
    }

    distance[(int) start.y()][(int) start.x()] = 0;

    PriorityQueue<Coordinates> queue =
            new PriorityQueue<>(Comparator.comparingDouble(
                    c -> distance[(int) c.y()][(int) c.x()])
            );

    queue.add(start);

    Coordinates[][] prev = new Coordinates[size][size];
    boolean[][] visited = new boolean[size][size];

    while (!queue.isEmpty()) {
      Coordinates current = queue.poll();
      int cx = (int) current.x();
      int cy = (int) current.y();

      if (visited[cy][cx]) {
        continue;
      }
      visited[cy][cx] = true;

      if (current.equals(goal)) {
        break;
      }

      List<Coordinates> neighbors = List.of(
              new Coordinates(cx + 1, cy),
              new Coordinates(cx - 1, cy),
              new Coordinates(cx, cy + 1),
              new Coordinates(cx, cy - 1)
      );

      for (Coordinates n : neighbors) {
        int nx = (int) n.x();
        int ny = (int) n.y();

        if (nx < 0 || ny < 0 || nx >= size || ny >= size) {
          continue;
        }

        if (blockedTiles != null && blockedTiles[ny][nx]) {
          continue;
        }

        double altDistance = distance[cy][cx] + 1;
        if (altDistance < distance[ny][nx]) {
          distance[ny][nx] = altDistance;
          prev[ny][nx] = current;
          queue.add(n);
        }
      }
    }

    List<Coordinates> path = new ArrayList<>();
    Coordinates step = goal;

    if (prev[(int) step.y()][(int) step.x()] == null && !step.equals(start)) {
      return path;
    }

    while (step != null) {
      path.add(step);
      step = prev[(int) step.y()][(int) step.x()];
    }

    Collections.reverse(path);
    return path;
  }
}