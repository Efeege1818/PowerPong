package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import java.util.ArrayList;

/**
 * SimpleAlien contains all Information and can create an Immutable Record of all Information.
 */
public class SimpleAlien {
  private Coordinate coordinate;
  private int hitPoints;
  private AlienType alienType;
  private int alienId;
  private ArrayList<Coordinate> hitbox;

  /**
   * Creates a SimpleAlien Object automatically creates a Hitbox for the Alien.
   *
   * @param coordinate the top left Coordinate of the Alien.
   * @param alienType the Type of Alien.
   * @param alienId the ID to identify the Alien.
   */
  public SimpleAlien(Coordinate coordinate, AlienType alienType, int alienId) {
    this.coordinate = coordinate;
    hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.HITBOX_SIZE,
            APIConstants.HITBOX_SIZE);
    this.alienType = alienType;
    hitPoints = 3;
    this.alienId = alienId;
  }

  /**
   * Removes the amount of Damage from the HitPoints.
   *
   * @return a boolean true if the alien is still alive after the hit.
   */
  public boolean getHit() {
    this.hitPoints = this.hitPoints - 1;
    return hitPoints > 0;
  }

  /**
   * Moves the Alien in a specific direction by changing the Coordinate of the Alien and redrawing
   * the Hitbox.
   *
   * @param direction the direction in which you want to move the Alien.
   */
  public void move(Direction direction) {
    if (direction == Direction.RIGHT) {
      coordinate = new Coordinate(coordinate.x() + 2, coordinate.y());
      this.hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    } else if (direction == Direction.LEFT) {
      coordinate = new Coordinate(coordinate.x() - 2, coordinate.y());
      this.hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    } else if (direction == Direction.DOWN) {
      coordinate = new Coordinate(coordinate.x(), coordinate.y() + 5);
      this.hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    }
  }

  /**
   * A getter which creates an Immutable Alien and returns it.
   *
   * @return the Alien Record containing all the Aliens information.
   */
  public Alien immutableAlien() {
    return new Alien(coordinate, hitPoints, alienType, alienId);
  }

  public ArrayList<Coordinate> getHitbox() { return hitbox; }

  public Coordinate getCoordinate() { return coordinate; }
}
