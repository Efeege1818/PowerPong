package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Listener interface for GUI updates.
 */
public interface GameListener {
  void onTurnChanged(Player currentPlayer);
  void onBoardChanged();
  void onGameEnded(Player winner);
  void onDraw();
}

