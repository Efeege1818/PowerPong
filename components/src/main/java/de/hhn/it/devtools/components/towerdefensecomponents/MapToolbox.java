package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class MapToolbox {



  /**
   * Generates a Grid with the Dijkstra's algorithm.
   *
   * @param size of the Grid
   * @return generated Grid
   * @throws IllegalArgumentException if the given size is too small
   */
  public Grid generateMap(int size) {
    return null;
  }


  /**
   * Calculates the enemy's path.
   *
   * @param type the EnemyType of the Enemy
   * @return the default money for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public ArrayList<Coordinates> getPath(Grid grid) {
    return null;
  }

  /**
   * Returns the default money values for different enemy types.
   *
   * @param type the EnemyType of the Enemy
   * @return the default money for an enemy of the given type
   * @throws NoSuchElementException if the given EnemyType isn't supported
   */
  public boolean isTowerPlacementAllowed(Coordinates coordinates, List<Tower> towers, Grid grid) {
    return false;
  }





}
