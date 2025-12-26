package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.javafx.spaceinvaders.view.SpaceInvadersScreen;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpaceInvaders Controller class.
 */
public class SpaceInvadersController extends Controller implements Initializable {
  private static final Logger logger = LoggerFactory.getLogger(SpaceInvadersController.class);

  @FXML
  AnchorPane spaceInvadersPane;

  @FXML
  Button spaceInvadersButton;

  @FXML
  ChoiceBox<Difficulty> difficultyChoiceBox;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    difficultyChoiceBox.setValue(Difficulty.NORMAL);
    difficultyChoiceBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
  }

  /**
   * start button.
   */
  public void doActionStart() {
    Stage stage = (Stage) spaceInvadersPane.getScene().getWindow();
    stage.close();
    GameConfiguration gameConfiguration = new GameConfiguration(3, difficultyChoiceBox.getValue());
    Stage newStage = new Stage();
    Scene scene = new Scene(new SpaceInvadersScreen(stage, gameConfiguration), 1280, 720);
    newStage.setScene(scene);
    newStage.setResizable(false);
    newStage.setTitle("Space Invaders");
    newStage.getIcons().add(new Image(getClass()
            .getResource("/images/spaceinvaders/alien.png").toExternalForm()));
    newStage.show();
    logger.info("SpaceInvaders Game started");
  }

}
