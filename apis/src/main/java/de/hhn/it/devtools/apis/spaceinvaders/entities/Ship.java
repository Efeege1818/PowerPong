package de.hhn.it.devtools.apis.spaceinvaders.entities;

/**
 * Ship Represents the Player Character in the SpaceInvader game.
 */
public interface Ship extends Entity {

  /**
   * Creates a Projectile which will travel downwards.
   *
   * @return the Projectile which has been shot out.
   */
  Projectile shootProjectile();
}
