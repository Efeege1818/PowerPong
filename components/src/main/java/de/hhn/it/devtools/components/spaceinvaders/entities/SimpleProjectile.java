package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;

/**
 * Represents a moving SimpleProjectile in the SpaceInvader game.
 */
public class SimpleProjectile {
  private Direction direction;
  private Coordinate coordinate;
  private int damage;

  /**
   * Basic Constructor for SimpleProjectile.
   *
   * @param coordinate coordinate from the Projectile.
   */
  public SimpleProjectile(Coordinate coordinate, Direction direction, int damage) {
    this.direction = direction;
    this.coordinate = coordinate;
    this.damage = damage;
  }

  public int getDamage() { return damage; }
  public Coordinate getCoordinate() { return coordinate; }

  public Direction getdirection() { return direction; }

  /**
   * Moves the Projectile either UP or DOWN based on its direction.
   */
  public void move() {
    if (direction == Direction.DOWN) {
      this.coordinate = new Coordinate(coordinate.x(), coordinate.y() + 1);
    } else if (direction == Direction.UP) {
      this.coordinate = new Coordinate(coordinate.x(), coordinate.y() - 1);
    }
  }

}
