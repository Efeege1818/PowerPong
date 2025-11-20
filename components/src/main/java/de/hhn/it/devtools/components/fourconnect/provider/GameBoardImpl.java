package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.Player;

/**
 * Concrete implementation of the GameBoard interface.
 * Manages the 2D array of FieldImpl objects.
 */
public class GameBoardImpl implements GameBoard {

  public static final int ROWS = 6;
  public static final int COLUMNS = 7;
  private final FieldImpl[][] fields;

  public GameBoardImpl() {
    this.fields = new FieldImpl[ROWS][COLUMNS];
  }

  @Override
  public Field[][] getFields() {
    return fields;
  }

  @Override
  public Field getField(int row, int column) {
    return fields[row][column];
  }

  @Override
  public int getRows() {
    return ROWS;
  }

  @Override
  public int getColumns() {
    return COLUMNS;
  }

  /**
   * Clears the board and initializes all fields. (Package-private)
   */
  void clearBoard() {
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLUMNS; c++) {
        fields[r][c] = new FieldImpl(false);
      }
    }
  }

  /**
   * Places a chip (Player) at the lowest available spot in a column.
   * @return The row where the chip landed, or -1 if the column is full.
   */

  int placeChip(int column, Player player) {
    for (int r = ROWS - 1; r >= 0; r--) {
      if (fields[r][column].getOccupyingPlayer() == null) {
        fields[r][column].setOccupyingPlayer(player);

        if (fields[r][column].isToxicZone()) {
          fields[r][column].setDecayTime(3);
        }
        return r;
      }
    }
    return -1;
  }}
