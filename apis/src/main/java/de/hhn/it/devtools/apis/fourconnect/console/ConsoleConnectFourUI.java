package de.hhn.it.devtools.apis.fourconnect.console;

import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;

public class ConsoleConnectFourUI {

  // === UI Zeichen (Snake-Style) ===
  private static final char EMPTY = '.';
  private static final char TOXIC = 't';
  private static final char RED = 'R';
  private static final char YELLOW = 'Y';
  private static final char RED_TOXIC = 'r';
  private static final char YELLOW_TOXIC = 'y';

  // === Board Rendering ===
  public void render(GameBoard board) {
    clearScreen();
    int rows = board.getRows();
    int cols = board.getColumns();

    // Spaltennummern
    for (int c = 1; c <= cols; c++) {
      System.out.print(" " + c);
    }

  }

  private void clearScreen() {
    for (int i = 0; i < 40; i++) {
      System.out.println();
    }
  }

}