package de.hhn.it.devtools.javafx.spaceinvaders.view;

import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
          .getResource("/images/spaceinvaders/ship.png").toExternalForm());

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
            openNextRoundPopup()));
    viewModel.syncProperty().addListener((obs, oldValue, newValue) -> {
      if (newValue == true) {
        drawCanvas();
      } else {
        openEndingPopup();
      }
    });
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    settings.setImage(new Image(getClass()
            .getResource("/images/spaceinvaders/setting.png").toExternalForm()));
    settings.setOnMouseClicked((m) -> {
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

  private void testAnimation() {
    GraphicsContext gc = canvas.getGraphicsContext2D();

    Image alien = new Image(getClass()
            .getResource("/images/spaceinvaders/alien.png").toExternalForm());


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

  private void openStartPopup() {
    openPopup((e) -> spaceInvadersService.start(), "SpaceInvaders", "Start Game");
  }

  private void openNextRoundPopup() {
    openPopup((e) -> spaceInvadersService.nextRound(), "Level Complete", "Next Level");
  }

  private void openPopup(EventHandler<ActionEvent> firstButtonAction,
                         String title, String buttonName) {
    Stage popup = new Stage();
    popup.setTitle(title);

    VBox vbox = new VBox(20);
    vbox.setAlignment(Pos.CENTER);
    vbox.setPadding(new Insets(30, 30, 30, 30));

    Button firstButton = new Button(buttonName);
    firstButton.setPrefWidth(150);

    Button cancelButton = new Button("Quit");
    cancelButton.setPrefWidth(150);

    vbox.getChildren().addAll(firstButton, cancelButton);

    popup.setScene(new Scene(vbox, 220, 140));

    if (getScene() != null && getScene().getWindow() != null) {
      popup.initOwner(getScene().getWindow());
      popup.initModality(Modality.APPLICATION_MODAL);
    }

    firstButton.setOnAction(e -> {
      popup.close();
      firstButtonAction.handle(e);
    });

    cancelButton.setOnAction(e -> {
      spaceInvadersService.abort();
      popup.close();
      ((Stage) getScene().getWindow()).close();
      mainStage.show();
    });

    popup.setOnCloseRequest((e) -> {
      spaceInvadersService.abort();
      ((Stage) getScene().getWindow()).close();
      mainStage.show();
    });

    popup.showAndWait();
  }

  private void openEndingPopup() {
    Stage popup = new Stage();
    popup.setTitle("Game Over");

    VBox vbox = new VBox(15);
    vbox.setAlignment(Pos.CENTER);
    vbox.setPadding(new Insets(20));

    Label lbl1 = new Label("Your Score");
    Label lbl3 = new Label("Reached Level");

    Button quitButton = new Button("Quit");
    quitButton.setPrefWidth(150);

    vbox.getChildren().addAll(lbl1, score, lbl3, level, quitButton);

    quitButton.setOnAction(e -> {
      popup.close();
      spaceInvadersService.abort();
      ((Stage) getScene().getWindow()).close();
      mainStage.show();
    });

    popup.setOnCloseRequest((e) -> {
      spaceInvadersService.abort();
      ((Stage) getScene().getWindow()).close();
      mainStage.show();
    });

    popup.setScene(new Scene(vbox, 260, 220));
    popup.initModality(Modality.APPLICATION_MODAL);
    popup.showAndWait();
  }

}
