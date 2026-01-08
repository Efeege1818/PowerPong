package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.*;

public class EnemyState {

    public int id;
    public int x;
    public int y;
    public int currentHealth;
    final int maxHealth;
    public final double speed;
    public final int contactDamage;
    public final int experience;

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
}
