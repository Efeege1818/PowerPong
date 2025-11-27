package de.hhn.it.devtools.components.spaceinvaders.utils;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleAlien;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleBarrier;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleProjectile;
import de.hhn.it.devtools.components.spaceinvaders.entities.SimpleShip;
import de.hhn.it.devtools.components.spaceinvaders.utils.Constans;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * EntityProvider for all Entity Utils.
 */
public class EntityProvider {

  private SimpleShip player;
  private HashMap<Integer, SimpleBarrier> barriers = new HashMap<>();
  private HashMap<Integer, SimpleAlien> aliens = new HashMap<>();
  private ArrayList<SimpleProjectile> projectiles = new ArrayList<>();

  /**
   * Default Constructor.
   */
  public EntityProvider() {
    player = new SimpleShip(new Coordinate(Constans.FIELD_SIZE/2,Constans.FIELD_SIZE - 10));
    generateAliens();
  }

  /**
   * Method to generate Aliens on the field.
   */
  private void generateAliens() {
    int row = 1;
    int col = 1;
    for(int i = 1; i <= Constans.NUMBER_OF_ALIENS; i++){
      if(i % 10 == 0) {
        row++;
        col = 1;
      }
      aliens.put(i, new SimpleAlien(new Coordinate(col*10, 10*row), AlienType.BASIC, i));
      col++;
    }
  }

  /**
   * Method to check for collisions.
   */
  private void checkCollision() {
    for (SimpleProjectile projectile : projectiles) {
      if(projectile.getdirection() == Direction.UP) {
        aliens.values().forEach(alien -> {
          if(alien.getHitbox().contains(projectile.getCoordinate())) {
            alien.getHit(Constans.BASE_DAMAGE);
          }
        });
      }
      else if(projectile.getdirection() == Direction.DOWN) {
        if(player.getHitbox().contains(projectile.getCoordinate())) {
          player.setHitPoints(player.getHitPoints()-1);
        }
      }
    }

    barriers.values().forEach(barrier -> {
      aliens.values().forEach(alien -> {
        alien.getHitbox().stream()
            .filter(barrier.getHitbox()::contains)
            .findFirst()
            .ifPresent(hit->alien.getHit(100));
      });
    });

    aliens.values().forEach(alien -> {
      if(alien.getCoordinate().y() >= player.getCoordinate().y()) {
        player.setHitPoints(0);
      }
    });
  }

  /**
   * Method to Calculate Entity hitbox.
   *
   * @param x coordinate to calculate hitbox.
   * @param y coordinate to calculate hitbox
   * @return hitbox ArrayList.
   */
  public static ArrayList<Coordinate> fillHitBox(Coordinate coordinate, int x, int y) {
    ArrayList<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        coords.add(new Coordinate(coordinate.x() + i, coordinate.y() + j));
      }
    }
    return coords;
  }

}
