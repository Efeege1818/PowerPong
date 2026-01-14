package de.hhn.it.devtools.apis.towerdefenseapi;

import java.util.UUID;

/**
 * Represents an enemy in the Tower Defense game.
 *
 * <p>Each enemy has a unique ID, a movement speed (how much time between movements),
 * a health value, a path it follows, and a type (e.g. SMALL, MEDIUM, LARGE).
 * Enemies move along their assigned path toward the player's base.
 *
 * <p>This record is immutable — once created, the enemy’s properties cannot change.
 *
 * @param id            a unique Identifier
 * @param coordinates   the position of this enemy on the Board
 * @param type          the EnemyType of this Enemy
 * @param currentHealth the current Health of this enemy
 * @param index         the current index in the path progression
 */
public record Enemy(UUID id,
                    Coordinates coordinates,
                    EnemyType type,
                    int currentHealth,
                    int index
){}
