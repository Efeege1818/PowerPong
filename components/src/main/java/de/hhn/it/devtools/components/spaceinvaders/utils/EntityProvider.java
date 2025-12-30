package de.hhn.it.devtools.components.spaceinvaders.utils;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleBarrier;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;

import java.util.*;

/**
 * EntityProvider for all Entity Utils.
 */
public class EntityProvider {

  // spatial hash grid for barriers
  private final HashMap<Long, List<SimpleBarrier>> barrierGrid = new HashMap<>();

  private long cellKey(int cx, int cy) {
    return (((long) cx) << 32) | (cy & 0xffffffffL);
  }

  private long cellKey(Coordinate c) {
    return cellKey(c.x() / Constants.GRID_CELL_SIZE, c.y() / Constants.GRID_CELL_SIZE);
  }

  private List<SimpleBarrier> getNearbyBarriers(Coordinate c) {
    List<SimpleBarrier> result = new ArrayList<>();
    int cx = c.x() / Constants.GRID_CELL_SIZE;
    int cy = c.y() / Constants.GRID_CELL_SIZE;

    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        List<SimpleBarrier> cell = barrierGrid.get(cellKey(cx + dx, cy + dy));
        if (cell != null) {
          result.addAll(cell);
        }
      }
    }
    return result;
  }

  private final SimpleShip player;
  private final HashMap<Integer, SimpleBarrier> barriers = new HashMap<>();
  private final HashMap<Integer, SimpleAlien> aliens = new HashMap<>();
  private final ArrayList<SimpleProjectile> projectiles = new ArrayList<>();

  private Direction currentAlienDirection = Direction.RIGHT;
  private final SimpleSpaceInvadersService service;

  public EntityProvider(SimpleSpaceInvadersService service) {
    this.service = service;
    this.player = new SimpleShip(
            new Coordinate(APIConstants.FIELD_SIZE / 2, APIConstants.FIELD_SIZE - 26)
    );
    generateAliens();
    initBarriers();
  }

  public void generateAliens() {
    int row = 1;
    int col = 1;

    for (int i = 1; i <= Constants.NUMBER_OF_ALIENS; i++) {
      aliens.put(i, new SimpleAlien(
              new Coordinate(col * 25, 25 * row),
              AlienType.BASIC,
              i
      ));

      col++;
      if (i % 10 == 0) {
        row++;
        col = 1;
      }
    }
  }

  public static ArrayList<Coordinate> fillHitBox(Coordinate coordinate, int width, int height) {
    if (coordinate.x() < 0 || coordinate.y() < 0
            || coordinate.x() > APIConstants.FIELD_SIZE
            || coordinate.y() > APIConstants.FIELD_SIZE) {
      return new ArrayList<>();
    }

    ArrayList<Coordinate> coords = new ArrayList<>();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        coords.add(new Coordinate(coordinate.x() + x, coordinate.y() + y));
      }
    }
    return coords;
  }

  private void initBarriers() {
    int numberOfBarriers = 3;
    int spacing = APIConstants.FIELD_SIZE / (numberOfBarriers + 1);
    int id = 0;

    for (int i = 1; i <= numberOfBarriers; i++) {
      for (int y = 0; y < APIConstants.BARRIER_HITBOX_HEIGHT; y++) {
        for (int x = 0; x < APIConstants.BARRIER_HITBOX_WIDTH; x++) {
          Coordinate c = new Coordinate(
                  x + (i * spacing),
                  APIConstants.FIELD_SIZE - 60 + y
          );

          SimpleBarrier barrier = new SimpleBarrier(c, id);
          barriers.put(id, barrier);

          barrierGrid
                  .computeIfAbsent(cellKey(c), k -> new ArrayList<>())
                  .add(barrier);

          id++;
        }
      }
    }
  }

  public void updateAliens() {
    if (aliens.isEmpty()) {
      return;
    }
    for (SimpleAlien a : aliens.values()) {
      if (a.getCoordinate().x() < 0
              || a.getCoordinate().x() > APIConstants.FIELD_SIZE - APIConstants.HITBOX_SIZE) {
        aliens.values().forEach(alien -> alien.move(Direction.DOWN));
        if (this.currentAlienDirection == Direction.RIGHT) {
          this.currentAlienDirection = Direction.LEFT;
        } else {
          this.currentAlienDirection = Direction.RIGHT;
        }

        service.notifyListeners(spaceInvadersListener -> spaceInvadersListener
                .updateAliens(aliens.values().stream().map(SimpleAlien::immutableAlien).toArray(Alien[]::new)));
        break;
      }
    }
    aliens.values().forEach(alien -> alien.move(currentAlienDirection));
    service.notifyListeners(spaceInvadersListener -> spaceInvadersListener
            .updateAliens(aliens.values().stream().map(SimpleAlien::immutableAlien).toArray(Alien[]::new)));
    service.notifyListeners((s) -> s.updateShip(player.getImmutableShip()));
  }

  public void updateProjectiles() {
    projectiles.forEach(SimpleProjectile::move);
    service.notifyListeners(l ->
            l.updateProjectiles(projectiles.stream()
                    .map(SimpleProjectile::getImmtProjectile)
                    .toArray(Projectile[]::new)));
  }

  public void shootPlayer() {
    projectiles.add(new SimpleProjectile(
            new Coordinate(player.getCoordinate().x() + 1, player.getCoordinate().y() - 1),
            Direction.UP,
            Constants.BASE_DAMAGE
    ));
  }

  public void shootAliens() {
    if (aliens.isEmpty()) return;

    if (new Random().nextInt(100) <= Constants.ALIEN_SHOOTING_CHANCE) {
      List<Integer> keys = new ArrayList<>(aliens.keySet());
      SimpleAlien a = aliens.get(keys.get(new Random().nextInt(keys.size())));
      projectiles.add(new SimpleProjectile(
              new Coordinate(a.getCoordinate().x() + 1, a.getCoordinate().y() + 1),
              Direction.DOWN,
              Constants.BASE_DAMAGE
      ));
    }
  }

  public void checkCollision() {

    List<SimpleAlien> toRemoveAliens = new ArrayList<>();
    List<SimpleProjectile> toRemoveProjectiles = new ArrayList<>();
    List<SimpleBarrier> toRemoveBarriers = new ArrayList<>();

    if (aliens.isEmpty()) {
      return;
    }

    for (SimpleAlien alien : aliens.values()) {

      for (SimpleBarrier barrier : getNearbyBarriers(alien.getCoordinate())) {
        if (alien.getHitbox().stream().anyMatch(barrier.getHitbox()::contains)) {
          toRemoveAliens.add(alien);
          toRemoveBarriers.add(barrier);
          break;
        }
      }

      if (alien.getCoordinate().y() >= player.getCoordinate().y()) {
        player.setHitPoints(0);
        service.notifyListeners(l -> l.updateShip(player.getImmutableShip()));
      }
    }

    for (SimpleProjectile p : projectiles) {

      if (p.getdirection() == Direction.DOWN) {

        if (player.getHitbox().contains(p.getCoordinate())) {
          player.setHitPoints(p.getDamage());
          service.notifyListeners(l ->
                  l.updateShip(player.getImmutableShip()));
          toRemoveProjectiles.add(p);
          continue;
        }

        for (SimpleBarrier barrier : getNearbyBarriers(p.getCoordinate())) {
          if (barrier.getHitbox().contains(p.getCoordinate())) {
            toRemoveProjectiles.add(p);
            toRemoveBarriers.add(barrier);
            break;
          }
        }
        continue;
      }

      if (p.getdirection() == Direction.UP) {
        for (SimpleAlien alien : aliens.values()) {
          if (alien.getHitbox().contains(p.getCoordinate())) {
            toRemoveProjectiles.add(p);
            if (!alien.getHit()) {
              toRemoveAliens.add(alien);
              service.notifyListeners(l ->
                      l.updateScore(service.score += Constants.ALIEN_DEATH_POINTS));
            }
            service.notifyListeners(l ->
                    l.damageAlien(alien.immutableAlien()));
            break;
          }
        }
      }

      if (p.getCoordinate().y() < 0 || p.getCoordinate().y() > APIConstants.FIELD_SIZE) {
        toRemoveProjectiles.add(p);
      }
    }

    projectiles.removeAll(toRemoveProjectiles);
    toRemoveAliens.forEach(aliens.values()::remove);

    for (SimpleBarrier barrier : toRemoveBarriers) {
      barriers.values().remove(barrier);
      long key = cellKey(barrier.getCoordinate());
      List<SimpleBarrier> cell = barrierGrid.get(key);
      if (cell != null) {
        cell.remove(barrier);
      }
      service.notifyListeners(l ->
              l.updateBarrier(barrier.getImmutableBarrier()));
    }

    service.notifyListeners(l ->
            l.updateProjectiles(projectiles.stream()
                    .map(SimpleProjectile::getImmtProjectile)
                    .toArray(Projectile[]::new)));

    service.notifyListeners(l ->
            l.updateAliens(aliens.values().stream()
                    .map(SimpleAlien::immutableAlien)
                    .toArray(Alien[]::new)));
  }

  public SimpleShip getPlayer() {
    return player;
  }

  public HashMap<Integer, SimpleAlien> getAliens() {
    return aliens;
  }

  public HashMap<Integer, SimpleBarrier> getBarriers() {
    return barriers;
  }
}
