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

    public WeaponAnimationState() {
        this.angle = 0;
        this.lastAttackTime = 0;
        this.attacking = false;
    }

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

    public boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastAttackTime >= (1000.0 / 1.5); // Attack cooldown
    }

    public void attack() {
        lastAttackTime = System.currentTimeMillis();
        attacking = true;
        attackingLeft = !attackingLeft;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public long getAttackProgress() {
        if (!attacking) return 0;
        return System.currentTimeMillis() - lastAttackTime;
    }

    public boolean isAttackingLeft() {
        return attackingLeft;
    }
}