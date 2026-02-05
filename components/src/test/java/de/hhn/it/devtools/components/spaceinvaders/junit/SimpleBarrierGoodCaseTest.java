package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleBarrier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBarrierGoodCaseTest {

  @Test
  void testGettersAndImmutableBarrier() {
    Coordinate c = new Coordinate(10, 20);
    SimpleBarrier b = new SimpleBarrier(c, 5);
    assertEquals(5, b.getId());
    assertEquals(c, b.getCoordinate());
    assertNotNull(b.getImmutableBarrier());
  }

  @Test
  void testEqualsAndHashCodeConsistency() {
    Coordinate c = new Coordinate(1, 1);
    SimpleBarrier b1 = new SimpleBarrier(c, 1);
    SimpleBarrier b2 = new SimpleBarrier(c, 1);
    SimpleBarrier b3 = new SimpleBarrier(new Coordinate(2, 2), 2);

    assertEquals(b1, b2);
    assertEquals(b1.hashCode(), b2.hashCode());
    assertNotEquals(b1, b3);
  }
}

