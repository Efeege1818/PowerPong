package de.hhn.it.devtools.apis.towerdefenseapi;

import java.util.Map;

/**
 * Data-Structure that contains the Positions for all Enemy.
 *
 * @param board An Immutable Map that saves the Positions for all Enemies
 */
public record EnemyBoard(Map<Coordinates, Enemy> board) {}
