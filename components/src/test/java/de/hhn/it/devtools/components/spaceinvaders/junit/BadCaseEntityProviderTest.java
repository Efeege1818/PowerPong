package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import de.hhn.it.devtools.components.spaceinvaders.utils.Constants;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BadCaseEntityProviderTest {

  private SimpleSpaceInvadersService testService;
  private EntityProvider provider;

  static class TestService extends SimpleSpaceInvadersService {
    // keep default behavior but avoid side effects; notifier can be left as-is for these tests
  }

  @BeforeEach
  void setUp() {
    testService = new TestService();
    provider = new EntityProvider(testService);
  }

  @SuppressWarnings("unchecked")
  private <T> T getPrivateField(Object target, String fieldName) throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    return (T) f.get(target);
  }

  @Test
  void testFillHitBoxZeroAndNegativeDimensions() {
    Coordinate c = new Coordinate(5, 5);
    var hitboxZeroX = EntityProvider.fillHitBox(c, 0, 3);
    var hitboxZeroY = EntityProvider.fillHitBox(c, 3, 0);
    var hitboxNegative = EntityProvider.fillHitBox(c, -1, -2);

    assertNotNull(hitboxZeroX);
    assertNotNull(hitboxZeroY);
    assertNotNull(hitboxNegative);

    assertTrue(hitboxZeroX.isEmpty(), "Expected empty hitbox when x dimension is 0");
    assertTrue(hitboxZeroY.isEmpty(), "Expected empty hitbox when y dimension is 0");
    assertTrue(hitboxNegative.isEmpty(), "Expected empty hitbox when dimensions are negative");
  }

  @Test
  void testShootAliensDoesNothingWhenNoAliens() throws Exception {
    // clear aliens
    HashMap<Integer, ?> aliens = getPrivateField(provider, "aliens");
    aliens.clear();

    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    int before = projectiles.size();

    // should not throw and should not add projectiles
    provider.shootAliens();

    assertEquals(before, projectiles.size(), "No projectiles should be added when there are no aliens");
  }

  @Test
  void testCheckCollisionSkipsWhenNoAliens() throws Exception {
    // clear aliens so checkCollision returns early
    HashMap<Integer, ?> aliens = getPrivateField(provider, "aliens");
    aliens.clear();

    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    SimpleShip player = getPrivateField(provider, "player");

    projectiles.clear();
    // place a projectile on player's coordinate and direction DOWN (alien shot)
    projectiles.add(new SimpleProjectile(new Coordinate(player.getCoordinate().x(), player.getCoordinate().y()), Direction.DOWN, Constants.BASE_DAMAGE));

    // before hit
    assertEquals(3, player.getHitPoints());
    provider.checkCollision();
    // because aliens are empty, checkCollision returns immediately and player should not be hit
    assertEquals(3, player.getHitPoints(), "Player should not be hit when aliens list is empty and checkCollision returns early");
  }
}

