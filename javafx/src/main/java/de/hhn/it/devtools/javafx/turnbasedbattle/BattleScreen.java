package de.hhn.it.devtools.javafx.turnbasedbattle;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class BattleScreen extends AnchorPane {
  public static final String SCREEN_NAME = "BattleScreen";

  private final SimpleScreenManager screenManager;

  public BattleScreen(SimpleScreenManager screenManager) {
    this.screenManager = screenManager;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleScreen.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
