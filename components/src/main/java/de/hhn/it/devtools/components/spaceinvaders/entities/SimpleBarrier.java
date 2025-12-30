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
  private final ArrayList<Coordinate> hitbox;
  private final int id;

  /**
   * Basic Constructor for SimpleBarrier.
   *
   * @param coordinate start coordinate top left.
   * @param id id for identification.
   */
  public SimpleBarrier(Coordinate coordinate, int id) {
    this.coordinate = coordinate;
    this.hitbox = EntityProvider.fillHitBox(coordinate, APIConstants.BARRIER_HITBOX_WIDTH, APIConstants.BARRIER_HITBOX_HEIGHT);
    this.id = id;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public ArrayList<Coordinate> getHitbox() {
    return hitbox;
  }

  public Barrier getImmutableBarrier() {
    return new Barrier(this.coordinate, id);
  }

}
