package de.hhn.it.devtools.apis.spaceinvaders.entities;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Ship Represents the Player Character in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of the Ship.
 * @param hitRadius  The Radius of the Hitbox surrounding the Ship originating from the Center of the Ship.
 * @param hitPoints  How much damage the Ship can take before it is destroyed.
 */
public record Ship(Coordinate coordinate, Integer hitRadius, Integer hitPoints) implements Entity {

  @Override
  public Integer hitPoints() {
    return hitPoints;
  }

  @Override
  public Integer hitRadius() {
    return hitRadius;
  }

  @Override
  public Coordinate coordinate() {
    return coordinate;
  }
}
