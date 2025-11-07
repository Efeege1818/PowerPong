package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents an unmoving Barrier in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of a Barrier.
 * @param barrierId  ID for identification purposes.
 */
public record Barrier(Coordinate coordinate, Integer barrierId) {


  @Override
  public Coordinate coordinate() {
    return coordinate;
  }
}
