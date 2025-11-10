package de.hhn.it.devtools.apis.towerdefenseapi;

/**
 * Represents a tower in the Tower Defense game.
 *
 * <p>A tower has a unique ID, an attack speed (how much time between shots),
 * a price (cost to place), a range (how far it can attack),
 * fixed coordinates on the map, and a specific tower type (e.g., MELEE, MONEYMAKER and RANGED).
 *
 * <p>This record is immutable – once created, the tower's properties cannot be changed.
 */
public record Tower(int id,
                    Coordinates coordinates,
                    TowerType type) {
}
