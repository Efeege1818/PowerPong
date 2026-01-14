package de.hhn.it.devtools.apis.fourconnect;

/**
 * Immutable interface to query the game board state.
 */
public interface GameBoard {

    /**
     * Returns all fields of the board as a 2D array.
     */
    Field[][] getFields();

    /**
     * Returns the field at the specified position.
     */
    Field getField(int row, int column);

    /**
     * Returns number of rows.
     */
    int getRows();

    /**
     * Returns number of columns.
     */
    int getColumns();
}
