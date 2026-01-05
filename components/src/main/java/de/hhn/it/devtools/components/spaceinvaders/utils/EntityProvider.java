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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles all entity-related logic for the Space Invaders game.
 * This includes creation, movement, collision detection, and removal of
 * aliens, player, projectiles, and barriers. A spatial hash grid is used
 * for efficient collision detection.
 */
public class EntityProvider {

  /**
   * Spatial hash grid storing barriers by grid cell.
   * Used to efficiently query nearby barriers.
   */
  public HashMap<Long, List<SimpleBarrier>> barrierGrid = new HashMap<>();

  /**
   * Creates a unique 64-bit key for a grid cell using its x/y indices.
   *
   * @param cx cell x-coordinate
   * @param cy cell y-coordinate
   * @return unique long key representing the grid cell
   */
  private long cellKey(int cx, int cy) {
    return (((long) cx) << 32) | (cy & 0xffffffffL);
  }

  /**
   * Computes the grid cell key for a given world coordinate.
   *
   * @param c world coordinate
   * @return grid cell key
   */
  public long cellKey(Coordinate c) {
    return cellKey(c.x() / Constants.GRID_CELL_SIZE,
            c.y() / Constants.GRID_CELL_SIZE);
  }

  /**
   * Returns all barriers that are located in the same or neighboring grid
   * cells as the given coordinate.
   *
   * @param c coordinate to query around
   * @return list of nearby barriers
   */
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
  private final HashMap<Integer, SimpleAlien> aliens = new HashMap<>();
  private final CopyOnWriteArrayList<SimpleProjectile> projectiles = new CopyOnWriteArrayList<>();
  private Direction currentAlienDirection = Direction.RIGHT;
  private final SimpleSpaceInvadersService service;

  /**
   * Creates a new EntityProvider and initializes all entities.
   *
   * @param service the game service used to notify listeners
   */
  public EntityProvider(SimpleSpaceInvadersService service) {
    this.service = service;
    this.player = new SimpleShip(
            new Coordinate(APIConstants.FIELD_SIZE / 2, APIConstants.FIELD_SIZE - 26)
    );
    generateAliens();
    initBarriers();
  }

  /**
   * Creates all alien entities and positions them in rows.
   */
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

  /**
   * Calculates a rectangular hitbox for an entity.
   *
   * @param coordinate top-left coordinate
   * @param width      hitbox width
   * @param height     hitbox height
   * @return list of coordinates representing the hitbox
   */
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

  /**
   * Initializes all barrier blocks and places them into the spatial grid.
   */
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

          barrierGrid
                  .computeIfAbsent(cellKey(c), k -> new ArrayList<>())
                  .add(barrier);

