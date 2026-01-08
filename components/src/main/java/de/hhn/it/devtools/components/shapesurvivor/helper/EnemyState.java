package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.*;

public class EnemyState {

    private int id;
    private int x;
    private int y;
    private int currentHealth;
    private final int maxHealth;
    private final double speed;
    private final int contactDamage;
    private final int experience;

    public EnemyState(Enemy enemy) {
        this.id = enemy.id();
        this.x = enemy.position().x();
        this.y = enemy.position().y();
        this.currentHealth = enemy.currentHealth();
        this.maxHealth = enemy.maxHealth();
        this.speed = enemy.movementSpeed();
        this.contactDamage = enemy.contactDamage();
        this.experience = enemy.experienceValue();
    }

    public Enemy toEnemy() {
        return new Enemy(
                id,
                new Position(x, y),
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
}