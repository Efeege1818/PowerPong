package de.hhn.it.devtools.apis.spaceinvaders.entities;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Ship Represents the Player Character in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of the Ship.
 * @param hitPoints  How much damage the Ship can take before it is destroyed.
 */
public record Ship(Coordinate coordinate, Integer hitPoints) {
}
