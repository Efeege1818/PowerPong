package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;

/**
 * Aliens Represent the Enemies in the SpaceInvaders game.
 */
public interface Alien extends Entity {

  /**
   * All Aliens move in the same direction which can be changed.
   */
  public static Direction alienDirection = Direction.RIGHT;

  /**
   * Creates a Projectile which will travel downwards.
   *
   * @return the Projectile which has been shot out.
   */
  Projectile shootProjectile();
}
