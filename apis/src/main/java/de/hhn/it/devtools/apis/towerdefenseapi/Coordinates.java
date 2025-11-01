package de.hhn.it.devtools.apis.towerdefenseapi;


/**
 * Holds Coordinates for the positioning of Towers and Enemies.
 *
 * @param x Position on the ordinate
 * @param y Position on the abscissa
 */
public record Coordinates(float x, float y) {

  @Override
  public float x() {
    return x;
  }

  @Override
  public float y() {
    return y;
  }
}
