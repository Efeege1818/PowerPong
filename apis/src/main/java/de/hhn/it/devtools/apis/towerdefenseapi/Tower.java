package de.hhn.it.devtools.apis.towerdefenseapi;

import java.util.UUID;

/**
 * Represents a tower in the Tower Defense game.
 *
 * <p>A tower has a unique ID, an attack speed (how much time between shots),
 * a price (cost to place), a range (how far it can attack),
 * fixed coordinates on the map, and a specific tower type (e.g., MELEE, MONEYMAKER and RANGED).
 *
 * <p>This record is immutable – once created, the tower's properties cannot be changed.
 */
public record Tower(UUID id,
                    Coordinates coordinates,
                    TowerType type) {

  Tower(Coordinates coordinates, TowerType type) {
    this(UUID.randomUUID(), coordinates, type);
  }
}
