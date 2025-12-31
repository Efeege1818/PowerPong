package de.hhn.it.devtools.javafx.spaceinvaders.view;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.PopupConfigurations;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.AliensListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.BarrierListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.GameStateListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.ProjectileListener;
import de.hhn.it.devtools.javafx.spaceinvaders.listener.ShipListener;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Stage;
import javafx.util.Duration;
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
  private Timeline shooting = new Timeline(new KeyFrame(Duration.seconds(0.1)));

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
    viewModel.getShipObjectPropertyProperty().addListener(new ShipListener(canvasProvider));
    viewModel.getBarriers().addListener(new BarrierListener(canvasProvider));
    viewModel.getAliens().addListener(new AliensListener(canvasProvider));
    viewModel.getProjectiles().addListener(new ProjectileListener(new CanvasProvider(canvas1)));
    viewModel.getGameStateObjectProperty().addListener(new GameStateListener(
            popupConfigurations,
            viewModel));
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    settings.setImage(Images.settingsImage.getImage());
    settings.setOnMouseClicked((m) -> spaceInvadersService.pause());
    canvasProvider = new CanvasProvider(canvas);
    popupConfigurations = new PopupConfigurations(spaceInvadersService,
            mainStage,
            instance,
            viewModel);
    canvas.setFocusTraversable(true);
    Platform.runLater(() -> {
      popupConfigurations.openStartPopup();
      getScene().getWindow().setOnCloseRequest((e) -> {
        spaceInvadersService.removeListener(viewModel);
        this.mainStage.show();
        spaceInvadersService.abort();
      });
      Scene scene = getScene();
      scene.setOnKeyPressed(event -> {
        KeyCode code = event.getCode();
        if (code == KeyCode.LEFT) {
          onLeftPressed();
        } else if (code == KeyCode.RIGHT) {
          onRightPressed();
        } else if (code == KeyCode.SPACE) {
          onSpacePressed();
        }
      });
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

  private void onLeftPressed() {
    spaceInvadersService.move(Direction.LEFT);
  }

  private void onRightPressed() {
    spaceInvadersService.move(Direction.RIGHT);
  }

  private void onSpacePressed() {
    if (shooting.getStatus() == Animation.Status.STOPPED) {
      spaceInvadersService.shoot();
      shooting.setCycleCount(1);
      shooting.play();
    }
  }

}
