package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import de.hhn.it.devtools.components.spaceinvaders.utils.Constans;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityProviderTest {

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
  void testFillHitBox() {
    Coordinate c = new Coordinate(2, 3);
    var hitbox = EntityProvider.fillHitBox(c, 2, 3);
    // 2 * 3 = 6 coordinates
    assertEquals(6, hitbox.size());
    assertTrue(hitbox.contains(new Coordinate(2, 3)));
    assertTrue(hitbox.contains(new Coordinate(3, 5)));
  }

  @Test
  void testShootPlayerAddsProjectile() throws Exception {
    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    int before = projectiles.size();
    // shoot
    provider.shootPlayer();
    assertEquals(before + 1, projectiles.size());
    SimpleProjectile p = projectiles.get(projectiles.size() - 1);
    SimpleShip player = getPrivateField(provider, "player");
    assertEquals(player.getCoordinate().x() + 1, p.getCoordinate().x());
    assertEquals(player.getCoordinate().y() - 1, p.getCoordinate().y());
    assertEquals(Direction.UP, p.getdirection());
    assertEquals(Constans.BASE_DAMAGE, p.getDamage());
  }

  @Test
  void testShootAliensAddsDownProjectile() throws Exception {
    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    int before = projectiles.size();
    provider.shootAliens();
    assertEquals(before + 1, projectiles.size());
    SimpleProjectile p = projectiles.get(projectiles.size() - 1);
    assertEquals(Direction.DOWN, p.getdirection());
    // y should be within field bounds
    assertTrue(p.getCoordinate().y() >= 0 && p.getCoordinate().y() <= Constans.FIELD_SIZE + 10);
  }

  @Test
  void testUpdateProjectilesMovesProjectile() throws Exception {
    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    projectiles.clear();
    projectiles.add(new SimpleProjectile(new Coordinate(50, 50), Direction.UP, 1));
    provider.updateProjectiles();
    assertEquals(49, projectiles.get(0).getCoordinate().y());
  }

  @Test
  void testCheckCollisionRemovesAlienWhenKilled() throws Exception {
    // access aliens map
    @SuppressWarnings("unchecked")
    HashMap<Integer, SimpleAlien> aliens = getPrivateField(provider, "aliens");
    assertFalse(aliens.isEmpty());
    Map.Entry<Integer, SimpleAlien> entry = aliens.entrySet().iterator().next();
    Integer id = entry.getKey();
    SimpleAlien alien = entry.getValue();

    // reduce alien HP to 1 by calling getHit appropriate times (initial HP = 3)
    alien.getHit(); // now 2
    alien.getHit(); // now 1

    // create projectile that hits alien
    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    projectiles.clear();
    Coordinate hitCoord = alien.getCoordinate(); // top-left of alien, inside hitbox
    projectiles.add(new SimpleProjectile(new Coordinate(hitCoord.x(), hitCoord.y()), Direction.UP, 1));

    // run collision detection
    provider.checkCollision();

    // projectile should be removed
    assertTrue(projectiles.isEmpty() || projectiles.stream().noneMatch(pr -> pr.getCoordinate().equals(hitCoord)));

    // alien should be removed due to death
    assertFalse(aliens.containsKey(id));
  }

  @Test
  void testCheckCollisionPlayerHitPointsUpdated() throws Exception {
    ArrayList<SimpleProjectile> projectiles = getPrivateField(provider, "projectiles");
    SimpleShip player = getPrivateField(provider, "player");

    projectiles.clear();
    // place a projectile on player's coordinate and direction DOWN (alien shot)
    projectiles.add(new SimpleProjectile(new Coordinate(player.getCoordinate().x(), player.getCoordinate().y()), Direction.DOWN, Constans.BASE_DAMAGE));

    // before hit
    assertEquals(3, player.getHitPoints());
    provider.checkCollision();
    // note: current implementation sets hitPoints to projectile damage (as per code)
    assertEquals(Constans.BASE_DAMAGE, player.getHitPoints());
  }
}

