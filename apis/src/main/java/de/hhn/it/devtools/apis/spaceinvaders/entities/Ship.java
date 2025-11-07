package de.hhn.it.devtools.apis.spaceinvaders.entities;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Ship Represents the Player Character in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of the Ship.
 * @param hitPoints  How much damage the Ship can take before it is destroyed.
 * @param shipId  ID for identification purposes.
 */
public record Ship(Coordinate coordinate, Integer hitPoints, Integer shipId) {

  @Override
  public Integer hitPoints() {
    return hitPoints;
  }

  @Override
  public Coordinate coordinate() {
    return coordinate;
  }
}
