package de.hhn.it.devtools.javafx.spaceinvaders.view;

import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.PopupConfigurations;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.KeyBoardProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.AliensListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.BarrierListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.GameStateListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.ProjectileListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.ShipListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.SoundListener;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for SpaceInvaders Game Screen.
 */
public class SpaceInvadersScreen extends AnchorPane implements Initializable {
  private static final Logger logger = LoggerFactory.getLogger(SpaceInvadersScreen.class);
  private final Stage mainStage;
  private final SpaceInvadersService spaceInvadersService;
  private final SpaceInvadersViewModel viewModel;
  private final Stage instance;
  private CanvasProvider canvasProvider;
  private PopupConfigurations popupConfigurations;
  private final MediaPlayer soundTrack = new MediaPlayer(new Media(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.TRACK.getSound()).toExternalForm()));

  @FXML
  public Label score;

  @FXML
  public Label level;

  @FXML
  public ImageView settings;

  @FXML
  public Canvas canvas;

  @FXML
  public Canvas canvas1;

  @FXML
  public ImageView leben1;

  @FXML
  public ImageView leben2;

  @FXML
  public ImageView leben3;

  /**
   * Constructor for GameScreen.
   */
  public SpaceInvadersScreen(Stage stage, GameConfiguration gameConfiguration, Stage instance) {
    this.instance = instance;
    this.spaceInvadersService = new SimpleSpaceInvadersService();
    this.viewModel = new SpaceInvadersViewModel();
    this.mainStage = stage;
    setBackground();
    this.spaceInvadersService.addListener(viewModel);
    this.spaceInvadersService.configure(gameConfiguration);

    FXMLLoader loader = new FXMLLoader(getClass()
            .getResource("/fxml/spaceinvaders/SpaceInvadersGame.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException exception) {
      logger.error("Could not load SpaceInvaders FXML: {}", exception.getMessage(), exception);
      throw new IllegalStateException("Failed to load SpaceInvaders FXML", exception);
    }

    if (score == null || level == null || settings == null || canvas == null) {
      spaceInvadersService.abort();
      mainStage.show();
      throw new IllegalStateException("FXML did not inject required controls: "
              + "score/level/settings/canvas");
    }

    score.textProperty().bind(viewModel.getScoreProperty().asString());
    level.textProperty().bind(viewModel.getCurrentRoundProperty().asString());
    viewModel.getPropertyChangeSupport().addPropertyChangeListener(new SoundListener());
    viewModel.getShipObjectPropertyProperty().addListener(new ShipListener(canvasProvider,
            leben1, leben2, leben3));
    viewModel.getBarriers().addListener(new BarrierListener(canvasProvider));
    viewModel.getAliens().addListener(new AliensListener(canvasProvider));
    viewModel.getProjectiles().addListener(new ProjectileListener(new CanvasProvider(canvas1)));
    viewModel.getGameStateObjectProperty().addListener(new GameStateListener(
            popupConfigurations,
            viewModel));
    soundTrack.setVolume(0.05);
    soundTrack.setCycleCount(MediaPlayer.INDEFINITE);
    soundTrack.play();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    settings.setImage(Images.settingsImage.getImage());
    settings.setOnMouseClicked((m) -> spaceInvadersService.pause());
    canvasProvider = new CanvasProvider(canvas);
    popupConfigurations = new PopupConfigurations(spaceInvadersService,
            mainStage,
            instance,
            viewModel,
            soundTrack);
    canvas.setFocusTraversable(true);
    Platform.runLater(() -> {
      popupConfigurations.openStartPopup();
      getScene().getWindow().setOnCloseRequest((e) -> {
        spaceInvadersService.removeListener(viewModel);
        this.mainStage.show();
        this.soundTrack.stop();
        spaceInvadersService.abort();
      });

      new KeyBoardProvider(getScene(), spaceInvadersService).start();
      canvas.requestFocus();
    });
  }

  private void setBackground() {
    BackgroundSize bgSize = new BackgroundSize(BackgroundSize.AUTO,
            BackgroundSize.AUTO, true, true, false, true);
    BackgroundImage bgImage = new BackgroundImage(
            Images.background.getImage(),
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            bgSize
    );
    this.setBackground(new Background(bgImage));
  }

}
