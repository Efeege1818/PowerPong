package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEntitiesGoodCaseTest {

  @Test
  void testSimpleAlienMoveAndHitAndHitboxSize() {
    SimpleAlien alien = new SimpleAlien(new Coordinate(10, 0), AlienType.BASIC, 1);
    // initial coordinate
    assertEquals(10, alien.getCoordinate().x());
    assertEquals(0, alien.getCoordinate().y());

    // move right
    alien.move(Direction.RIGHT);
    assertEquals(12, alien.getCoordinate().x());
    assertEquals(0, alien.getCoordinate().y());

    // move left
    alien.move(Direction.LEFT);
    assertEquals(10, alien.getCoordinate().x());

    // move down
    alien.move(Direction.DOWN);
    assertEquals(10, alien.getCoordinate().y());

    // hitbox size 25 * 25 = 625
    assertEquals(625, alien.getHitbox().size());

    // hit points: initial 3 -> calling getHit reduces it
    assertTrue(alien.getHit()); // now 2
    assertTrue(alien.getHit()); // now 1
    assertFalse(alien.getHit()); // now 0 -> dead
  }

  @Test
  void testSimpleProjectileMove() {
    SimpleProjectile pUp = new SimpleProjectile(new Coordinate(5, 5), Direction.UP, 1);
    pUp.move();
    assertEquals(2, pUp.getCoordinate().y());

    SimpleProjectile pDown = new SimpleProjectile(new Coordinate(5, 5), Direction.DOWN, 1);
    pDown.move();
    assertEquals(8, pDown.getCoordinate().y());
  }

  @Test
  void testSimpleShipMoveAndHitbox() {
    Coordinate c = new Coordinate(100, 100);
    SimpleShip ship = new SimpleShip(c);
    assertEquals(c.x(), ship.getCoordinate().x());
    assertEquals(c.y(), ship.getCoordinate().y());

    ship.move(Direction.LEFT);
    assertEquals(c.x()-1, ship.getCoordinate().x());

    ship.move(Direction.RIGHT);
    ship.move(Direction.RIGHT);
    assertEquals(c.x()+1, ship.getCoordinate().x());

    // hitbox size 35 * 35 = 1225
    assertEquals(1225, ship.getHitbox().size());
  }

}

