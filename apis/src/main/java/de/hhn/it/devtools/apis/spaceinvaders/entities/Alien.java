package de.hhn.it.devtools.apis.spaceinvaders.entities;

/**
 * Aliens Represent the Enemies in the SpaceInvaders game.
 */
public interface Alien extends Entity {

  /**
   * Creates a Projectile which will travel downwards.
   *
   * @return the Projectile which has been shot out.
   */
  Projectile shootProjectile();
}
