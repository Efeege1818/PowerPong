package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import java.util.ArrayList;

/**
 * Represents a moving SimpleShip in the SpaceInvader game.
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
    this.hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.HITBOX_SIZE, APIConstants.HITBOX_SIZE);
    this.hitPoints = 3;
  }

  /**
   * Method to move Player Ship into direction.
   *
   * @param direction direction to move.
   */
  public void move(Direction direction) throws IllegalArgumentException {
    switch (direction) {
      case LEFT:
        if (!(coordinate.x() <= APIConstants.HITBOX_SIZE)) {
          this.coordinate = new Coordinate(coordinate.x() - 2, coordinate.y());
          this.hitbox = EntityProvider.fillHitBox(coordinate, 10, 10);
        }
        break;
      case RIGHT:
        if (!(coordinate.x() >= APIConstants.FIELD_SIZE - APIConstants.HITBOX_SIZE)) {
          this.coordinate = new Coordinate(coordinate.x() + 2, coordinate.y());
          this.hitbox = EntityProvider.fillHitBox(coordinate, 10, 10);
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid direction");
    }
  }

  public Coordinate getCoordinate() { return coordinate; }

  public int getHitPoints() { return this.hitPoints; }

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
