package de.hhn.it.devtools.javafx.controllers;


import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import java.net.URL;
import java.util.ResourceBundle;

import de.hhn.it.devtools.javafx.spaceinvaders.view.SpaceInvadersScreen;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

  /**
   * Constructor.
   */
  public SpaceInvadersController() {

  }


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

    Stage newStage = new Stage();

    Scene scene = new Scene(new SpaceInvadersScreen(stage), 1280, 720);
    newStage.setOnCloseRequest((event) -> stage.show());
    newStage.setScene(scene);
    newStage.setResizable(false);
    newStage.setTitle("Space Invaders");
    newStage.show();
  }

}
