package de.hhn.it.devtools.apis.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;

/**
 * An Entity Represents an Object on the GameField.
 */
public interface Entity {
  /**
   * Makes the Entity move in a given Direction.
   *
   * @param direction direction in which the entity will move.
   * @return returns the new Coordinate the entity will be in after moving.
   */
  Coordinate move(Direction direction);

  /**
   * Reduces the HealthPoints of the Entity by the given Integer.
   *
   * @param damage the amount of Damage the Entity will take.
   */
  void takeDamage(Integer damage);

}
