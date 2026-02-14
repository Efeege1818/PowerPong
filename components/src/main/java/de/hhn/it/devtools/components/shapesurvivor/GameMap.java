package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.Obstacle;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an infinite procedurally generated game map with obstacles.
 */
public class GameMap {

  private static final int CHUNK_SIZE = 512;

  private final List<Obstacle> obstacles;
  private final long seed;

  /**
   * Creates a new game map.
   */
  public GameMap() {
    this.seed = System.currentTimeMillis();
    this.obstacles = new ArrayList<>();
    generateInitialChunks();
  }

  private void generateInitialChunks() {
    for (int cx = -1; cx <= 1; cx++) {
      for (int cy = -1; cy <= 1; cy++) {
        generateChunk(cx, cy);
      }
    }
  }

  /**
   * Generates obstacles for a specific chunk.
   */
  private void generateChunk(int chunkX, int chunkY) {
    Random chunkRandom = new Random(seed + chunkX * 1000L + chunkY);

    int startX = chunkX * CHUNK_SIZE;
    int startY = chunkY * CHUNK_SIZE;

    int obstacleCount = 4 + chunkRandom.nextInt(3);

    for (int i = 0; i < obstacleCount; i++) {
      int x = startX + chunkRandom.nextInt(CHUNK_SIZE - 60);
      int y = startY + chunkRandom.nextInt(CHUNK_SIZE - 60);

      if (Math.abs(x) < 100 && Math.abs(y) < 100) {
        continue;
      }

      Obstacle.ObstacleType type =
          Obstacle.ObstacleType
              .values()[chunkRandom.nextInt(Obstacle.ObstacleType.values().length)];
      int size = switch (type) {
        case ROCK -> 20; //+ chunkRandom.nextInt(30);
        case TREE -> 15;
        case WALL -> 60 + chunkRandom.nextInt(40);
        case PILLAR -> 20 + chunkRandom.nextInt(20);
        case BUSH -> 0;
      };

      int height = type == Obstacle.ObstacleType.WALL ? 20 : size;

      obstacles.add(new Obstacle(x, y, size, height, type));
    }
  }

  /**
   * Ensures chunks around the player are loaded.
   */
  public void ensureChunksLoaded(Position playerPos) {
    int playerChunkX = Math.floorDiv(playerPos.x(), CHUNK_SIZE);
    int playerChunkY = Math.floorDiv(playerPos.y(), CHUNK_SIZE);

    for (int cx = playerChunkX - 1; cx <= playerChunkX + 1; cx++) {
      for (int cy = playerChunkY - 1; cy <= playerChunkY + 1; cy++) {
        if (!isChunkLoaded(cx, cy)) {
          generateChunk(cx, cy);
        }
      }
    }

    obstacles.removeIf(obs -> {
      int obsChunkX = Math.floorDiv(obs.xpos(), CHUNK_SIZE);
      int obsChunkY = Math.floorDiv(obs.ypos(), CHUNK_SIZE);
      return Math.abs(obsChunkX - playerChunkX) > 2 || Math.abs(obsChunkY - playerChunkY) > 2;
    });
  }

  private boolean isChunkLoaded(int chunkX, int chunkY) {
    int startX = chunkX * CHUNK_SIZE;
    int endX = startX + CHUNK_SIZE;
    int startY = chunkY * CHUNK_SIZE;
    int endY = startY + CHUNK_SIZE;

    for (Obstacle obs : obstacles) {
      if (obs.xpos() >= startX && obs.xpos() < endX && obs.ypos() >= startY && obs.ypos() < endY) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether a position is valid.
   */
  public boolean isValidPosition(Position pos, int radius) {
    for (Obstacle obstacle : obstacles) {
      if (obstacle.collidesWith(pos, radius)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Adjusts a position to the nearest valid position.
   */
  public Position adjustToValidPosition(Position pos, int radius) {
    if (isValidPosition(pos, radius)) {
      return pos;
    }

    int x = pos.x();
    int y = pos.y();

    for (Obstacle obstacle : obstacles) {
      if (obstacle.collidesWith(pos, radius)) {
        int obstacleX = obstacle.xpos() + obstacle.width() / 2;
        int obstacleY = obstacle.ypos() + obstacle.height() / 2;

        int dx = x - obstacleX;
        int dy = y - obstacleY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
          double pushDist = radius + Math.max(obstacle.width(), obstacle.height()) / 2.0 + 5;
          x = obstacleX + (int) (dx / dist * pushDist);
          y = obstacleY + (int) (dy / dist * pushDist);
        }
      }
    }

    return new Position(x, y);
  }

  /**
   * Returns obstacles visible to the camera.
   */
  public List<Obstacle> getVisibleObstacles(Position cameraPos, int viewWidth, int viewHeight) {

    List<Obstacle> visible = new ArrayList<>();

    int minX = cameraPos.x() - viewWidth / 2 - 100;
    int maxX = cameraPos.x() + viewWidth / 2 + 100;
    int minY = cameraPos.y() - viewHeight / 2 - 100;
    int maxY = cameraPos.y() + viewHeight / 2 + 100;

    for (Obstacle obs : obstacles) {
      if (obs.xpos() + obs.width() >= minX && obs.xpos() <= maxX && obs.ypos()
          + obs.height() >= minY && obs.ypos() <= maxY) {
        visible.add(obs);
      }
    }

    return visible;
  }

  /**
   * Clears obstacles in a radius around a position (typically for spawn safety).
   */
  public void clearSpawnArea(Position center, int radius) {
    obstacles.removeIf(obs -> {
      int obsCenterX = obs.xpos() + obs.width() / 2;
      int obsCenterY = obs.ypos() + obs.height() / 2;
      int dx = obsCenterX - center.x();
      int dy = obsCenterY - center.y();
      double distance = Math.sqrt(dx * dx + dy * dy);
      return distance < radius;
    });
  }

  public List<Obstacle> getObstacles() {
    return new ArrayList<>(obstacles);
  }
}
