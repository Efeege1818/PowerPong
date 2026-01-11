package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;

public class EndScreen extends AnchorPane {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(EndScreen.class);

  public static final String SCREEN_NAME = "EndScreen";

  private final SimpleScreenManager screenManager;

  private Integer winnerPlayerId;
  private Element winnerElement;

  @FXML private Label resultLabel;
  @FXML private Label infoLabel;
  @FXML private ImageView winnerMonsterImage;

  @FXML private Button restartButton;
  @FXML private Button exitButton;

  public EndScreen(SimpleScreenManager screenManager) {
    this.screenManager = screenManager;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EndScreen.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void initialize() {
    this.setFocusTraversable(true);

    this.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case R -> restartGame();
        case ESCAPE -> exitGame();
        default -> System.out.println("Key pressed: " + event.getCode());
      }
    });

    if (restartButton != null) {
      restartButton.setOnAction(e -> restartGame());
      restartButton.setFocusTraversable(false);
    }
    if (exitButton != null) {
      exitButton.setOnAction(e -> exitGame());
      exitButton.setFocusTraversable(false);
    }

    refresh();
    this.requestFocus();
  }

  public void setWinner(Integer playerId, Element element) {
    this.winnerPlayerId = playerId;
    this.winnerElement = element;
  }

  public void refresh() {
    if (resultLabel == null || infoLabel == null || winnerMonsterImage == null) {
      return;
    }

    infoLabel.setText("Press R to restart or ESC to exit");

    if (winnerPlayerId == null) {
      resultLabel.setText("Battle ended without a Winner");
      infoLabel.setText("Press R to restart or ESC to exit");
      winnerMonsterImage.setImage(null);
      return;
    }

    resultLabel.setText("Player " + winnerPlayerId + " wins!");
    winnerMonsterImage.setImage(loadMonsterSpriteByElement(winnerElement));

    logger.debug("EndScreen shows winner: Player {}, Element {}", winnerPlayerId, winnerElement);
  }

  @FXML
  public void onActionRestartGame() {
    restartGame();
  }

  @FXML
  public void onActionExitGame() {
    exitGame();
  }

  private void restartGame() {
    try {
      screenManager.switchTo(EndScreen.SCREEN_NAME, SelectScreen.SCREEN_NAME);
    } catch (UnknownTransitionException e) {
      throw new RuntimeException(e);
    }
  }

  private void exitGame() {
    Platform.exit();
  }

  private Image loadMonsterSpriteByElement(Element element) {
    if (element == null) {
      return null;
    }

    String resourcePath = switch (element) {
      case FIRE -> "/Monster Sprites/FeuerMon.png";
      case WATER -> "/Monster Sprites/WasserMon.png";
      case GRASS -> "/Monster Sprites/PflanzeMon.png";
      default -> null;
    };

    if (resourcePath == null) {
      return null;
    }

    try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
      if (in == null) {
        logger.warn("Sprite not found: {}", resourcePath);
        return null;
      }
      return new Image(in);
    } catch (IOException ex) {
      logger.warn("Failed loading sprite: {}", resourcePath, ex);
      return null;
    }
  }
}
