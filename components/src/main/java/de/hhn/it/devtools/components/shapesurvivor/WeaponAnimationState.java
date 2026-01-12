package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.Weapon;

/**
 * Tracks animation state for weapons.
 */
public class WeaponAnimationState {
  private double angle;
  private long lastAttackTime;
  private boolean attacking;
  private static final long ATTACK_DURATION_MS = 300;
  private boolean attackingLeft;
  private long lastDamageTime;
  private static final long AURA_DAMAGE_INTERVAL_MS = 500;

  /**
   * Creates a new weapon animation state.
   */
  public WeaponAnimationState() {
    this.angle = 0;
    this.lastAttackTime = 0;
    this.attacking = false;
  }

  /**
   * Updates the weapon animation state.
   */
  public void update(Weapon weapon) {
    double rotationSpeed = weapon.attackSpeed() * 0.05;
    angle += rotationSpeed;
    if (angle > 2 * Math.PI) {
      angle -= 2 * Math.PI;
    }
    if (attacking && System.currentTimeMillis() - lastAttackTime > ATTACK_DURATION_MS) {
      attacking = false;
    }
  }

  /**
   * Checks if the weapon can attack.
   */
  public boolean canAttack() {
    long currentTime = System.currentTimeMillis();
    return currentTime - lastAttackTime >= (1000.0 / 1.5); // Attack cooldown
  }

  /**
   * Triggers an attack.
   */
  public void attack() {
    lastAttackTime = System.currentTimeMillis();
    attacking = true;
    attackingLeft = !attackingLeft;
  }

  public double getAngle() {
    return angle;
  }

  public boolean isNotAttacking() {
    return !attacking;
  }

  /**
   *  gets the Attack Progress Time.
   */
  public long getAttackProgress() {
    if (!attacking) {
      return 0;
    }
    return System.currentTimeMillis() - lastAttackTime;
  }

  /**
   * Checks if aura damage can be dealt.
   */
  public boolean canDealAuraDamage() {
    long now = System.currentTimeMillis();
    if (now - lastDamageTime >= AURA_DAMAGE_INTERVAL_MS) {
      lastDamageTime = now;
      return true;
    }
    return false;
  }

  public boolean isAttackingLeft() {
    return attackingLeft;
  }
}