package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents a moving SimpleProjectile in the SpaceInvader game.
 */
public class SimpleProjectile {

  private Coordinate coordinate;

  /**
   * Basic Constructor for SimpleProjectile.
   *
   * @param coordinate coordinate from the Projectile.
   */
  public SimpleProjectile(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

  public void setCoordinate(Coordinate coordinate) {
    this.coordinate = coordinate;
  }

}
