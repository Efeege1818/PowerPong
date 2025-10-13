package de.hhn.it.devtools.apis.towerDefenseApis;

public interface Map {

    /**
     * Generates a new Map and saves it as a two-dimensional array
     * @param size the length and height of the map
     */
    public void generateMap(int size);

    /**
     * Provides a copy of the underlying Data Structure that represents the enemy path
     * @return the grid of Directions as a two-dimensional array
     */
    public Direction[][] getGrid();

}
