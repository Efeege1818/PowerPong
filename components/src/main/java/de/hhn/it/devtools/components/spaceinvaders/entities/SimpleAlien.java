package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;
import java.util.ArrayList;

/**
 * SimpleAlien contains all Information and can create an Immutable Record of all Information.
 */
public class SimpleAlien {
  private static int X = 10;
  Coordinate coordinate;
  Integer hitPoints;
  AlienType alienType;
  Integer alienId;
  ArrayList<Coordinate> hitbox;

  /**
   * Creates a SimpleAlien Object automatically creates a Hitbox for the Alien
   *
   * @param coordinate the top left Coordinate of the Alien.
   * @param alienType the Type of Alien.
   * @param alienId the ID to identify the Alien.
   */
  public SimpleAlien(Coordinate coordinate, AlienType alienType, Integer alienId) {
    this.coordinate = coordinate;
    hitbox = drawHitbox(X);
    this.alienType = alienType;
    hitPoints = 3;
    this.alienId = alienId;
  }

  /**
   * Draws the HitBox of the ALien based on the coordinate.
   * The Hitbox is a Square.
   *
   * @param x the length of the side of the Sqaure in Pixels.
   * @return a List containing all Coordinates of the Hitbox.
   */
  private ArrayList<Coordinate> drawHitbox(int x) {
    ArrayList<Coordinate> coords = new ArrayList<>();
    int j = 0;
    for (int i = 0; i < x; i++) {
      for (j = 0; j < x; j++) {
        coords.add(new Coordinate(coordinate.x() + i, coordinate.y() + j));
      }
    }
    return coords;
  }

  /**
   * Removes the amount of Damage from the HitPoints.
   *
   * @param damage the amount of Damage to take.
   * @return a boolean true if the alien is still alive after the hit.
   */
  public boolean getHit(Integer damage) {
    this.hitPoints = this.hitPoints - damage;
    return hitPoints > 0;
  }

  /**
   * Moves the Alien in a specific direction by changing the Coordinate of the Alien and redrawing the Hitbox.
   *
   * @param direction the direction in which you want to move the Alien.
   */
  public void move(Direction direction) {
    if (direction == Direction.RIGHT) {
      coordinate = new Coordinate(coordinate.x() + 1, coordinate.y());
      this.hitbox = drawHitbox(X);
    } else if (direction == Direction.LEFT) {
      coordinate = new Coordinate(coordinate.x() - 1, coordinate.y());
      this.hitbox = drawHitbox(X);
    } else if (direction == Direction.DOWN) {
      coordinate = new Coordinate(coordinate.x(), coordinate.y() + 1);
      this.hitbox = drawHitbox(X);
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
}
