package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.javafx.spaceinvaders.view.SpaceInvadersScreen;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpaceInvaders Controller class.
 */
public class SpaceInvadersController extends Controller implements Initializable {
  private static final Logger logger = LoggerFactory.getLogger(SpaceInvadersController.class);
  private static int counter = 0;

  @FXML
  AnchorPane spaceInvadersPane;

  @FXML
  Button spaceInvadersButton;

  @FXML
  ChoiceBox<Difficulty> difficultyChoiceBox;

  @FXML
  ImageView logo;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Media media = new Media(getClass().getResource("/spaceinvaders/sounds/"
            + Sound.CHICKEN.getSound()).toString());
    MediaPlayer mediaPlayer = new MediaPlayer(media);
    difficultyChoiceBox.setValue(Difficulty.NORMAL);
    difficultyChoiceBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
    logo.setImage(new Image(getClass()
            .getResource("/spaceinvaders/images/logo.png").toExternalForm()));

    AudioClip audioClip = new AudioClip(getClass().getResource("/spaceinvaders/sounds/"
            + Sound.GRRR.getSound()).toExternalForm());
    logo.setOnMouseClicked((mouseEvent -> {
      if (counter == 5) {
        return;
      } else if (counter < 3) {
        TranslateTransition wobble = new TranslateTransition(Duration.millis(80), logo);
        wobble.setFromX(-1);    // Links 1px
        wobble.setToX(1);       // Rechts 1px
        wobble.setAutoReverse(true);
        wobble.setCycleCount(14);
        audioClip.setVolume(0.05);
        audioClip.play();
        wobble.play();
        counter++;
      } else {
        counter++;
        RotateTransition rotate = new RotateTransition(mediaPlayer.getTotalDuration(), logo);
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(1);
        mediaPlayer.setVolume(0.05);
        mediaPlayer.play();
        rotate.play();
        mediaPlayer.setOnEndOfMedia(() -> {
          counter = 0;
          logo.setRotate(0);
          mediaPlayer.stop();
        });
      }
    }));
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
            .getResource("/spaceinvaders/images/logo.png").toExternalForm()));
    newStage.show();
    logger.debug("SpaceInvaders Game started");
  }

  @Override
  void pause() {
    logger.debug("pause: SpaceInvadersConfiguration is pausing from stage ...");
  }

  @Override
  public void resume() {
    logger.debug("resume: SpaceInvadersConfiguration is back on stage ...");
  }

  @Override
  public void shutdown() {
    logger.debug("shutdown: - SpaceInvadersConfiguration is shutting down ...");
  }
}
