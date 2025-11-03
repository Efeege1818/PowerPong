package de.hhn.it.devtools.apis.towerdefenseapi;

import java.util.List;

/**
 * Data-Structure that contains the Positions for all Enemy.
 *
 * @param enemies An Immutable List that saves the Positions for all Enemies
 */
public record EnemyList(List<Enemy> enemies) {}
