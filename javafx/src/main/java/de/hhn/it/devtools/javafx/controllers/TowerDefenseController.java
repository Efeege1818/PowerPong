package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.javafx.controllers.template.PurProgrammingScreen;
import de.hhn.it.devtools.javafx.controllers.template.ScreenController;
import de.hhn.it.devtools.javafx.controllers.template.SingletonAttributeStore;
import de.hhn.it.devtools.javafx.controllers.template.UnknownTransitionException;
import de.hhn.it.devtools.javafx.towerdefense.controllers.TowerDefenseLauncher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class TowerDefenseController extends Controller implements Initializable {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(TowerDefenseController.class);
  public static final String SCREEN_CONTROLLER = "screen.controller";

  @FXML
  AnchorPane templateAnchorPane;
  ScreenController screenController;

  public TowerDefenseController() {
    logger.debug("TowerDefense Controller created.");
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    // initialize screenController here because now we have the anchorPane.
    screenController = new ScreenController(templateAnchorPane);


  }

  @FXML
  public void startGameButton() {
    logger.info("Starting towerDefense Game");
  }
}
