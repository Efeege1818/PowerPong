package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiGoodCasesTest {

  @Test
  void testCoordinateEqualityAndHashCode() {
    Coordinate a = new Coordinate(5, 10);
    Coordinate b = new Coordinate(5, 10);
    Coordinate c = new Coordinate(6, 10);

    assertEquals(a, b, "Gleiche Koordinaten müssen gleich sein");
    assertEquals(a.hashCode(), b.hashCode(), "Equal objects sollten gleiches hashCode haben");
    assertNotEquals(a, c, "Unterschiedliche Koordinaten dürfen nicht gleich sein");
  }

  @Test
  void testRecordAccessorsForEntities() {
    Coordinate coord = new Coordinate(1, 2);
    Alien alien = new Alien(coord, 3, AlienType.BASIC, 42);
    Barrier barrier = new Barrier(coord, 7);
    Ship ship = new Ship(coord, 5);
    Projectile proj = new Projectile(coord, 1);
    GameConfiguration cfg = new GameConfiguration(2, Difficulty.NORMAL);

    assertEquals(coord, alien.coordinate());
    assertEquals(3, alien.hitPoints());
    assertEquals(AlienType.BASIC, alien.alienType());
    assertEquals(42, alien.alienId());

    assertEquals(coord, barrier.coordinate());
    assertEquals(7, barrier.barrierId());

    assertEquals(coord, ship.coordinate());
    assertEquals(5, ship.hitPoints());

    assertEquals(coord, proj.coordinate());

    assertEquals(2, cfg.numberOfBarriers());
    assertEquals(Difficulty.NORMAL, cfg.difficulty());
  }

  @Test
  void testEnumValuesCountAndOrdinals() {
    assertEquals(4, Direction.values().length);
    assertEquals(3, Difficulty.values().length);
    assertEquals(5, GameState.values().length);
    assertEquals(1, AlienType.values().length);

    assertEquals(Direction.UP.ordinal(), 0); // sanity check of enum ordering
  }
}
