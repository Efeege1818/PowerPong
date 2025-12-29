package de.hhn.it.devtools.components.spaceinvaders.utils;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleBarrier;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * EntityProvider for all Entity Utils.
 */
public class EntityProvider {

  private SimpleShip player;
  private HashMap<Integer, SimpleBarrier> barriers = new HashMap<>();
  private HashMap<Integer, SimpleAlien> aliens = new HashMap<>();
  private ArrayList<SimpleProjectile> projectiles = new ArrayList<>();
  private Direction currentAlienDirection = Direction.RIGHT;
  private SimpleSpaceInvadersService service;
  /**
   * Default Constructor.
   */
  public EntityProvider(SimpleSpaceInvadersService service) {
    player = new SimpleShip(new Coordinate(APIConstants.FIELD_SIZE / 2, APIConstants.FIELD_SIZE - 26));
    generateAliens();
    this.service = service;
  }

  public SimpleShip getPlayer() {
    return player;
  }

  public HashMap<Integer, SimpleAlien> getAliens() {
    return aliens;
  }

  /**
   * moves all the aliens.
   */
  public void updateAliens() {
    if (aliens.isEmpty()) {
      return;
    }
    for (SimpleAlien a : aliens.values()) {
      if (a.getCoordinate().x() < 0 || a.getCoordinate().x() > APIConstants.FIELD_SIZE - 15) {
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

  /**
   * moves all the projectiles.
   */
  public void updateProjectiles() {
    projectiles.forEach(SimpleProjectile::move);
  }

  /**
   * shoots from player.
   */
  public void shootPlayer() {
    projectiles.add(new SimpleProjectile(new Coordinate(player.getCoordinate().x() + 1,
            player.getCoordinate().y() - 1), Direction.UP, Constants.BASE_DAMAGE));
  }

  /**
   * Selects a random alien and shoots from its Position.
   */
  public void shootAliens() {
    if (aliens.isEmpty()) {
      return;
    }

    Random rand = new Random();
    List<Integer> keys = new ArrayList<>(aliens.keySet());
    Integer randomKey = keys.get(rand.nextInt(keys.size()));
    SimpleAlien randomAlien = aliens.get(randomKey);
    projectiles.add(new SimpleProjectile(new Coordinate(randomAlien.getCoordinate().x() + 1,
            randomAlien.getCoordinate().y() + 1), Direction.DOWN, Constants.BASE_DAMAGE));
  }

  /**
   * Method to generate Aliens on the field.
   */
  public void generateAliens() {
    int row = 1;
    int col = 1;
    for (int i = 1; i <= Constants.NUMBER_OF_ALIENS; i++) {
      aliens.put(i, new SimpleAlien(new Coordinate(col * 25, 25 * row), AlienType.BASIC, i));
      col++;
      if (i % 10 == 0) {
        row++;
        col = 1;
      }
    }
  }

  /**
   * Method to check for collisions.
   */
  public void checkCollision() {
    if (aliens.isEmpty()) {
      return;
    }
    // check if alien either hits barrier or the player
    List<SimpleAlien> toRemoveAliens = new ArrayList<>();
    List<SimpleProjectile> toRemoveProjectile = new ArrayList<>();
    aliens.values().forEach(alien -> {
      java.util.Optional<SimpleBarrier> barrier = barriers.values().stream().filter(barriers -> alien.getHitbox().stream().anyMatch(barriers.getHitbox()::contains)).findFirst();

      if (barrier.isPresent()) {
        service.notifyListeners(spaceInvadersListener -> spaceInvadersListener
                .updateBarrier(barrier.get().getImmutableBarrier()));
        toRemoveAliens.add(alien);
      }
      if (alien.getCoordinate().y() >= player.getCoordinate().y()) {
        player.setHitPoints(0);
        service.notifyListeners(spaceInvadersListener -> spaceInvadersListener.updateShip(player.getImmutableShip()));
      }
    });

    // check if projectiles hit alien or player
    for (SimpleProjectile projectile : projectiles) {
      if (projectile.getdirection() == Direction.UP) { // player can only shoot up, so all projectiles moving up are from player
        aliens.values().forEach(alien -> {
          if (alien.getHitbox().contains(projectile.getCoordinate())) {
            toRemoveProjectile.add(projectile);
            if (!alien.getHit()) {
              toRemoveAliens.add(alien);
              service.notifyListeners(spaceInvadersListener -> spaceInvadersListener.updateScore(service.score += Constants.ALIEN_DEATH_POINTS));
            }
            service.notifyListeners(spaceInvadersListener -> spaceInvadersListener.damageAlien(alien.immutableAlien()));
          }
        });
      } else { // aliens can only shoot down, so all projectiles moving down are from aliens
        if (player.getHitbox().contains(projectile.getCoordinate())) {
          service.notifyListeners(spaceInvadersListener -> spaceInvadersListener
                  .updateShip(player.getImmutableShip()));
          player.setHitPoints(projectile.getDamage());
        }
      }
    }
    projectiles.removeAll(toRemoveProjectile);
    toRemoveProjectile.clear();
    toRemoveAliens.forEach(aliens.values()::remove);
    toRemoveAliens.clear();
  }


  /**
   * Method to Calculate Entity hitbox.
   *
   * @param x coordinate to calculate hitbox.
   * @param y coordinate to calculate hitbox
   * @return hitbox ArrayList.
   */
  public static ArrayList<Coordinate> fillHitBox(Coordinate coordinate, int x, int y) {
    if (coordinate.x() < 10 || coordinate.y() < 0 || coordinate.x() > APIConstants.FIELD_SIZE - 15
            || coordinate.y() > APIConstants.FIELD_SIZE) {
      return new ArrayList<>();
    }
    ArrayList<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        coords.add(new Coordinate(coordinate.x() + i, coordinate.y() + j));
      }
    }
    return coords;
  }

}
