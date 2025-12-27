package de.hhn.it.devtools.javafx.spaceinvaders.view;

import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.PopupProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
  private final Stage mainStage;
  private final SpaceInvadersService spaceInvadersService;
  private final SpaceInvadersViewModel viewModel;
  private final Image alien = new Image(getClass()
          .getResource("/images/spaceinvaders/alien.png").toExternalForm());
  private final Image ship = new Image(getClass()
          .getResource("/images/spaceinvaders/ship.png").toExternalForm());
  private final Image barrier = new Image(getClass()
          .getResource("/images/spaceinvaders/barrier.png").toExternalForm());
  private Stage settingsStage;
  private Stage startStage;
  private Stage nextRoundStage;

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
      logger.warn("Something went wrong {}", exception.getMessage());
    }

    score.textProperty().bind(viewModel.getCurrentRoundProperty().asString());
    level.textProperty().bind(viewModel.getCurrentRoundProperty().asString());
    viewModel.getCurrentRoundProperty().addListener(((observableValue, oldRound, newRound) ->
            Platform.runLater(this::openNextRoundPopup)));
    viewModel.getSyncProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue == true) {
        Platform.runLater(this::drawCanvas);
      } else {
        Platform.runLater(this::openEndingPopup);
      }
    });
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    settings.setImage(new Image(getClass()
            .getResource("/images/spaceinvaders/setting.png").toExternalForm()));
    settings.setOnMouseClicked((m) -> {
      spaceInvadersService.pause();
      Platform.runLater(this::openSettingsPopup);
    });

    Platform.runLater(() -> {
      this.openStartPopup();
      getScene().getWindow().setOnCloseRequest((e) -> {
        spaceInvadersService.removeListener(viewModel);
        this.mainStage.show();
        spaceInvadersService.abort();
      });
    });
  }

  private void drawCanvas() {
    GraphicsContext gc = canvas.getGraphicsContext2D();

    gc.clearRect(0, 0, 512, 512);

    if (!viewModel.getAliens().isEmpty()) {
      viewModel.getAliens().values().forEach((a) ->
            gc.drawImage(alien, a.coordinate().x(), a.coordinate().y(), 25, 25));
    }

    if (!viewModel.getBarriers().isEmpty()) {
      viewModel.getBarriers().values().forEach((b) ->
              gc.drawImage(barrier, b.coordinate().x(), b.coordinate().y(), 25, 25));
    }

    if (!viewModel.getProjectiles().isEmpty()) {
      viewModel.getProjectiles().values().forEach((p) ->
              gc.fillOval(p.coordinate().x(), p.coordinate().y(), 1, 1));
    }

    if (viewModel.getShipObjectProperty() != null) {
      Ship player = viewModel.getShipObjectProperty();
      gc.drawImage(ship, player.coordinate().x(), player.coordinate().y(), 25, 25);
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

    new PopupProvider((Stage) getScene().getWindow())
            .setTitle("Game Over")
            .addLabel("Your Score")
            .addLabel(finalScore)
            .addLabel("Reached Level")
            .addLabel(finalLevel)
            .addButton((e) -> {
              spaceInvadersService.removeListener(viewModel);
              ((Stage) getScene().getWindow()).close();
              mainStage.show();
            }, "Quit")
            .setCloseRequest((e) -> {
              ((Stage) getScene().getWindow()).close();
              mainStage.show();
            }).build().showAndWait();
  }

  private void createPopup() {

    // Settings Popup.
    this.settingsStage = new PopupProvider((Stage) getScene().getWindow())
            .setTitle("Settings")
            .addButton((e) -> spaceInvadersService.resume(), "Resume")
            .addButton((e) -> spaceInvadersService.abort(), "Quit")
            .setCloseRequest((e) -> spaceInvadersService.resume()).build();

    // Start Popup.
    this.startStage = new PopupProvider((Stage) getScene().getWindow())
            .setTitle("SpaceInvaders")
            .addButton((e) -> spaceInvadersService.start(), "Start Game")
            .addButton((e) -> {
              spaceInvadersService.removeListener(viewModel);
              spaceInvadersService.abort();
              ((Stage) getScene().getWindow()).close();
              mainStage.show();
            }, "Quit")
            .setCloseRequest((e) -> {
              spaceInvadersService.removeListener(viewModel);
              spaceInvadersService.abort();
              ((Stage) getScene().getWindow()).close();
              mainStage.show();
            }).build();

    // Next Round Popup.
    this.nextRoundStage = new PopupProvider((Stage) getScene().getWindow())
            .setTitle("Level Complete")
            .addButton((e) -> spaceInvadersService.nextRound(), "Next Level")
            .addButton((e) -> {
              spaceInvadersService.abort();
              mainStage.show();
            }, "Quit")
            .setCloseRequest((e) -> {
              spaceInvadersService.abort();
              ((Stage) getScene().getWindow()).close();
            }).build();
  }

}
