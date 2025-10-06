package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Used for 2D coordinates. Coordinates are immutable.
 */

public record Coordinate(int row, int column) {
}
// benötigt Pixel coordinaten.
//Stage oder Fenster ?
// x und y statt row collumn