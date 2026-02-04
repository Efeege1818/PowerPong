package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.components.towerdefense.MapToolbox;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MapToolboxBadCasesTest {

  MapToolbox mapToolbox;

  @BeforeEach
  public void setup() {
    mapToolbox = new MapToolbox(30L);
  }

  @AfterEach
  public void tearDown() {
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
  void testIsAllowedOutOfBounds() {
    mapToolbox.generateMap(5);
    assertThrows(IllegalArgumentException.class, () -> mapToolbox.isAllowed(new Coordinates(10, 10)));
  }

  @Test
  void testIsAllowedWithoutMap() {
    assertThrows(IllegalStateException.class, () -> mapToolbox.isAllowed(new Coordinates(0, 0)));
  }

  @Test
  void testGetExtendedPathWithoutGeneration() {
    assertThrows(IllegalStateException.class, () -> mapToolbox.getExtendedPath());
  }
}
