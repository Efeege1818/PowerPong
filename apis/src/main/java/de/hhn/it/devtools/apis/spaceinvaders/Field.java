package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Represents the field of the game.
 */
public interface Field {

  /**
   * return size of board.
   *
   * @return size of the board.
   */
  int size();

  /**
   * Returns the FieldState at the given coordinate.
   *
   * @param coordinate requested coordinate
   * @return state at this coordinate
   * @throws IllegalArgumentException if the coordinate is either null or not on the board.
   */
  FieldState getStateFromPosition(Coordinate coordinate) throws IllegalArgumentException;

  /**
   * Returns the FieldState at the given coordinate.
   *
   * @param row requested row
   * @param column requested column
   * @return state at this coordinate
   * @throws IllegalArgumentException if the coordinate is either null or not on the board.
   */
  FieldState getStateFromPosition(int row, int column) throws IllegalArgumentException;
}
