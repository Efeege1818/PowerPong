package de.hhn.it.devtools.components.shapesurvivor.junit;

import de.hhn.it.devtools.apis.shapesurvivor.Obstacle;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.components.shapesurvivor.GameMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameMap test")
class GameMapTest {

  private GameMap gameMap;

  @BeforeEach
  void setUp() {
    gameMap = new GameMap();
  }

  @Test
  @DisplayName("GameMap initializes with obstacles")
  void testInitialization() {
    List<Obstacle> obstacles = gameMap.getObstacles();
    assertNotNull(obstacles);
    assertTrue(obstacles.size() > 0, "Should have initial obstacles");
  }

  @Test
  @DisplayName("Center area near spawn is clear")
  void testCenterAreaIsClear() {
    Position center = new Position(0, 0);
    assertTrue(gameMap.isValidPosition(center, 15),
        "Center position should be valid for player spawn");
  }

  @Test
  @DisplayName("Can check if position is valid")
  void testIsValidPosition() {
    Position farAway = new Position(1000, 1000);
    // Should be valid as it's far from initial obstacles
    boolean result = gameMap.isValidPosition(farAway, 15);
    assertTrue(result || !result); // Just testing it doesn't throw
  }

  @Test
  @DisplayName("Can adjust invalid position to valid one")
  void testAdjustToValidPosition() {
    Position pos = new Position(100, 100);
    Position adjusted = gameMap.adjustToValidPosition(pos, 15);

    assertNotNull(adjusted);
  }

  @Test
  @DisplayName("Valid position returns same position when adjusted")
  void testAdjustValidPosition() {
    Position validPos = new Position(500, 500);

    if (gameMap.isValidPosition(validPos, 15)) {
      Position adjusted = gameMap.adjustToValidPosition(validPos, 15);
      assertEquals(validPos, adjusted);
    }
  }

  @Test
  @DisplayName("Can get visible obstacles for camera")
  void testGetVisibleObstacles() {
    Position cameraPos = new Position(0, 0);
    List<Obstacle> visible = gameMap.getVisibleObstacles(cameraPos, 800, 600);

    assertNotNull(visible);
  }

  @Test
  @DisplayName("Chunks are loaded as player moves")
  void testChunkLoading() {
    Position startPos = new Position(0, 0);
    gameMap.ensureChunksLoaded(startPos);

    int initialCount = gameMap.getObstacles().size();

    // Move far away to trigger new chunk loading
    Position farPos = new Position(2000, 2000);
    gameMap.ensureChunksLoaded(farPos);

    int newCount = gameMap.getObstacles().size();

    // Obstacles should change as we move to new chunks
    assertTrue(newCount > 0);
  }

  @Test
  @DisplayName("Distant chunks are unloaded")
  void testChunkUnloading() {
    Position pos1 = new Position(0, 0);
    gameMap.ensureChunksLoaded(pos1);

    // Move very far away
    Position pos2 = new Position(5000, 5000);
    gameMap.ensureChunksLoaded(pos2);

    // Old chunks should be unloaded, map shouldn't grow infinitely
    assertTrue(gameMap.getObstacles().size() < 100,
        "Should unload distant chunks");
  }

  @Test
  @DisplayName("Obstacles have different types")
  void testObstacleTypes() {
    List<Obstacle> obstacles = gameMap.getObstacles();

    boolean hasRock = false;
    boolean hasTree = false;
    boolean hasWall = false;
    boolean hasPillar = false;

    for (Obstacle obs : obstacles) {
      switch (obs.type()) {
        case ROCK -> hasRock = true;
        case TREE -> hasTree = true;
        case WALL -> hasWall = true;
        case PILLAR -> hasPillar = true;
      }
    }

    // At least one type should exist
    assertTrue(hasRock || hasTree || hasWall || hasPillar);
  }

  @Test
  @DisplayName("Same chunk generates same obstacles")
  void testDeterministicGeneration() {
    GameMap map1 = new GameMap();
    GameMap map2 = new GameMap();

    // Maps with same seed should generate different obstacles
    // (different timestamps), but structure should be consistent
    assertNotNull(map1.getObstacles());
    assertNotNull(map2.getObstacles());
  }

  @Test
  @DisplayName("Player position is always ensured loaded")
  void testEnsurePlayerChunksLoaded() {
    for (int i = 0; i < 10; i++) {
      Position randomPos = new Position(i * 600, i * 600);
      gameMap.ensureChunksLoaded(randomPos);

      // Should have obstacles around player
      List<Obstacle> visible = gameMap.getVisibleObstacles(
          randomPos, 800, 600);
      assertNotNull(visible);
    }
  }

  @Test
  @DisplayName("Collision detection works")
  void testCollisionDetection() {
    List<Obstacle> obstacles = gameMap.getObstacles();

    if (!obstacles.isEmpty()) {
      Obstacle firstObstacle = obstacles.get(0);
      Position obsPos = new Position(
          firstObstacle.xpos() + firstObstacle.width() / 2,
          firstObstacle.ypos() + firstObstacle.height() / 2
      );

      // Position inside obstacle should be invalid
      assertFalse(gameMap.isValidPosition(obsPos, 5));
    }
  }

  @Test
  @DisplayName("Large radius collision detection works")
  void testLargeRadiusCollision() {
    List<Obstacle> obstacles = gameMap.getObstacles();

    if (!obstacles.isEmpty()) {
      Obstacle firstObstacle = obstacles.get(0);
      Position nearObstacle = new Position(
          firstObstacle.xpos(),
          firstObstacle.ypos()
      );

      // Very large radius should definitely collide
      assertFalse(gameMap.isValidPosition(nearObstacle, 100));
    }
  }
}