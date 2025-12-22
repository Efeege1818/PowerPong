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

// TODO: Method getEndpoint and getStartPoint
// TODO: Fix generative algorithm
// TODO: Implement tests

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
    this.grid = new Grid(map);
    generatePath();
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
   * Adds a path on the grid, whilst randomizing the start and the endpoint.
   *
   * @throws RuntimeException when something went wrong in the algorithm
   */
  private void generatePath() {
    // TODO: Exceptions
    // TODO: Tests
    // TODO: Logic-check
    int size = originSize;
    Random random = new Random();
    Coordinates start = new Coordinates(0, random.nextInt(size));
    Coordinates goal = new Coordinates(size - 1, random.nextInt(size));

    Coordinates mid = random.nextBoolean()
            ? new Coordinates(random.nextInt(size), 0)
            : new Coordinates(random.nextInt(size), size - 1);

    List<Coordinates> path1 = dijkstra(start, mid, null);

    boolean[][] blocked = new boolean[size][size];
    for (Coordinates c : path1) {
      blocked[(int) c.y()][(int) c.x()] = true;
    }

    blocked[(int) mid.y()][(int) mid.x()] = false;
    List<Coordinates> path2 = dijkstra(mid, goal, blocked);

    path1.removeLast();
    List<Coordinates> fullPath = new ArrayList<>();
    fullPath.addAll(path1);
    fullPath.addAll(path2);

    this.path = fullPath;
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
   * Expands on the path with the dijkstra algorithm, using the start-,end- and midpoint.
   */
  private List<Coordinates> dijkstra(Coordinates start, Coordinates goal, boolean[][] blocked) {
    int size = originSize;
    double[][] dist = new double[size][size];

    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        dist[y][x] = Double.POSITIVE_INFINITY;
      }
    }

    dist[(int) start.y()][(int) start.x()] = 0;

    PriorityQueue<Coordinates> queue =
            new PriorityQueue<>(Comparator.comparingDouble(
                    c -> dist[(int) c.y()][(int) c.x()])
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

        if (blocked != null && blocked[ny][nx]) {
          continue;
        }

        double alt = dist[cy][cx] + 1;
        if (alt < dist[ny][nx]) {
          dist[ny][nx] = alt;
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