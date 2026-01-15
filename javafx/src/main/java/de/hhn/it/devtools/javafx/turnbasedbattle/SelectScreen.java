package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SelectScreen extends AnchorPane {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(SelectScreen.class);

  public static final String SCREEN_NAME = "SelectScreen";

  private final SimpleScreenManager screenManager;
  private boolean selected1 = false;
  private boolean selected2 = false;
  private Monster p1Monster;
  private Monster p2Monster;
  Data data = new Data();

  public SelectScreen(SimpleScreenManager screenManager) {
    this.screenManager = screenManager;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SelectScreen.fxml"));
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
        case A -> monsterForP1(data.getMonsters()[0]);  //FireMonster for P1
        case S -> monsterForP1(data.getMonsters()[2]);  //WaterMonster for P1
        case D -> monsterForP1(data.getMonsters()[1]);  //GrassMonster for P1
        case J -> monsterForP2(data.getMonsters()[0]);  //FireMonster for P2
        case K -> monsterForP2(data.getMonsters()[2]);  //WaterMonster for P2
        case L -> monsterForP2(data.getMonsters()[1]);  //GrassMonster for P2
        default -> System.out.println("Key pressed: " + event.getCode());
      }
    });

    this.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene != null) {
        javafx.application.Platform.runLater(this::requestFocus);
      }
    });

    this.requestFocus();
  }

  @FXML
  public void onActionExitGame() {
    javafx.application.Platform.exit();
  }

  @FXML
  public void openFireInfo() {
    screenManager.switchToInfo(SimpleMonster.create(data.getMonsters()[0]));
  }

  @FXML
  public void openWaterInfo() {
    screenManager.switchToInfo(SimpleMonster.create(data.getMonsters()[2]));
  }

  @FXML
  public void openGrassInfo() {
    screenManager.switchToInfo(SimpleMonster.create(data.getMonsters()[1]));
  }

  private void monsterForP1(Monster monster) {
    if(!selected1) {
      p1Monster = monster;
      selected1 = true;
      logger.debug("Player 1 selected: " + monster.element() + "MONSTER");
      checkSelectionFinished();
    }
  }

  private void monsterForP2(Monster monster) {
    if(!selected2) {
      p2Monster = monster;
      selected2 = true;
      logger.debug("Player 2 selected: " + monster.element() + "MONSTER");
      checkSelectionFinished();
    }
  }

  private void checkSelectionFinished() {
    if(isSelectionFinished()) {
      logger.debug("Both players have picked a monster. Switching screen now...");

      SimpleTurnBasedBattleService service = new SimpleTurnBasedBattleService();
      Player player1 = new Player(1, p1Monster, 0);
      Player player2 = new Player(2, p2Monster, 0);

      service.setupPlayers(player1, player2, p1Monster, p2Monster);
      service.start();

      // an ScreenManager übergeben (damit BattleScreen ihn bekommt)
      screenManager.setPendingBattleService(service);

      screenManager.switchTo(SelectScreen.SCREEN_NAME, BattleScreen.SCREEN_NAME);
    }
  }

  private boolean isSelectionFinished() {
    return selected1 && selected2;
  }

  public Monster getP1Monster() {
    return p1Monster;
  }

  public Monster getP2Monster() {
    return p2Monster;
  }
}
