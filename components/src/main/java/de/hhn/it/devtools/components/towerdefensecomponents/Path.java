package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import java.util.ArrayList;

/**
 * Determines whether a certain list of coordinates is PATH or free for other things.
 */
public interface Path {

  // TODO: Comments to add

  /**
   * Returns the list of Coordinates as Arraylist, it must be generated first.
   *
   * @return Arraylist of Coordinates
   */
  ArrayList<Coordinates> getPath();


}
