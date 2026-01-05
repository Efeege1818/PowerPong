package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.components.spaceinvaders.utils.EntityProvider;
import java.util.ArrayList;

/**
 * Represents an unmoving SimpleBarrier in the SpaceInvader game.
 */
public class SimpleBarrier {

  private final Coordinate coordinate;
  private final int id;

  /**
   * Basic Constructor for SimpleBarrier.
   *
   * @param coordinate start coordinate top left.
   * @param id id for identification.
   */
  public SimpleBarrier(Coordinate coordinate, int id) {
    this.coordinate = coordinate;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public Barrier getImmutableBarrier() {
    return new Barrier(this.coordinate, id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SimpleBarrier that = (SimpleBarrier) o;
    return this.id == that.id && this.coordinate.equals(that.coordinate);
  }

  @Override
  public int hashCode() {
    int result = coordinate.hashCode();
    result = 31 * result + this.id;
    return result;
  }
}
