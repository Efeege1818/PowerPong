package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.javafx.turnbasedbattle.Launcher;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class TurnBasedBattleController extends Controller implements Initializable {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(TurnBasedBattleController.class);

  @FXML
  AnchorPane templateAnchorPane;

  public TurnBasedBattleController() {
    logger.debug("TurnBasedBattle Controller created.");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {}

  @FXML
  public void startGameButton() {
    logger.info("Starting TurnBasedBattle");
    Launcher.openNewWindow();
  }
}
