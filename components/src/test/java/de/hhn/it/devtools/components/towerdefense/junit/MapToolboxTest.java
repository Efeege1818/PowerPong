package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.apis.towerdefense.Direction;
import de.hhn.it.devtools.apis.towerdefense.Grid;
import de.hhn.it.devtools.components.towerdefense.MapToolbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public class MapToolboxTest {

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(MapToolboxTest.class);


  MapToolbox mapToolbox;

  @BeforeEach
  public void setup() {
    mapToolbox = new MapToolbox(30L);
  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  void testGenerateMapValidSize() {
    mapToolbox.generateMap(10);
    assertNotNull(mapToolbox.getGrid());
    assertFalse(mapToolbox.getPath().isEmpty());
  }

  @Test
  void testGenerateMapInvalidSize() {
    assertThrows(IllegalArgumentException.class, () -> mapToolbox.generateMap(1));
  }

  @Test
  void testGetGridWithoutGeneration() {
    assertThrows(IllegalStateException.class, () -> mapToolbox.getGrid());
  }

  @Test
  void testGetPathWithoutGeneration() {
    assertThrows(IllegalStateException.class, () -> mapToolbox.getPath());
  }

  @Test
  void testIsAllowedValidCoordinate() {
    mapToolbox.generateMap(8);
    Coordinates c = new Coordinates(3, 3);
    boolean allowed = mapToolbox.isAllowed(c);
    assertDoesNotThrow(() -> assertTrue(allowed));
  }

  @Test
  void testIsAllowedOutOfBounds() {
    mapToolbox.generateMap(5);
    assertThrows(IllegalArgumentException.class, () -> mapToolbox.isAllowed(new Coordinates(10, 10)));
  }

  @Test
  void testIsAllowedWithoutMap() {
    assertThrows(IllegalStateException.class, () -> mapToolbox.isAllowed(new Coordinates(0, 0)));
  }

  @Test
  void testGetStartAndEndPoint() {
    mapToolbox.generateMap(10);
    Coordinates start = mapToolbox.getStartPoint();
    Coordinates end = mapToolbox.getEndPoint();

    assertEquals(0, (int) start.x());
    assertEquals(9, (int) end.x());
  }

  @Test
  void testGetExtendedPath() {
    mapToolbox.generateMap(6);
    List<Coordinates> path = mapToolbox.getPath();
    List<Coordinates> extended = mapToolbox.getExtendedPath();

    assertTrue(extended.size() > path.size());
    assertEquals(path.getLast(), extended.getLast());
  }

  @Test
  void testGetExtendedPathWithoutGeneration() {
    assertThrows(IllegalStateException.class, () -> mapToolbox.getExtendedPath());
  }

  @Test
  void testGridContainsDirections() {
    mapToolbox.generateMap(7);
    Grid grid = mapToolbox.getGrid();

    boolean hasPath = false;
    for (Direction[] row : grid.grid()) {
      for (Direction direct : row) {
        if (direct != Direction.NONE) {
          hasPath = true;
          break;
        }
      }
    }
    assertTrue(hasPath);
  }

}
