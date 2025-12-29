package de.hhn.it.devtools.javafx.spaceinvaders.view;

import de.hhn.it.devtools.apis.spaceinvaders.*;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.PopupProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
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
  // 1x1 transparent PNG as data URL to avoid NPEs when images are missing
  private static final String TRANSPARENT_PNG_DATA =
      "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAA"
              + "AC1HAwCAAAAC0lEQVQImWNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=";

  private final Stage mainStage;
  private final SpaceInvadersService spaceInvadersService;
  private final SpaceInvadersViewModel viewModel;
  private final Image alien;
  private final Image ship;
  private final Image barrier;
  private Stage settingsStage;
  private Stage startStage;
  private Stage nextRoundStage;
  private Alien dummyAlien;

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

    // safe image loading after FXML load (so logger available)
    this.alien = loadImageSafe("/images/spaceinvaders/alien.png");
    this.ship = loadImageSafe("/images/spaceinvaders/ship.png");
    this.barrier = loadImageSafe("/images/spaceinvaders/barrier.png");

    score.textProperty().bind(viewModel.getScoreProperty().asString());
    level.textProperty().bind(viewModel.getCurrentRoundProperty().asString());
    viewModel.getCurrentRoundProperty().addListener(((observableValue, oldRound, newRound) ->
            openNextRoundPopup()));
    viewModel.getAliens().addListener((MapChangeListener<Integer, Alien>)  change -> {
      if (dummyAlien != null) {
        drawEntity(this.alien, dummyAlien.coordinate(), APIConstants.ALIEN_HITBOX_SIZE,
                APIConstants.ALIEN_HITBOX_SIZE);
        dummyAlien = null;
      }
      if (change.wasAdded() && change.wasRemoved()) {
        clearEntity(change.getValueRemoved().coordinate(), APIConstants.ALIEN_HITBOX_SIZE,
                APIConstants.ALIEN_HITBOX_SIZE);
        drawEntity(this.alien, change.getValueAdded().coordinate(), APIConstants.ALIEN_HITBOX_SIZE,
                APIConstants.ALIEN_HITBOX_SIZE);
      } else if (change.wasAdded()) {
        drawEntity(this.alien, change.getValueAdded().coordinate(), APIConstants.ALIEN_HITBOX_SIZE,
                APIConstants.ALIEN_HITBOX_SIZE);
      } else if (change.wasRemoved()) {
        clearEntity(change.getValueRemoved().coordinate(), APIConstants.ALIEN_HITBOX_SIZE,
                APIConstants.ALIEN_HITBOX_SIZE);
        dummyAlien = change.getValueRemoved();
      }
    });
    viewModel.getBarriers().addListener((InvalidationListener) change -> drawCanvas());
    viewModel.getProjectiles().addListener((InvalidationListener) change -> drawCanvas());
    viewModel.getShipObjectPropertyProperty().addListener(change -> drawCanvas());
    viewModel.getGameStateObjectProperty().addListener((obs, oldState, newState) -> {
      if (newState == GameState.ABORTED) {
        openEndingPopup();
      } else if (newState == GameState.PAUSED) {
        openSettingsPopup();
      }
    });
  }

  private Image loadImageSafe(String path) {
    try {
      URL res = getClass().getResource(path);
      if (res == null) {
        logger.warn("Resource not found: {}", path);
        // return a small transparent image to avoid NPEs in draw calls
        return new Image(TRANSPARENT_PNG_DATA);
      }
      return new Image(res.toExternalForm());
    } catch (Exception e) {
      logger.warn("Failed to load image {}: {}", path, e.getMessage());
      return new Image(TRANSPARENT_PNG_DATA);
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // settings ImageView might be null if FXML failed earlier; we validated in ctor
    settings.setImage(loadImageSafe("/images/spaceinvaders/setting.png"));
    settings.setOnMouseClicked((m) -> spaceInvadersService.pause());

    Platform.runLater(() -> {
      this.openStartPopup();
      getScene().getWindow().setOnCloseRequest((e) -> {
        spaceInvadersService.removeListener(viewModel);
        this.mainStage.show();
        spaceInvadersService.abort();
      });
    });

  }

  private void clearEntity(Coordinate coordinate, int a, int b) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(coordinate.x(), coordinate.y(), a, b);
  }

  private void drawEntity(Image image, Coordinate coordinate, int a, int b) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(image, coordinate.x(), coordinate.y(), a, b);
  }

  private void drawCanvas() {
    if (canvas == null) {
      return;
    }
    GraphicsContext gc = canvas.getGraphicsContext2D();

    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    if (!viewModel.getAliens().isEmpty()) {
      viewModel.getAliens().values().forEach((a) ->
            gc.drawImage(alien, a.coordinate().x(), a.coordinate().y(),
                    APIConstants.ALIEN_HITBOX_SIZE, APIConstants.ALIEN_HITBOX_SIZE));
    }

    if (!viewModel.getBarriers().isEmpty()) {
      viewModel.getBarriers().values().forEach((b) ->
              gc.drawImage(barrier, b.coordinate().x(), b.coordinate().y(),
                      APIConstants.ALIEN_HITBOX_SIZE, APIConstants.ALIEN_HITBOX_SIZE));
    }

    if (!viewModel.getProjectiles().isEmpty()) {
      viewModel.getProjectiles().values().forEach((p) ->
              gc.fillOval(p.coordinate().x(), p.coordinate().y(), 1, 1));
    }

    if (viewModel.getShipObjectPropertyProperty().get() != null) {
      Ship player = viewModel.getShipObjectPropertyProperty().get();
      gc.drawImage(ship, player.coordinate().x(), player.coordinate().y(),
              APIConstants.ALIEN_HITBOX_SIZE, APIConstants.ALIEN_HITBOX_SIZE);
    }

  }

  private void openSettingsPopup() {
    if (settingsStage == null) {
      createPopup();
    }
    settingsStage.showAndWait();
  }

  private void openStartPopup() {
    if (startStage == null) {
      createPopup();
    }
    startStage.showAndWait();
  }

  private void openNextRoundPopup() {
    if (nextRoundStage == null) {
      createPopup();
    }
    nextRoundStage.showAndWait();
  }

  private void openEndingPopup() {
    String finalScore = score.getText();
    String finalLevel = level.getText();

    Stage owner = (Stage) getScene().getWindow();
    new PopupProvider(owner)
      .setTitle("Game Over")
      .addLabel("Your Score")
      .addLabel(finalScore)
      .addLabel("Reached Level")
      .addLabel(finalLevel)
      .addButton((e) -> {
        spaceInvadersService.removeListener(viewModel);
        owner.close();
        mainStage.show();
      }, "Quit")
      .setCloseRequest((e) -> {
        owner.close();
        mainStage.show();
      }).build().showAndWait();
  }

  private void createPopup() {
    Stage owner = (Stage) getScene().getWindow();

    // Settings Popup.
    this.settingsStage = new PopupProvider(owner)
      .setTitle("Settings")
      .addButton((e) -> spaceInvadersService.resume(), "Resume")
      .addButton((e) -> spaceInvadersService.abort(), "Quit")
      .setCloseRequest((e) -> spaceInvadersService.resume()).build();

    // Start Popup.
    this.startStage = new PopupProvider(owner)
      .setTitle("SpaceInvaders")
      .addButton((e) -> spaceInvadersService.start(), "Start Game")
      .addButton((e) -> {
        spaceInvadersService.removeListener(viewModel);
        spaceInvadersService.abort();
        owner.close();
        mainStage.show();
      }, "Quit")
      .setCloseRequest((e) -> {
        spaceInvadersService.removeListener(viewModel);
        spaceInvadersService.abort();
        owner.close();
        mainStage.show();
      }).build();

    // Next Round Popup.
    this.nextRoundStage = new PopupProvider(owner)
      .setTitle("Level Complete")
      .addButton((e) -> spaceInvadersService.nextRound(), "Next Level")
      .addButton((e) -> {
        spaceInvadersService.abort();
        mainStage.show();
      }, "Quit")
      .setCloseRequest((e) -> {
        spaceInvadersService.abort();
        owner.close();
      }).build();
  }

}
