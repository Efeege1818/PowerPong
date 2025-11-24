package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Direction;

public class SimpleGrid implements Grid {

	Direction[][] directionGrid = new Direction[0][0];

	@Override
	public void generateGrid(int size) {
		if (size < 1) {
			throw new IllegalArgumentException();
		}

	}

	@Override
	public Direction[][] getGrid() throws RuntimeException {
		if (directionGrid == null) {
			throw new RuntimeException();
		}
		return directionGrid;
	}
}
