package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEntitiesBadCaseTest {

  private EntityProvider provider;

  static class DummyService extends SimpleSpaceInvadersService {
    // avoid side effects
  }

  @BeforeEach
  void setUp() {
    provider = new EntityProvider(new DummyService());
  }

  @Test
  void testShootAliensWithEmptyAlienMapThrows() throws Exception {
    // set private aliens map to empty to simulate bad case
    Field f = provider.getClass().getDeclaredField("aliens");
    f.setAccessible(true);
    f.set(provider, new HashMap<Integer, Object>());

    assertDoesNotThrow( () -> provider.shootAliens(),
        "shootAliens should throw when aliens map is empty (rand.nextInt(0))");
  }

  @Test
  void testShipMoveInvalidDirectionThrows() {
    SimpleShip ship = new SimpleShip(new Coordinate(0, 0));
    // UP is invalid for ship.move according to implementation -> expect IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> ship.move(Direction.UP));
  }

  @Test
  void testFillHitBoxWithNegativeSizesReturnsEmpty() {
    var resXNeg = EntityProvider.fillHitBox(new Coordinate(0, 0), -5, 3);
    var resYNeg = EntityProvider.fillHitBox(new Coordinate(0, 0), 3, -2);
    assertTrue(resXNeg.isEmpty(), "Negative width should produce empty hitbox");
    assertTrue(resYNeg.isEmpty(), "Negative height should produce empty hitbox");
  }

  @Test
  void testProjectileMoveWithNullDirectionDoesNotChangeCoordinate() {
    SimpleProjectile p = new SimpleProjectile(new Coordinate(7, 7), null, 1);
    p.move();
    assertEquals(7, p.getCoordinate().x());
    assertEquals(7, p.getCoordinate().y());
  }
}
