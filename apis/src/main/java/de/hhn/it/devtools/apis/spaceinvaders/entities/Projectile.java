package de.hhn.it.devtools.apis.spaceinvaders.entities;


import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents the Projectiles in the SpaceInvaders game which can be shot by Ships or Aliens.
 *
 * @param coordinate The Position of the Projectile.
 * @param damage     How much damage this Projectile does.
 */
public record Projectile(Coordinate coordinate, Integer damage) {

  @Override
  public Integer damage() {
    return damage;
  }

  @Override
  public Coordinate coordinate() {
    return coordinate;
  }
}