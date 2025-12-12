package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEntitiesTest {

  @Test
  void testSimpleAlienMoveAndHitAndHitboxSize() {
    SimpleAlien alien = new SimpleAlien(new Coordinate(10, 0), AlienType.BASIC, 1);
    // initial coordinate
    assertEquals(10, alien.getCoordinate().x());
    assertEquals(0, alien.getCoordinate().y());

    // move right
    alien.move(Direction.RIGHT);
    assertEquals(11, alien.getCoordinate().x());
    assertEquals(0, alien.getCoordinate().y());

    // move left
    alien.move(Direction.LEFT);
    assertEquals(10, alien.getCoordinate().x());

    // move down
    alien.move(Direction.DOWN);
    assertEquals(1, alien.getCoordinate().y());

    // hitbox size is 10 * 10 = 100
    assertEquals(100, alien.getHitbox().size());

    // hit points: initial 3 -> calling getHit reduces it
    assertTrue(alien.getHit()); // now 2
    assertTrue(alien.getHit()); // now 1
    assertFalse(alien.getHit()); // now 0 -> dead
  }

  @Test
  void testSimpleProjectileMove() {
    SimpleProjectile pUp = new SimpleProjectile(new Coordinate(5, 5), Direction.UP, 1);
    pUp.move();
    assertEquals(4, pUp.getCoordinate().y());

    SimpleProjectile pDown = new SimpleProjectile(new Coordinate(5, 5), Direction.DOWN, 1);
    pDown.move();
    assertEquals(6, pDown.getCoordinate().y());
  }

  @Test
  void testSimpleShipMoveAndHitbox() {
    SimpleShip ship = new SimpleShip(new Coordinate(10, 10));
    assertEquals(10, ship.getCoordinate().x());
    assertEquals(10, ship.getCoordinate().y());

    ship.move(Direction.LEFT);
    assertEquals(9, ship.getCoordinate().x());

    ship.move(Direction.RIGHT);
    ship.move(Direction.RIGHT);
    assertEquals(11, ship.getCoordinate().x());

    // hitbox size 10 * 10 = 100
    assertEquals(100, ship.getHitbox().size());
  }

}

