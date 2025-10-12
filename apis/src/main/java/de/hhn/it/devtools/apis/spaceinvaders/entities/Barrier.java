package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents an unmoving Barrier in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of a Barrier.
 * @param hitPoints  How much damage the Barrier can take before it is destroyed.
 */
public record Barrier(Coordinate coordinate, Integer hitPoints) {

  @Override
  public Integer hitPoints() {
    return hitPoints;
  }

  @Override
  public Coordinate coordinate() {
    return coordinate;
  }
}
