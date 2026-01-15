package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.ui.fourconnect.SceneManager;
import de.hhn.it.devtools.ui.fourconnect.UIState;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SelectController {

  @FXML private Label titleLabel;
  @FXML private Label subtitleLabel;

  private final SceneManager sceneManager;

  private boolean player1Selected = false;

  public SelectController(SceneManager sceneManager) {
    this.sceneManager = sceneManager;
  }

  @FXML
  public void initialize() {
    UIState.reset();
    titleLabel.setText("Player 1: Select your Character");
    subtitleLabel.setText("");
  }

  @FXML
  private void selectRed() {
    if (!player1Selected) {
      // Player 1 wählt RED -> Player 2 automatisch YELLOW
      UIState.setPlayer1Color(PlayerColor.RED);
      UIState.setPlayer2Color(PlayerColor.YELLOW);

      player1Selected = true;
      titleLabel.setText("Player 1 selected RED.");
      subtitleLabel.setText("Player 2: your character is YELLOW.\nClick any color to continue.");
      return;
    }
    sceneManager.showGame();
  }

  @FXML
  private void selectYellow() {
    if (!player1Selected) {
      // Player 1 wählt YELLOW -> Player 2 automatisch RED
      UIState.setPlayer1Color(PlayerColor.YELLOW);
      UIState.setPlayer2Color(PlayerColor.RED);

      player1Selected = true;
      titleLabel.setText("Player 1 selected YELLOW.");
      subtitleLabel.setText("Player 2: your character is RED.\nClick any color to continue.");
      return;
    }
    sceneManager.showGame();
  }

  @FXML
  private void back() {
    sceneManager.showMain();
  }

  @FXML
  private void onExit() {
    Stage stage = (Stage) titleLabel.getScene().getWindow();
    stage.close();
  }
}
