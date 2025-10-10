package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents an Alien Enemy in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of an Alien.
 * @param hitRadius  The Radius of the Hitbox surrounding the Alien originating from the Center of the Alien.
 * @param hitPoints  How much damage the Alien can take before it is destroyed.
 */
public record Alien(Coordinate coordinate, Integer hitRadius, Integer hitPoints) implements Entity {

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
