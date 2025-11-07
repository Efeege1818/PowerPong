package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import java.util.ArrayList;

/**
 * Represents an unmoving SimpleBarrier in the SpaceInvader game.
 */
public class SimpleBarrier {

  private final Coordinate coordinate;
  private final ArrayList<Coordinate> hitbox;
  private final int id;

  /**
   * Basic Constructor for SimpleBarrier.
   *
   * @param coordinate start coordinate top left.
   * @param id id for identification.
   */
  public SimpleBarrier(Coordinate coordinate, int id) {
    this.coordinate = coordinate;
    this.hitbox = fillHitBox(20, 10);
    this.id = id;
  }

  ArrayList<Coordinate> fillHitBox(int x, int y) {
    ArrayList<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        coords.add(new Coordinate(coordinate.x() + i, coordinate.y() + j));
      }
    }
    return coords;
  }

  public ArrayList<Coordinate> getHitbox() {
    return hitbox;
  }

  public Barrier getImmutableBarrier() {
    return new Barrier(this.coordinate, id);
  }

}
