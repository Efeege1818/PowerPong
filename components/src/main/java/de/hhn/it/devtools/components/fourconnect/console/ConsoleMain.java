package de.hhn.it.devtools.components.fourconnect.console;

import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;

/**
 * Console entry point for a simple Connect Four demo.
 */
public final class ConsoleMain {

  private ConsoleMain() {
    // utility class
  }

  /**
   * Starts a small demo game in the console.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {

    ConnectFourService service = new ConnectFourServiceImpl();
    service.startGame(new GameConfiguration(5, 3));

    // Demo moves
    try {
      service.dropChip(0); // Red
      service.dropChip(0); // Yellow
      service.dropChip(1); // Red
    } catch (Exception e) {
      System.out.println("Demo move failed: " + e.getMessage());
    }

    // Render board
    ConsoleConnectFourUi ui = new ConsoleConnectFourUi();
    ui.render(service.getBoard());

    System.out.println("Demo OK: Game started, 3 moves executed, board rendered.");
  }
}
