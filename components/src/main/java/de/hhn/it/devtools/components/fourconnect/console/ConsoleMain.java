package de.hhn.it.devtools.components.fourconnect.console;

import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;

public class ConsoleMain {

  public static void main(String[] args) {

    ConnectFourService service = new ConnectFourServiceImpl();
    service.startGame(new GameConfiguration(5, 3));

    // 🎯 Demo-Moves (fest, kein Input nötig)
    try {
      service.dropChip(0); // Red
      service.dropChip(0); // Yellow
      service.dropChip(1); // Red
    } catch (Exception e) {
      System.out.println("Demo move failed: " + e.getMessage());
    }

    // Board anzeigen
    ConsoleConnectFourUI ui = new ConsoleConnectFourUI();
    ui.render(service.getBoard());

    System.out.println("✅ Demo OK: Game started + 3 moves executed + board rendered");
  }
}
