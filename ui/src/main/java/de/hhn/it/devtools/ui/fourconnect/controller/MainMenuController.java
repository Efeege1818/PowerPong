package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.ui.fourconnect.SceneManager;
import javafx.fxml.FXML;

public class MainMenuController {

  private final SceneManager sceneManager;

  public MainMenuController(SceneManager sceneManager) {
    this.sceneManager = sceneManager;
  }

  @FXML
  private void onNewGame() {
    sceneManager.showSelect();
  }

  @FXML
  private void onLoadGame() {
    // später: load logic
    sceneManager.showGame();
  }

  @FXML
  private void onExitApp() {
    System.exit(0);
  }
}
