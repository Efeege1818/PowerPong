package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import de.hhn.it.devtools.components.spaceinvaders.utils.Constants;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityProviderGoodCaseTest {

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
    Coordinate c = new Coordinate(12, 20);
    var hitbox = EntityProvider.fillHitBox(c, 2, 3);
    // 2 * 3 = 6 coordinates
    assertEquals(6, hitbox.size());
    assertTrue(hitbox.contains(new Coordinate(13, 22)));
    assertTrue(hitbox.contains(new Coordinate(13, 21)));
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
    assertEquals(Constants.BASE_DAMAGE, p.getDamage());
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
    assertTrue(p.getCoordinate().y() >= 0 && p.getCoordinate().y() <= APIConstants.FIELD_SIZE + 10);
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
    projectiles.add(new SimpleProjectile(new Coordinate(player.getCoordinate().x(), player.getCoordinate().y()), Direction.DOWN, Constants.BASE_DAMAGE));

    // before hit
    assertEquals(3, player.getHitPoints());
    provider.checkCollision();
    // note: current implementation sets hitPoints to projectile damage (as per code)
    assertEquals(Constants.BASE_DAMAGE, player.getHitPoints());
  }

  @Test
  void testAliensMoveDownAtEdge() throws Exception {
    SimpleSpaceInvadersService svc = new SimpleSpaceInvadersService();
    EntityProvider provider = new EntityProvider(svc);

    // create 3 aliens in a horizontal line near the right edge; one is beyond the field to trigger edge behavior
    int fs = APIConstants.FIELD_SIZE;
    SimpleAlien a1 = new SimpleAlien(new Coordinate(fs + 1, 10), AlienType.BASIC, 1); // triggers > FIELD_SIZE
    SimpleAlien a2 = new SimpleAlien(new Coordinate(fs - 1, 10), AlienType.BASIC, 2);
    SimpleAlien a3 = new SimpleAlien(new Coordinate(fs - 2, 10), AlienType.BASIC, 3);

    HashMap<Integer, SimpleAlien> customAliens = new HashMap<>();
    customAliens.put(1, a1);
    customAliens.put(2, a2);
    customAliens.put(3, a3);

    // replace private aliens map with our custom map
    Field aliensField = provider.getClass().getDeclaredField("aliens");
    aliensField.setAccessible(true);
    aliensField.set(provider, customAliens);

    // ensure current direction is RIGHT before update
    Field dirField = provider.getClass().getDeclaredField("currentAlienDirection");
    dirField.setAccessible(true);
    dirField.set(provider, Direction.RIGHT);

    // record positions before
    Map<Integer, Coordinate> before = new HashMap<>();
    customAliens.forEach((k, v) -> before.put(k, v.getCoordinate()));

    // call updateAliens -> should move all aliens DOWN once and then in the new horizontal direction
    provider.updateAliens();

    // after positions
    Map<Integer, Coordinate> after = new HashMap<>();
    ((HashMap<Integer, SimpleAlien>) provider.getAliens()).forEach((k, v) -> after.put(k, v.getCoordinate()));

    // after update: y should have increased by 1 (moved down);
    // since initial direction was RIGHT, it should flip to LEFT and then move LEFT by 1
    for (Integer id : before.keySet()) {
      Coordinate b = before.get(id);
      Coordinate a = after.get(id);
      assertNotNull(a, "Alien should still exist after update");
      assertEquals(b.y() + 1, a.y(), "Alien should have moved down by 1 at edge");
      assertEquals(b.x() - 1, a.x(), "Alien should have moved horizontally left by 1 after direction flip");
    }

    // check that the provider's direction was flipped to LEFT
    Direction newDir = (Direction) dirField.get(provider);
    assertEquals(Direction.LEFT, newDir, "Alien direction should have been flipped to LEFT after hitting edge");
  }
}

