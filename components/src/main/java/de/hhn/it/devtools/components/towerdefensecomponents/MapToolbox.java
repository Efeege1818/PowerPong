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
   * Returns the enemy's path of the given grid.
   *
   * @param grid the grid on wich the path will be based on.
   * @return the path of the grid
   * @throws IllegalArgumentException if the given grid is null
   */
  public ArrayList<Coordinates> getPath(Grid grid) {
    return null;
  }

  /**
   * Returns the default money values for different enemy types.
   *
   * @param coordinates where the tower should be placed
   * @param towers is the list of towers already on the grid
   * @param grid the grid on wich the tower will be placed on
   * @return if the tower is allowed to be placed on the given coordinates
   * @throws IllegalArgumentException if any given parameters are null
   */
  public boolean isTowerPlacementAllowed(Coordinates coordinates, List<Tower> towers, Grid grid) {
    return false;
  }





}
