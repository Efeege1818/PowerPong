package de.hhn.it.devtools.apis.spaceinvaders.entities;


import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;

/**
 * Represents the Projectiles in the SpaceInvaders game which can be shot by Ships or Aliens.
 *
 * @param coordinate The Position of the Projectile.
 */
public record Projectile(Coordinate coordinate, int projectileId, Direction direction) {
}