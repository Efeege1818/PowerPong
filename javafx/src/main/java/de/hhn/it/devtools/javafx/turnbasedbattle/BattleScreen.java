package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class BattleScreen extends AnchorPane {
  public static final String SCREEN_NAME = "BattleScreen";

  private final SimpleScreenManager screenManager;

  private final BattleScreenController controller;

  public BattleScreen(SimpleScreenManager screenManager, TurnBasedBattleService service) {
    this.screenManager = screenManager;
    this.controller = new BattleScreenController();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleScreen.fxml"));
    loader.setRoot(this);
    loader.setController(controller);

    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    controller.setDependencies(screenManager, service);

    setFocusTraversable(true);
    sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null) {
        controller.installKeyHandling(newScene);
        Platform.runLater(this::requestFocus);
      }
    });
  }

  public BattleScreenController getController() {
    return controller;
  }
}
