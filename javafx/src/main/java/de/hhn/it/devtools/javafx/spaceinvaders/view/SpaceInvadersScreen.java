package de.hhn.it.devtools.javafx.spaceinvaders.view;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.PopupProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
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
  private final Image projectile;
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
    this.alien = loadImageSafe("/spaceinvaders/images/alien.png", APIConstants.HITBOX_SIZE,
            APIConstants.HITBOX_SIZE);
    this.ship = loadImageSafe("/spaceinvaders/images/ship.png", APIConstants.HITBOX_SIZE,
            APIConstants.HITBOX_SIZE);
    this.barrier = loadImageSafe("/spaceinvaders/images/barrier.png",
            APIConstants.BARRIER_HITBOX_WIDTH, APIConstants.BARRIER_HITBOX_HEIGHT);
    this.projectile = loadImageSafe("/spaceinvaders/images/shot.png",
            APIConstants.SHOT_HITBOX_SIZE, APIConstants.SHOT_HITBOX_SIZE);

    score.textProperty().bind(viewModel.getScoreProperty().asString());
    level.textProperty().bind(viewModel.getCurrentRoundProperty().asString());
    viewModel.getShipObjectPropertyProperty().addListener((obs, oldShip, newShip) -> {
        drawEntity(this.ship, newShip.coordinate(), APIConstants.HITBOX_SIZE,
                APIConstants.HITBOX_SIZE);
    });
    viewModel.getBarriers().addListener((MapChangeListener<Integer, Barrier>) barrier -> {
      if (barrier.wasAdded()) {
        int size = APIConstants.BARRIER_HITBOX_HEIGHT * APIConstants.BARRIER_HITBOX_WIDTH;
        Barrier bar = barrier.getValueAdded();
        if (bar.barrierId() > size * 3) {
          return; // invalid barrier id, skip drawing
        } else if (bar.barrierId() > size * 2) {
          int s = bar.barrierId() - (size * 2);
          int x = s % APIConstants.BARRIER_HITBOX_WIDTH;
          int y = s / APIConstants.BARRIER_HITBOX_WIDTH;
          Color color = this.barrier.getPixelReader().getColor(x, y);
            drawEntity(color, bar.coordinate());
            System.out.println("Draw barrier part at " + bar.coordinate().x() + ", "
                    + bar.coordinate().y() + " with color " + color);
        } else if (bar.barrierId() > size) {
          int s = bar.barrierId() - (size);
          int x = s % APIConstants.BARRIER_HITBOX_WIDTH;
          int y = s / APIConstants.BARRIER_HITBOX_WIDTH;
          Color color = this.barrier.getPixelReader().getColor(x, y);
            drawEntity(color, bar.coordinate());
          System.out.println("Draw barrier part at " + x + ", "
                  + y + " with color " + color);
        } else {
          int s = bar.barrierId();
          int x = s % APIConstants.BARRIER_HITBOX_WIDTH;
          int y = s / APIConstants.BARRIER_HITBOX_WIDTH;
          Color color = this.barrier.getPixelReader().getColor(x, y);
            drawEntity(color, bar.coordinate());
          System.out.println("Draw barrier part at " + bar.coordinate().x() + ", "
                  + bar.coordinate().y() + " with color " + color);
        }
      } else if (barrier.wasRemoved()) {
        clearEntity(barrier.getValueRemoved().coordinate(), 1, 1);
      }
    });
    viewModel.getAliens().addListener((MapChangeListener<Integer, Alien>)  change -> {
      if (dummyAlien != null) {
        drawEntity(this.alien, dummyAlien.coordinate(), APIConstants.HITBOX_SIZE,
                APIConstants.HITBOX_SIZE);
        dummyAlien = null;
      }
      if (change.wasAdded() && change.wasRemoved()) {
        clearEntity(change.getValueRemoved().coordinate(), APIConstants.HITBOX_SIZE,
                APIConstants.HITBOX_SIZE);
        drawEntity(this.alien, change.getValueAdded().coordinate(), APIConstants.HITBOX_SIZE,
                APIConstants.HITBOX_SIZE);
      } else if (change.wasAdded()) {
        drawEntity(this.alien, change.getValueAdded().coordinate(), APIConstants.HITBOX_SIZE,
                APIConstants.HITBOX_SIZE);
      } else if (change.wasRemoved()) {
        clearEntity(change.getValueRemoved().coordinate(), APIConstants.HITBOX_SIZE,
                APIConstants.HITBOX_SIZE);
      }
    });
    viewModel.getProjectiles().addListener((MapChangeListener<Integer, Projectile>) change -> {
      if (change.wasAdded() && change.wasRemoved()) {
        clearEntity(change.getValueRemoved().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
                APIConstants.SHOT_HITBOX_SIZE);
        drawEntity(this.projectile, change.getValueAdded().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
                APIConstants.SHOT_HITBOX_SIZE);
      } else if (change.wasAdded()) {
        drawEntity(this.projectile, change.getValueAdded().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
                APIConstants.SHOT_HITBOX_SIZE);
      } else if (change.wasRemoved()) {
        clearEntity(change.getValueRemoved().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
                APIConstants.SHOT_HITBOX_SIZE);
      }
    });

    viewModel.getGameStateObjectProperty().addListener((obs, oldState, newState) -> {
      if (newState == GameState.ABORTED) {
        openEndingPopup();
      } else if (newState == GameState.PAUSED) {
        if (viewModel.getAliens().isEmpty()) {
          openNextRoundPopup();
        } else {
          openSettingsPopup();
        }
      }
    });
  }

  private Image loadImageSafe(String path, int width, int height) {
    try {
      URL res = getClass().getResource(path);
      if (res == null) {
        logger.warn("Resource not found: {}", path);
        // return a small transparent image to avoid NPEs in draw calls
        return new Image(TRANSPARENT_PNG_DATA, width, height, true, true);
      }
      return new Image(res.toExternalForm(), width, height, true, true);
    } catch (Exception e) {
      logger.warn("Failed to load image {}: {}", path, e.getMessage());
      return new Image(TRANSPARENT_PNG_DATA, width, height, true, true);
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // settings ImageView might be null if FXML failed earlier; we validated in ctor
    settings.setImage(loadImageSafe("/spaceinvaders/images/setting.png", 25, 25));
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
    gc.drawImage(image, coordinate.x() + 0.5, coordinate.y() + 0.5, a, b);
  }

  private void drawEntity(Color color, Coordinate coordinate) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(coordinate.x(), coordinate.y(), 1, 1);
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
