package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.Player;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.apis.shapesurvivor.Weapon;

/**
 * Mutable Player state for the PlayerSystem.
 */
public class PlayerState {

  private Position position;
  private int currentHealth;
  private int maxHealth;
  private double movementSpeed;
  private int baseDamage;
  private double attackSpeed;
  private double damageResistance;
  private int level;
  private int experience;
  private int experienceToNextLevel;
  private Weapon[] weapons;

  /**
   * Converts this mutable state to an immutable Player object.
   */
  public Player toPlayer() {
    return new Player(
            position,
            currentHealth,
            maxHealth,
            movementSpeed,
            baseDamage,
            attackSpeed,
            damageResistance,
            level,
            experience,
            experienceToNextLevel,
            weapons
    );
  }

  public Position getPosition() {
    return position;
  }

  public int getCurrentHealth() {
    return currentHealth;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public double getMovementSpeed() {
    return movementSpeed;
  }

  public int getBaseDamage() {
    return baseDamage;
  }

  public double getAttackSpeed() {
    return attackSpeed;
  }

  public double getDamageResistance() {
    return damageResistance;
  }

  public int getLevel() {
    return level;
  }

  public int getExperience() {
    return experience;
  }

  public int getExperienceToNextLevel() {
    return experienceToNextLevel;
  }

  public Weapon[] getWeapons() {
    return weapons;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void setCurrentHealth(int currentHealth) {
    this.currentHealth = currentHealth;
  }

  public void setMaxHealth(int maxHealth) {
    this.maxHealth = maxHealth;
  }

  public void setMovementSpeed(double movementSpeed) {
    this.movementSpeed = movementSpeed;
  }

  public void setBaseDamage(int baseDamage) {
    this.baseDamage = baseDamage;
  }

  public void setAttackSpeed(double attackSpeed) {
    this.attackSpeed = attackSpeed;
  }

  public void setDamageResistance(double damageResistance) {
    this.damageResistance = damageResistance;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setExperience(int experience) {
    this.experience = experience;
  }

  public void setExperienceToNextLevel(int experienceToNextLevel) {
    this.experienceToNextLevel = experienceToNextLevel;
  }

  public void setWeapons(Weapon[] weapons) {
    this.weapons = weapons;
  }
}