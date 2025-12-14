package de.hhn.it.devtools.javafx.controllers;


import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import java.net.URL;
import java.util.ResourceBundle;

import de.hhn.it.devtools.javafx.Main;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpaceInvaders Controller class.
 */
public class SpaceInvadersController extends Controller implements Initializable {
  private static final Logger logger = LoggerFactory.getLogger(SpaceInvadersController.class);
  private final SpaceInvadersService spaceInvadersService;

  @FXML
  AnchorPane spaceInvadersPane;

  @FXML
  Button spaceInvadersButton;

  /**
   * Constructor.
   */
  public SpaceInvadersController() {
    this.spaceInvadersService = new SimpleSpaceInvadersService();
  }


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.spaceInvadersButton.setOnAction((event) -> doActionStart());
  }

  /**
   * start button.
   */
  public void doActionStart() {
    Stage stage = (Stage) spaceInvadersPane.getScene().getWindow();
    stage.close();

    Stage stage2 = new Stage();

    AnchorPane anchorPane = new AnchorPane();

    Canvas canvas = new Canvas(256, 256);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    Image alien = new Image(getClass().getResource("/images/alien.png").toExternalForm());


    new AnimationTimer() {

      double x = 56;      // Startposition links
      double y = 100;        // Höhe konstant
      double speedX = 1;     // Geschwindigkeit
      boolean movingRight = true; // Richtung

      @Override
      public void handle(long now) {

        // Richtung prüfen und Position ändern
        if (movingRight) {
          x += speedX;
          if (x + 10 >= 200) {  // rechts angekommen
            movingRight = false;
          }
        } else {
          x -= speedX;
          if (x <= 56) {        // links angekommen
            movingRight = true;
          }
        }

        gc.clearRect(0, 0, 256, 256);

        // Alien 10x10 Pixel zeichnen
        for (int i = 0; i < 5; i++) {
          gc.drawImage(alien, x + i * 10, y, 10, 10);
          gc.drawImage(alien, x - i * 10, y, 10, 10);
        }
      }
    }.start();

    AnchorPane.setLeftAnchor(canvas, (1080 - 256) / 2.0);
    AnchorPane.setTopAnchor(canvas,  (720  - 256) / 2.0);

    anchorPane.getChildren().add(canvas);

    Scene scene = new Scene(anchorPane, 1080, 720);
    stage2.setOnCloseRequest((event) -> stage.show());
    stage2.setScene(scene);
    stage2.setResizable(false);
    stage2.setTitle("Space Invaders");
    stage2.show();
  }

}
