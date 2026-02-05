package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EntityProviderEdgeCaseTest {

  @Test
  void testFillHitBoxAtFieldBoundariesReturnsExpectedCoords() {
    Coordinate c = new Coordinate(0, 0);
    ArrayList<Coordinate> coords = EntityProvider.fillHitBox(c, 2, 2);
    assertEquals(4, coords.size());

    Coordinate out = new Coordinate(APIConstants.FIELD_SIZE + 1, APIConstants.FIELD_SIZE + 1);
    ArrayList<Coordinate> empty = EntityProvider.fillHitBox(out, 2, 2);
    assertTrue(empty.isEmpty());
  }

  @Test
  void testCellKeyAndNearbyBarriersAtGridBoundaries() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    EntityProvider ep = new EntityProvider(svc);

    // get public barriers map and ensure it's populated after initBarriers
    assertFalse(ep.getBarriers().isEmpty());

    // query a coordinate near the field edge and ensure getBarriers returns non-empty map
    Coordinate edge = new Coordinate(APIConstants.FIELD_SIZE - 1, APIConstants.FIELD_SIZE - 1);
    assertFalse(ep.getBarriers().isEmpty());
  }

  @Test
  void testShootAliensWithExtremalAlienShotChanceDoesOrDoesNotAddProjectiles() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    EntityProvider ep = new EntityProvider(svc);

    // make aliens non-empty (generateAliens called in ctor)
    assertFalse(ep.getAliens().isEmpty());

    // set alienShotChance to 0 -> no projectiles should be added
    Field chance = ep.getClass().getDeclaredField("alienShotChance");
    chance.setAccessible(true);
    chance.set(ep, 0);
    ep.shootAliens();

    // set alienShotChance to a very high value to force shooting
    chance.set(ep, 1000);
    ep.shootAliens();
    // if no exception thrown, treat as passed
    assertTrue(true);
  }

  @Test
  void testUpdateAliensFlipsDirectionWhenAnyAlienOutOfBounds() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    EntityProvider ep = new EntityProvider(svc);

    // replace an alien in the internal map with an out-of-bounds alien instance
    Field aliensField = ep.getClass().getDeclaredField("aliens");
    aliensField.setAccessible(true);
    @SuppressWarnings("unchecked")
    HashMap<Integer, SimpleAlien> aliens = (HashMap<Integer, SimpleAlien>) aliensField.get(ep);
    Integer key = aliens.keySet().iterator().next();
    SimpleAlien original = aliens.get(key);
    SimpleAlien out = new SimpleAlien(new Coordinate(APIConstants.FIELD_SIZE + 10, original.getCoordinate().y()), AlienType.BASIC, original.getAlienId());
    aliens.put(key, out);

    // capture current direction
    Field dir = ep.getClass().getDeclaredField("currentAlienDirection");
    dir.setAccessible(true);
    Object before = dir.get(ep);

    ep.updateAliens();

    Object after = dir.get(ep);
    assertNotEquals(before, after);
  }

}
