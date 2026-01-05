package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

/**
 * Represents an Alien Enemy in the SpaceInvader game.
 *
 * @param coordinate The Position of the Center of an Alien.
 * @param hitPoints  How much damage the Alien can take before it is destroyed.
 * @param alienType  describes what type of Alien it is.
 * @param alienId    the ID that Identifies Aliens.
 */
public record Alien(Coordinate coordinate, Integer hitPoints, AlienType alienType,
                    Integer alienId) {
}
