package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents an unmoving Barrier in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of a Barrier.
 * @param hitRadius  The Radius of the Hitbox surrounding the Barrier originating from the Center of the Barrier.
 * @param hitPoints  How much damage the Barrier can take before it is destroyed.
 */
public record Barrier(Coordinate coordinate, Integer hitRadius, Integer hitPoints) implements Entity {

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
