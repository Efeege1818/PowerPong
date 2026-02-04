package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.apis.towerdefense.Direction;
import de.hhn.it.devtools.apis.towerdefense.Grid;
import de.hhn.it.devtools.components.towerdefense.MapToolbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class MapToolboxGoodCasesTest {

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
  void testIsAllowedValidCoordinate() {
    mapToolbox.generateMap(8);
    Coordinates c = new Coordinates(3, 3);
    assertTrue(mapToolbox.isAllowed(c));
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
