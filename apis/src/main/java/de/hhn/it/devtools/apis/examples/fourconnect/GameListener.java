package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Listener interface for GUI or other components to receive updates about the state
 * of a Four Connect (or similar) game.
 */
public interface GameListener {
  /**
   * Called when the turn changes to a new player.
   *
   * @param currentPlayer The {@link Player} whose turn it is now.
   */
  void onTurnChanged(Player currentPlayer);

  /**
   * Called when the game board's state has changed, typically after a successful move.
   * Components should redraw or update their view of the board.
   */
  void onBoardChanged(GameBoard board);

  /**
   * Called when the game has ended and a winner has been determined.
   *
   * @param winner The {@link Player} who won the game. Can be null if the game
   * has ended but resulted in a draw (in which case {@link #onDraw()}
   * should also be called, or used exclusively for a draw).
   */
  void onGameEnded(Player winner);

  /**
   * Called when the game has ended in a draw (a tie).
   */
  void onDraw();
}