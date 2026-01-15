package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.ui.fourconnect.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SelectController {

  @FXML private Label titleLabel;

  private boolean player1Chosen = false;

  @FXML
  private void selectRed() {
    if (!player1Chosen) {
      player1Chosen = true;
      titleLabel.setText("Player 2: You are YELLOW ✅");
      // direkt ins Spiel
      SceneManager.showGame();
    }
  }

  @FXML
  private void selectYellow() {
    if (!player1Chosen) {
      player1Chosen = true;
      titleLabel.setText("Player 2: You are RED ✅");
      // direkt ins Spiel
      SceneManager.showGame();
    }
  }

  @FXML
  private void back() {
    SceneManager.showMain();
  }
}
