package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.view.SpaceInvadersScreen;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
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
  private final Logger logger = LoggerFactory.getLogger(SpaceInvadersController.class);
  private final MediaPlayer chickenTrack = new MediaPlayer(new Media(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.CHICKEN.getSound()).toString()));
  private final AudioClip grrSound = new AudioClip(getClass().getResource("/spaceinvaders/sounds/"
          + Sound.GRRR.getSound()).toExternalForm());
  private TranslateTransition wobble;
  private RotateTransition rotate;
  private int counter = 0;

  @FXML
  AnchorPane spaceInvadersPane;

  @FXML
  Button spaceInvadersButton;

  @FXML
  ChoiceBox<Difficulty> difficultyChoiceBox;

  @FXML
  ImageView logo;

  @FXML
  Label infoLabel;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setBackground();
    loadAnimations();
    difficultyChoiceBox.setValue(Difficulty.NORMAL);
    infoLabel.setText(Difficulty.NORMAL.getInfoText());
    difficultyChoiceBox.setItems(FXCollections.observableArrayList(Difficulty.values()));
    difficultyChoiceBox.setOnAction(event -> infoLabel
            .setText(difficultyChoiceBox.getValue().getInfoText()));
    logo.setImage(new Image(getClass()
            .getResource("/spaceinvaders/images/logo.png").toExternalForm()));

    logo.setOnMouseClicked((mouseEvent -> {
      if (counter == 5) {
        this.rotate.play();
        this.chickenTrack.play();
        counter = -1;
      } else if (counter >= 0) {
        counter++;
        this.wobble.play();
        this.grrSound.play();
      }
    }));
  }

  /**
   * start button.
   */
  public void doActionStart() {
    Stage stage = (Stage) spaceInvadersPane.getScene().getWindow();
    stage.close();
    stopAnimations();
    GameConfiguration gameConfiguration = new GameConfiguration(3, difficultyChoiceBox.getValue());
    Stage newStage = new Stage();
    Scene scene = new Scene(new SpaceInvadersScreen(stage, gameConfiguration, newStage), 1280, 720);
    newStage.setScene(scene);
    newStage.setResizable(false);
    newStage.setTitle("Space Invaders");
    newStage.getIcons().add(Images.logo.getImage());
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

  private void setBackground() {
    BackgroundSize bgSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO,
            true, true, false, true);
    BackgroundImage bgImage = new BackgroundImage(
            Images.background.getImage(),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            bgSize
    );
    spaceInvadersPane.setBackground(new Background(bgImage));
  }

  private void loadAnimations() {
    this.wobble = new TranslateTransition(Duration.millis(80), logo);
    this.wobble.setFromX(-1);
    this.wobble.setToX(1);
    this.wobble.setAutoReverse(true);
    this.wobble.setCycleCount(14);

    this.rotate = new RotateTransition(Duration.seconds(35), logo);
    this.rotate.setByAngle(360);
    this.rotate.setInterpolator(Interpolator.LINEAR);
    this.rotate.setCycleCount(1);

    this.chickenTrack.setVolume(0.05);
    this.grrSound.setVolume(0.05);

    this.chickenTrack.setOnEndOfMedia(() -> {
      counter = 0;
      this.rotate.stop();
      logo.setRotate(0);
      this.chickenTrack.stop();
    });
  }

  private void stopAnimations() {
    this.wobble.stop();
    this.rotate.stop();
    logo.setRotate(0);
    this.chickenTrack.stop();
    counter = 0;
  }

}
