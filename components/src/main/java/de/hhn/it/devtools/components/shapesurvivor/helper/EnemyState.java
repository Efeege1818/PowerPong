package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.Enemy;
import de.hhn.it.devtools.apis.shapesurvivor.Position;

/**
 * Mutable Enemy state for the EnemySystem.
 */
public class EnemyState {

  private final int id;
  private int xpos;
  private int ypos;
  private int currentHealth;
  private final int maxHealth;
  private final double speed;
  private final int contactDamage;
  private final int experience;

  /**
   * Creates a new EnemyState from an Enemy object.
   *  @param enemy the enemy to create state from
   */
  public EnemyState(Enemy enemy) {
    this.id = enemy.id();
    this.xpos = enemy.position().x();
    this.ypos = enemy.position().y();
    this.currentHealth = enemy.currentHealth();
    this.maxHealth = enemy.maxHealth();
    this.speed = enemy.movementSpeed();
    this.contactDamage = enemy.contactDamage();
    this.experience = enemy.experienceValue();
  }

  /**
   * Returns the Immutable version of an Enemy.
   *  @return new Enemy
   */
  public Enemy toEnemy() {
    return new Enemy(
            id,
            new Position(xpos, ypos),
            currentHealth,
            maxHealth,
            speed,
            contactDamage,
            experience
    );
  }

  // Getters
  public int getId() {
    return id;
  }

  public int getXpos() {
    return xpos;
  }

  public int getYpos() {
    return ypos;
  }

  public int getCurrentHealth() {
    return currentHealth;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public double getSpeed() {
    return speed;
  }

  public int getContactDamage() {
    return contactDamage;
  }

  public int getExperience() {
    return experience;
  }

  // Setters
  public void setXpos(int xpos) {
    this.xpos = xpos;
  }

  public void setYpos(int ypos) {
    this.ypos = ypos;
  }

  public void setCurrentHealth(int currentHealth) {
    this.currentHealth = currentHealth;
  }
}