          id++;
        }
      }
    }
  }

  /**
   * Updates alien movement and handles boundary collisions.
   */
  public void updateAliens() {
    if (aliens.isEmpty()) {
      return;
    }

    for (SimpleAlien alien : aliens.values()) {
      if (alien.getCoordinate().x() < 0
              || alien.getCoordinate().x() > APIConstants.FIELD_SIZE - APIConstants.HITBOX_SIZE) {
        aliens.values().forEach(a -> a.move(Direction.DOWN));
        currentAlienDirection =
                (currentAlienDirection == Direction.RIGHT)
                        ? Direction.LEFT
                        : Direction.RIGHT;
        break;
      }
    }

    aliens.values().forEach(a -> a.move(currentAlienDirection));
    service.notifyListeners(l ->
            l.updateAliens(aliens.values().stream()
                    .map(SimpleAlien::immutableAlien)
                    .toArray(Alien[]::new)));
  }

  /**
   * Moves all active projectiles.
   */
  public void updateProjectiles() {
    projectiles.forEach(SimpleProjectile::move);
    service.notifyListeners(l ->
            l.updateProjectiles(projectiles.stream()
                    .map(SimpleProjectile::getImmtProjectile)
                    .toArray(Projectile[]::new)));
  }

  /**
   * Fires a projectile from the player's ship.
   */
  public void shootPlayer() {
    int calledFromX = player.getCoordinate().x() + (APIConstants.PLAYER_SIZE / 2)
            - (APIConstants.SHOT_HITBOX_SIZE / 2);
    projectiles.add(new SimpleProjectile(
            new Coordinate(calledFromX, player.getCoordinate().y()),
            Direction.UP,
            Constants.BASE_DAMAGE
    ));
  }

  /**
   * Randomly selects an alien and fires a projectile downward.
   */
  public void shootAliens() {
    if (aliens.isEmpty()) {
      return;
    }

    if (new Random().nextInt(1000) <= Constants.ALIEN_SHOOTING_CHANCE) {
      List<Integer> keys = new ArrayList<>(aliens.keySet());
      SimpleAlien a = aliens.get(keys.get(new Random().nextInt(keys.size())));
      projectiles.add(new SimpleProjectile(
              new Coordinate(a.getCoordinate().x() + 1, a.getCoordinate().y() + 1),
              Direction.DOWN,
              Constants.BASE_DAMAGE
      ));
    }
  }

  /**
   * Handles all collision detection and resolution.
   */
  public void checkCollision() {
    Set<SimpleAlien> toRemoveAliens = new HashSet<>();
    Set<SimpleBarrier> toRemoveBarriers = new HashSet<>();
    Set<SimpleProjectile> toRemoveProjectiles = new HashSet<>();
    if (aliens.isEmpty()) {
      return;
    }
    for (SimpleAlien alien : aliens.values()) {
      for (SimpleBarrier barrier : getNearbyBarriers(alien.getCoordinate())) {
        if (alien.getHitbox().contains(barrier.getCoordinate())) {
          alien.setHitPoints(0);
          toRemoveAliens.add(alien);
          toRemoveBarriers.add(barrier);
        }
      }
      if (alien.getCoordinate().y() >= player.getCoordinate().y() - (APIConstants.PLAYER_SIZE / 2)
              - (APIConstants.SHOT_HITBOX_SIZE / 2)) {
        player.setHitPoints(0);
        service.notifyListeners(l -> l.updateShip(player.getImmutableShip()));
      }
    }
    for (SimpleProjectile p : projectiles) {
      if (p.getdirection() == Direction.DOWN) {
        if (!Collections.disjoint(player.getHitbox(), p.getHitbox())) {

          player.setHitPoints(player.getHitPoints() - p.getDamage());
          service.notifyListeners(l -> l.updateShip(player.getImmutableShip()));
          toRemoveProjectiles.add(p);
        }

        for (SimpleBarrier barrier : getNearbyBarriers(p.getCoordinate())) {
          if (p.getHitbox().contains(barrier.getCoordinate())) {
            toRemoveBarriers.add(barrier);
            toRemoveProjectiles.add(p);
          }
        }
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
            break;
          }
        }
      }
      if (p.getCoordinate().y() < 0 || p.getCoordinate().y() > APIConstants.FIELD_SIZE) {
        toRemoveProjectiles.add(p);
      }
    }

    for (SimpleProjectile p : toRemoveProjectiles) {
      p.inverse();
    }
    service.notifyListeners(l ->
            l.updateProjectiles(toRemoveProjectiles.stream()
                    .map(SimpleProjectile::getImmtProjectile).toArray(Projectile[]::new)));
    projectiles.removeAll(toRemoveProjectiles);

    for (SimpleBarrier barrier : toRemoveBarriers) {
      long key = cellKey(barrier.getCoordinate());
      List<SimpleBarrier> cell = barrierGrid.get(key);
      if (cell != null) {
        cell.remove(barrier);
      }
      service.notifyListeners(l -> l.updateBarrier(barrier.getImmutableBarrier()));
    }

    toRemoveAliens.forEach(alien -> {
      alien.setHitPoints(0);
      aliens.remove(alien.getAlienId());
      service.notifyListeners(l -> l.damageAlien(alien.immutableAlien()));
    });

  }


  public SimpleShip getPlayer() {
    return player;
  }

  public HashMap<Integer, SimpleAlien> getAliens() {
    return aliens;
  }

  public HashMap<Integer, SimpleBarrier> getBarriers() {
    HashMap<Integer, SimpleBarrier> barriers = new HashMap<>();
    barrierGrid.values().forEach(list -> list.forEach(barrier ->
            barriers.put(barrier.getId(), barrier)));
    return barriers;
  }
}