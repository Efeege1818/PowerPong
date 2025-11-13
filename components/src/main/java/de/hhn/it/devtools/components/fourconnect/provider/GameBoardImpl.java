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

  private final Field[][] fields;

  public GameBoardImpl() {
    this.fields = new Field[ROWS][COLUMNS];
    // Board must be initialized (see clearBoard)
  }

  // --- Public Read-Only Methods (from Interface) ---

  @Override
  public Field[][] getFields() {
    return fields;
  }

  @Override
  public Field getField(int row, int column) {
    // (Bounds checking should be added here later)
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
        // Default: not toxic
        fields[r][c] = new Field() {
          @Override
          public Player getOccupyingPlayer() {
            return null;
          }

          @Override
          public boolean isToxicZone() {
            return false;
          }

          @Override
          public int getDecayTime() {
            return 0;
          }

          @Override
          public Player getOccupyingPlayer(Player player) {
            return null;
          }

          @Override
          public boolean isToxic() {
            return false;
          }
        };
      }
    }
  }

  /**
   * Gets the concrete FieldImpl object (package-private).
   */
  Field getFieldImpl(int row, int column) {
    return fields[row][column];
  }

  /**
   * Places a chip (Player) at the lowest available spot in a column.
   * @return The row where the chip landed, or -1 if the column is full.
   */

  int placeChip(int column, Player player) {
    for (int r = ROWS - 1; r >= 0; r--) {
      if (fields[r][column].getOccupyingPlayer(player) == null) {
        fields[r][column].getOccupyingPlayer(player);

        if (fields[r][column].isToxic()) {
        }
        return r;
      }
    }
    return -1;
  }
}