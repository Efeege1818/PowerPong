package de.hhn.it.devtools.apis.fourconnect;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;

/**
 * Central facade interface for controlling and interacting with a Connect Four game instance.
 * This service manages the game flow, board state, and turn changes.
 */
public interface ConnectFourService {

  /**
   * Initializes and starts a new game with the provided configuration.
   *
   * @param configuration The {@link GameConfiguration} specifying game parameters (e.g., toxic fields).
   */
  void startGame(GameConfiguration configuration);

  /**
   * Attempts to drop the current player's chip into the specified column.
   * This operation handles turn changes and notifies listeners of board updates.
   *
   * @param column The zero-based index of the column where the chip should be dropped.
   * @return The zero-based index of the row where the chip landed.
   * @throws IllegalParameterException If the column index is out of bounds or the column is full.
   * @throws OperationNotSupportedException If the game has not been started yet or has already ended.
   */
  int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException;

  /**
   * @return Das GameBoard-Interface, um den Zustand abzurufen.
   */
  GameBoard getBoard();

  /**
   * Returns the player who is currently scheduled to make a move.
   *
   * @return The {@link Player} whose turn it is.
   */
  Player getCurrentPlayer();

  /**
   * Checks if the last move resulted in a win for the current or previous player.
   *
   * @return {@code true} if a winning condition has been met, {@code false} otherwise.
   */
  boolean checkForWin();

  /**
   * Checks if the game board is full and no more moves can be made, resulting in a draw.
   *
   * @return {@code true} if the game has ended in a draw, {@code false} otherwise.
   */
  boolean checkForDraw();

  /**
   * Registers a listener to receive notifications about game state changes (e.g., turn change, board update).
   *
   * @param listener The {@link GameListener} to add.
   */
  void addGameListener(GameListener listener);

  /**
   * Deregisters a previously added listener.
   *
   * @param listener The {@link GameListener} to remove.
   */
  void removeGameListener(GameListener listener);
}