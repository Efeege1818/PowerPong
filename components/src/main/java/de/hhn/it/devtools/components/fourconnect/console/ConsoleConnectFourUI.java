package de.hhn.it.devtools.components.fourconnect.console;

import java.util.Scanner;

import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
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

  private static final boolean SHOW_DECAY_TIME = false;

  // === Board Rendering ===
  public void render(GameBoard board) {
    clearScreen();

    int rows = board.getRows();
    int cols = board.getColumns();

    for (int c = 1; c <= cols; c++) {
      System.out.print(" " + c);
    }
    System.out.println();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        Field f = board.getField(r, c);
        System.out.print(" " + renderField(f));
      }
      System.out.println();
    }

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

  // ✅ HIER muss die Methode rein (innerhalb der Klasse!)
  public void start(ConnectFourService service) {
    Scanner sc = new Scanner(System.in);

    while (true) {
      render(service.getBoard());

      try {
        System.out.println("Turn: " + service.getCurrentPlayer().name()
            + " (" + service.getCurrentPlayer().color() + ")");
      } catch (OperationNotSupportedException e) {
        System.out.println("Game not started: " + e.getMessage());
      }

      System.out.print("Choose column (1-" + service.getBoard().getColumns() + ") or q to quit: ");
      String input = sc.nextLine();

      if (input != null && input.trim().equalsIgnoreCase("q")) {
        System.out.println("Bye!");
        return;
      }

      if (!isValidColumnInput(input, service.getBoard().getColumns())) {
        System.out.println("Invalid input. Try again.");
        continue;
      }

      int col = Integer.parseInt(input.trim()) - 1;

      try {
        service.dropChip(col);

        try {
          service.applyToxicDecay();
        } catch (OperationNotSupportedException ignored) {
          // ignore
        }

        if (service.checkForWin()) {
          render(service.getBoard());
          System.out.println("🏆 WINNER: " + service.getCurrentPlayer().name());
          return;
        }

        if (service.checkForDraw()) {
          render(service.getBoard());
          System.out.println("🤝 DRAW!");
          return;
        }

      } catch (Exception e) {
        System.out.println("Move not possible: " + e.getMessage());
      }
    }
  }
}
