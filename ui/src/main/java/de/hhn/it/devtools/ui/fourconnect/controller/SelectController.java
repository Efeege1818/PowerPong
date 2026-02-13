package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.ui.fourconnect.SceneManager;
import de.hhn.it.devtools.ui.fourconnect.UIState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SelectController {

  @FXML private Label titleLabel; // optional (falls du im FXML eine Label fx:id="titleLabel" hast)

  private final SceneManager sceneManager;

  public SelectController(SceneManager sceneManager) {
    this.sceneManager = sceneManager;
  }

  @FXML
  private void selectRed() {
    UIState.setPlayer1Color(PlayerColor.RED);
    sceneManager.showGame();
  }

  @FXML
  private void selectYellow() {
    UIState.setPlayer1Color(PlayerColor.YELLOW);
    sceneManager.showGame();
  }

  @FXML
  private void back() {
    // Wenn du ein Main-Menü hast: sceneManager.showMain();
    // Wenn nicht: einfach im Select bleiben oder Fenster schließen
    sceneManager.showMain(); // <- NUR falls du showMain() hast. Sonst: auskommentieren und unten close nutzen.
  }

  @FXML
  private void onExit() {
    // Fenster schließen (nur wenn titleLabel existiert)
    if (titleLabel != null && titleLabel.getScene() != null) {
      Stage stage = (Stage) titleLabel.getScene().getWindow();
      stage.close();
    }
  }
}
