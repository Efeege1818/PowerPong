package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import java.util.ArrayList;

/**
 * Represents an unmoving SimpleBarrier in the SpaceInvader game.
 */
public class SimpleShip {

  private Coordinate coordinate;
  private ArrayList<Coordinate> hitbox;
  private int hitPoints;

  /**
   * Basic Constructor for SimpleBarrier.
   *
   * @param coordinate start coordinate top left.
   */
  public SimpleShip(Coordinate coordinate) {
    this.coordinate = coordinate;
    this.hitbox = fillHitBox(10, 10);
    this.hitPoints = 3;
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

  /**
   * Method to move Player Ship into direction.
   *
   * @param direction direction to move.
   */
  public void move(Direction direction) throws IllegalArgumentException {
    switch (direction) {
      case LEFT:
        this.coordinate = new Coordinate(coordinate.x() - 1, coordinate.y());
        this.hitbox = fillHitBox(10, 10);
        break;
      case RIGHT:
        this.coordinate = new Coordinate(coordinate.x() + 1, coordinate.y());
        this.hitbox = fillHitBox(10, 10);
        break;
      default:
        throw new IllegalArgumentException("Invalid direction");
    }
  }

  public void setHitPoints(int hitPoints) {
    this.hitPoints = hitPoints;
  }

  public ArrayList<Coordinate> getHitbox() {
    return hitbox;
  }

  public Ship getImmutableShip() {
    return new Ship(coordinate, hitPoints);
  }

}
