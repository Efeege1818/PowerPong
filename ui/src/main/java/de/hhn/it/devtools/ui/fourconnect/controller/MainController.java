package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.ui.fourconnect.SceneManager;
import javafx.fxml.FXML;

public class MainController {

 @FXML
private void onNewGame() {
  SceneManager.showSelect();
}


  @FXML
  private void onLoadGame() {
    // optional: später
    SceneManager.showGame();
  }
}
