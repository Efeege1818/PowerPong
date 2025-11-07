package de.hhn.it.devtools.components.spaceinvaders.utils;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import java.util.ArrayList;

/**
 * EntityProvider for all Entity Utils.
 */
public class EntityProvider {

  /**
   * Method to Calculate Entity hitbox.
   *
   * @param x coordinate to calculate hitbox.
   * @param y coordinate to calculate hitbox
   * @return hitbox ArrayList.
   */
  public static ArrayList<Coordinate> fillHitBox(Coordinate coordinate, int x, int y) {
    ArrayList<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        coords.add(new Coordinate(coordinate.x() + i, coordinate.y() + j));
      }
    }
    return coords;
  }

}
