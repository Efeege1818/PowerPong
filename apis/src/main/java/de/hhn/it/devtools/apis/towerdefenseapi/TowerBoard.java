package de.hhn.it.devtools.apis.towerdefenseapi;

import java.util.Map;

/**
 * Data-Structure that contains the Positions for all Towers.
 *
 * @param board An Immutable Map that saves the Positions for all Towers
 */
public record TowerBoard(Map<Coordinates, Tower> board) {}
