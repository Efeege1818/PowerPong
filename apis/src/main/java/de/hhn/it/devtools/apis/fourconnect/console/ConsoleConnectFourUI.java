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

  // Optional: DecayTime sichtbar machen (1..9)
  // Wenn du das nicht willst, setze auf false.
  private static final boolean SHOW_DECAY_TIME = false;

  // === Board Rendering ===
  public void render(GameBoard board) {
    clearScreen();

    int rows = board.getRows();
    int cols = board.getColumns();

    // Spaltennummern
    for (int c = 1; c <= cols; c++) {
      System.out.print(" " + c);
    }
    System.out.println();

    // Grid
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Field f = board.getField(r, c);
        System.out.print(" " + renderField(f));
      }
      System.out.println();
    }

    // Legend
    System.out.println();
    System.out.println("Legend:");
    System.out.println(" " + EMPTY + " = empty field");
    System.out.println(" " + RED + "/" + YELLOW + " = normal chip");
    System.out.println(" " + TOXIC + " = toxic field");
    System.out.println(" " + RED_TOXIC + "/" + YELLOW_TOXIC + " = chip on toxic field");
    if (SHOW_DECAY_TIME) {
      System.out.println(" 1..9 = decay time on toxic field (optional view)");
    }
    System.out.println();
  }

  private char renderField(Field f) {
    boolean toxic = f.isToxicZone();

    // Optional: DecayTime anzeigen (nur wenn Feld toxic ist und > 0)
    if (SHOW_DECAY_TIME && toxic) {
      int decay = f.getDecayTime();
      if (decay > 0 && decay < 10) {
        return Character.forDigit(decay, 10);

      } // falls decay >= 10 oder 0: einfach normales Toxic-Symbol benutzen
    }

    if (f.isOccupied()) {
      Player p = f.getOccupyingPlayer();
      boolean red = p != null && p.color() == PlayerColor.RED;

      if (toxic)
        return red ? RED_TOXIC : YELLOW_TOXIC;
      return red ? RED : YELLOW;
    }

    if (toxic)
      return TOXIC;
    return EMPTY;
  }

  private void clearScreen() {
    for (int i = 0; i < 40; i++) {
      System.out.println();
    }
  }

  public boolean isValidColumnInput(String input, int cols) {
    if (input == null)
      return false;
    input = input.trim();
    if (input.isEmpty())
      return false;
    if (input.equalsIgnoreCase("q"))
      return true;

    try {
      int col = Integer.parseInt(input);
      return col >= 1 && col <= cols;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
