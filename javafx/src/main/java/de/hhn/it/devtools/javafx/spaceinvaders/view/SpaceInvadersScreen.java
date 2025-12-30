package de.hhn.it.devtools.javafx.spaceinvaders.view;

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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
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
  private final Stage mainStage;
  private final SpaceInvadersService spaceInvadersService;
  private final SpaceInvadersViewModel viewModel;
  private CanvasProvider canvasProvider;
  private PopupConfigurations popupConfigurations;

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
  @SuppressWarnings("checkstyle:Indentation")
  public SpaceInvadersScreen(Stage stage, GameConfiguration gameConfiguration) {
    this.spaceInvadersService = new SimpleSpaceInvadersService();
    this.viewModel = new SpaceInvadersViewModel();
    this.mainStage = stage;

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

    // FXML fields must be available after load - validate early to fail fast
    if (score == null || level == null || settings == null || canvas == null) {
      throw new IllegalStateException("FXML did not inject required controls: "
             + "score/level/settings/canvas");
    }

    score.textProperty().bind(viewModel.getScoreProperty().asString());
    level.textProperty().bind(viewModel.getCurrentRoundProperty().asString());
    viewModel.getShipObjectPropertyProperty().addListener(new ShipListener(canvasProvider));
    viewModel.getBarriers().addListener(new BarrierListener(canvasProvider));
    viewModel.getAliens().addListener(new AliensListener(canvasProvider));
    viewModel.getProjectiles().addListener(new ProjectileListener(canvasProvider));
    viewModel.getGameStateObjectProperty().addListener(new GameStateListener(
            popupConfigurations,
            viewModel));
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    settings.setImage(Images.settingsImage.getImage());
    settings.setOnMouseClicked((m) -> spaceInvadersService.pause());
    canvasProvider = new CanvasProvider(canvas);
    Platform.runLater(() -> {
      popupConfigurations = new PopupConfigurations(spaceInvadersService,
              mainStage,
              (Stage) getScene().getWindow(),
              viewModel);
      popupConfigurations.openStartPopup();
      getScene().getWindow().setOnCloseRequest((e) -> {
        spaceInvadersService.removeListener(viewModel);
        this.mainStage.show();
        spaceInvadersService.abort();
      });
    });

  }

}
