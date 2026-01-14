package de.hhn.it.devtools.apis.towerdefenseapi;

/**
 * Data Structure that stores the positions on the Board.
 *
 * @param grid two-dimensional Array that stores the types of tiles on the Game Grid
 */
public record Grid(Direction[][] grid) {}
