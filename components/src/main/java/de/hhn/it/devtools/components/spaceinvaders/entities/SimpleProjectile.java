package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import java.util.ArrayList;

/**
 * Represents a moving SimpleProjectile in the SpaceInvader game.
 */
public class SimpleProjectile {
  private final Direction direction;
  private Coordinate coordinate;
  private final int damage;
  private static int projectileID = 0;
  private int id;
  private ArrayList<Coordinate> hitbox;

  /**
   * Basic Constructor for SimpleProjectile.
   *
   * @param coordinate coordinate from the Projectile.
   */
  public SimpleProjectile(Coordinate coordinate, Direction direction, int damage) {
    this.direction = direction;
    this.coordinate = coordinate;
    this.damage = damage;
    this.id = projectileID++;
    hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.SHOT_HITBOX_SIZE,
            APIConstants.SHOT_HITBOX_SIZE);
  }

  public int getDamage() {
    return damage;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public Direction getdirection() {
    return direction;
  }

  /**
   * Moves the Projectile either UP or DOWN based on its direction.
   */
  public void move() {
    if (direction == Direction.DOWN) {
      this.coordinate = new Coordinate(coordinate.x(), coordinate.y() + 3);
    } else if (direction == Direction.UP) {
      this.coordinate = new Coordinate(coordinate.x(), coordinate.y() - 3);
    }
    this.hitbox = EntityProvider.fillHitBox(this.coordinate, APIConstants.SHOT_HITBOX_SIZE,
            APIConstants.SHOT_HITBOX_SIZE);
  }

  /**
   * Sets id to minus so fx knows when to remove.
   */
  public void inverse() {
    id = -id;
  }

  public Projectile getImmtProjectile() {
    return new Projectile(this.coordinate, this.id, this.direction);
  }

  public ArrayList<Coordinate> getHitbox() {
    return hitbox;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SimpleProjectile that = (SimpleProjectile) o;
    return this.coordinate.equals(that.coordinate) && this.direction == that.direction;
  }

  @Override
  public int hashCode() {
    int result = coordinate != null ? coordinate.hashCode() : 0;
    result = 31 * result + (direction != null ? direction.hashCode() : 0);
    return result;
  }
}
