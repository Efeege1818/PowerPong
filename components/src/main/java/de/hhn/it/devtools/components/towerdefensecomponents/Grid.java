package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Direction;

/**
 * Grid on which the Path is generated and the Towers are placed.
 */
public interface Grid {

	/**
	 * Generates a new Map and saves it as a two-dimensional array.
	 *
	 * @param size the length and height of the map must be a positive number
	 */
	public void generateGrid(int size) throws IllegalArgumentException;

	/**
	 * Provides a copy of the underlying Data Structure that represents the enemy path.
	 *
	 * @return the grid of Directions as a two-dimensional array
	 */
	public Direction[][] getGrid() throws RuntimeException;

}
