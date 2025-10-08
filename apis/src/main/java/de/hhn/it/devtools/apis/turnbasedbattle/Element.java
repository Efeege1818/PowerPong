package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Enum to represent different elemental types of monsters and moves.
 */
public enum Element {

  /**
   * The object has the normal element.
   * Normal has no special traits with other elements.
   */
  NORMAL,

  /**
   * The object has the fire element.
   * Fire is effective against grass.
   */
  FIRE,

  /**
   * The object has the water element.
   * Water is effective against fire.
   */
  WATER,

  /**
   * The object has the grass element.
   * Grass is effective against water.
   */
  GRASS
}
