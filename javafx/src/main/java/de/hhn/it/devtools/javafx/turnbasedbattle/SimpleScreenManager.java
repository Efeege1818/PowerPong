package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.ScreenManager;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class SimpleScreenManager implements ScreenManager {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleScreenManager.class);

  private final StackPane pane;
  private SelectScreen selectScreen;
  private BattleScreen battleScreen;
  private EndScreen endScreen;
  private PauseScreenFx pauseScreen;
  private InfoScreenFx infoScreen;

  private SimpleTurnBasedBattleService pendingBattleService;
  private Integer pendingWinnerPlayerId;
  private Element pendingWinnerElement;
  private InfoScreenViewModel infoViewModel;
  private PauseScreenViewModel pauseViewModel;


  public SimpleScreenManager(final StackPane pane) {
    logger.info("SimpleScreenManager: initializing SimpleScreenManager");

    this.pane = pane;
  }

  private SelectScreen getSelectScreen() {
    logger.debug("SimpleScreenManager: getSelectScreen");

    if(selectScreen == null) {
      selectScreen = new SelectScreen(this);
    }

    return selectScreen;
  }

  private BattleScreen getBattleScreen() {
    logger.debug("SimpleScreenManager: getBattleScreen");

    if (pendingBattleService == null) {
      throw new IllegalStateException("No pending battle service. Call setPendingBattleService(service) before switching to BattleScreen.");
    }
    SimpleTurnBasedBattleService service = pendingBattleService;
    pendingBattleService = null; // consume
    return new BattleScreen(this, service);
  }


  private EndScreen getEndScreen() {
    logger.debug("SimpleScreenManager: getEndScreen, winnerPlayerId = {}, winnerElement = {}",
            pendingWinnerPlayerId, pendingWinnerElement);

    EndScreen end = new EndScreen(this);
    if (pendingWinnerPlayerId != null) {
      end.setWinner(pendingWinnerPlayerId, pendingWinnerElement);
    }
    return end;
  }

  private InfoScreenFx getInfoScreen() {
    logger.debug("SimpleScreenManager: getInfoScreen");

    return new InfoScreenFx(this, infoViewModel);
  }

  private PauseScreenFx getPauseScreen() {
    logger.debug("SimpleScreenManager: getPauseScreen");

    if(pauseScreen == null) {
      pauseScreen = new PauseScreenFx(this, pauseViewModel);
    }
    return pauseScreen;
  }

  @Override
  public void switchTo(String fromScreen, String toScreen) throws UnknownTransitionException {
    logger.info("switchTo: fromScreen={}, toScreen={}", fromScreen, toScreen);

    switch (toScreen) {
      case SelectScreen.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getSelectScreen());
        break;
      case BattleScreen.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getBattleScreen());
        break;
      case EndScreen.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getEndScreen());
        break;
      case InfoScreenFx.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getInfoScreen());
        break;
      case PauseScreenFx.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getPauseScreen());
        break;

      default: throw new UnknownTransitionException("Unknown screen: ", fromScreen, toScreen);
    }
  }

  public void switchToInfo(SimpleMonster monster) {
    logger.info("SimpleScreenManager: switchToInfo, monster = {}",
            monster.getName());

    infoViewModel = new InfoScreenViewModel(monster);
    pane.getChildren().clear();
    pane.getChildren().add(getInfoScreen());
  }

  public void switchToPause(SimpleMonster monster1, SimpleMonster monster2) {
    logger.info("SimpleScreenManager: switchToPause, monster1 = {}, monster2 = {}",
            monster1.getName(), monster2.getName());

    pauseViewModel = new PauseScreenViewModel(monster1, monster2);
    pane.getChildren().clear();
    pane.getChildren().add(getPauseScreen());
  }

  public void setPendingBattleService(SimpleTurnBasedBattleService service) {
    logger.info("SimpleScreenManager: setPendingBattleService");

    this.pendingBattleService = service;
  }
  // NEU: wird vom BattleScreenController aufgerufen
  public void setPendingWinner(int playerId, Element element) {
    logger.info("SimpleScreenManager: setPendingWinner, playerId = {}, element = {}",
            playerId, element);

    this.pendingWinnerPlayerId = playerId;
    this.pendingWinnerElement = element;
  }

}
