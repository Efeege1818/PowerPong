package de.hhn.it.devtools.javafx.spaceinvaders.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for SpaceInvaders Game Screen.
 */
public class SpaceInvadersScreen extends AnchorPane implements Initializable {
  private static final Logger logger = LoggerFactory.getLogger(SpaceInvadersScreen.class);

  @FXML
  public Label score;

  @FXML
  public Label level;

  @FXML
  public ImageView settings;

  @FXML
  public Canvas canvas;

  /**
   * Constructor for GameScreen.
   */
  public SpaceInvadersScreen() {

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/spaceinvaders/SpaceInvadersGame.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException exception) {
      logger.warn("Something went wrong {}", exception.getMessage());
    }

  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    settings.setImage(new Image(getClass().getResource("/images/spaceinvaders/setting.png").toExternalForm()));
    settings.setOnMouseClicked((m) -> {
    });

    testAnimation();
  }


  private void testAnimation() {
    GraphicsContext gc = canvas.getGraphicsContext2D();

    Image alien = new Image(getClass().getResource("/images/spaceinvaders/alien.png").toExternalForm());


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
  }
}
