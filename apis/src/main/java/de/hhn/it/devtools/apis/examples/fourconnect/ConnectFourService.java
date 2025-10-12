package de.hhn.it.devtools.apis.examples.fourconnect;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;

public interface ConnectFourService {
  /**
   * Wirft einen Chip in die Spalte. Dies ist die Aktion des Spielers.
   * Der Aufruf löst auch die Zerfallslogik aus.
   */
  int dropChip(int column) throws IllegalParameterException;
  // Gibt die Reihe des Chips zurück (int) und wirft eine Exception bei voller Spalte.
  

  Player[][] getBoardState();


  Player getCurrentPlayer();


  boolean checkForWin();


  void resetGame();


  boolean checkForDraw();


  void applyToxicDecay();

  void updateScore(Player player, int points);
  int resetTurn();
  void incrementTurn();
  void resetBoard();
  void placeRandomToxicFields();
}
