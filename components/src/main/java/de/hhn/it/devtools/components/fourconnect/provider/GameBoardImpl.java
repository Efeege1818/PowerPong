package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.Player;

/**
 * Concrete implementation of the {@link GameBoard} interface for the Four-Connect game.
 * <p>
 * This class manages the 2D array of {@link FieldImpl} objects that represent the
 * game grid. It defines the standard dimensions for a Four-Connect board (6 rows
 * and 7 columns) and provides methods for accessing the fields, clearing the board,
 * and placing chips.
 * </p>
 */
public class GameBoardImpl implements GameBoard {

  /** The standard number of rows on the Four-Connect board. */
  public static final int ROWS = 6;
  /** The standard number of columns on the Four-Connect board. */
  public static final int COLUMNS = 7;

  private final FieldImpl[][] fields;

  /**
   * Constructs a new {@code GameBoardImpl} and initializes the internal
   * 2D array of fields based on {@link #ROWS} and {@link #COLUMNS}.
   * Note: The fields are not initialized to specific {@link FieldImpl} objects
   * until {@link #clearBoard()} is called.
   */
  public GameBoardImpl() {
    this.fields = new FieldImpl[ROWS][COLUMNS];
  }

  /**
   * Returns the entire 2D array of fields that constitute the game board.
   *
   * @return A 2D array of {@link Field} objects.
   */
  @Override
  public Field[][] getFields() {
    return fields;
  }

  /**
   * Returns the field at the specified row and column coordinates.
   *
   * @param row The row index (0-based) of the field.
   * @param column The column index (0-based) of the field.
   * @return The {@link Field} object at the given coordinates.
   */
  @Override
  public Field getField(int row, int column) {
    return fields[row][column];
  }

  /**
   * Returns the number of rows on the board.
   *
   * @return The number of rows ({@value #ROWS}).
   */
  @Override
  public int getRows() {
    return ROWS;
  }

  /**
   * Returns the number of columns on the board.
   *
   * @return The number of columns ({@value #COLUMNS}).
   */
  @Override
  public int getColumns() {
    return COLUMNS;
  }

  /**
   * Clears the board by initializing every field in the 2D array
   * with a new, empty {@link FieldImpl} object.
   * <p>
   * This method is package-private and intended for use within the provider package.
   * </p>
   */
  void clearBoard() {
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLUMNS; c++) {
        fields[r][c] = new FieldImpl(false); // Assuming 'false' means it's not a toxic zone by default
      }
    }
  }

  /**
   * Places a chip for the given player at the lowest available spot in the specified column.
   * <p>
   * The placement starts checking from the bottom row ({@code ROWS - 1}) upwards.
   * If the field is a toxic zone, its decay time is set to 3.
   * </p>
   *
   * @param column The column index (0-based) where the chip should be placed.
   * @param player The {@link Player} whose chip is being placed.
   * @return The row index (0-based) where the chip landed, or -1 if the column is full.
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
  }
}