package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.ScreenManager;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import javafx.scene.layout.Pane;

public class SimpleScreenManager implements ScreenManager {

  Pane pane;
  private SelectScreen selectScreen;
  private BattleScreen battleScreen;
  private EndScreen endScreen;
  private PauseScreenFx pauseScreen;
  private InfoScreenFx infoScreen;

  private SimpleTurnBasedBattleService pendingBattleService;
  private Integer pendingWinnerPlayerId;
  private Element pendingWinnerElement;


  public SimpleScreenManager(final Pane pane) {
    this.pane = pane;
  }

  private SelectScreen getSelectScreen() {
    return new SelectScreen(this);
  }

  private BattleScreen getBattleScreen() {
    if (pendingBattleService == null) {
      throw new IllegalStateException("No pending battle service. Call setPendingBattleService(service) before switching to BattleScreen.");
    }
    SimpleTurnBasedBattleService service = pendingBattleService;
    pendingBattleService = null; // consume
    return new BattleScreen(this, service);
  }


  private EndScreen getEndScreen() {
    EndScreen end = new EndScreen(this);
    if (pendingWinnerPlayerId != null) {
      end.setWinner(pendingWinnerPlayerId, pendingWinnerElement);
    }
    return end;
  }

  private InfoScreenFx getInfoScreen() {
    if(infoScreen == null) {
      infoScreen = new InfoScreenFx(null); //TODO: update InfoScreen to use ScreenManager
    }
    return infoScreen;
  }

  private PauseScreenFx getPauseScreen() {
    if(pauseScreen == null) {
      pauseScreen = new PauseScreenFx(null); //TODO: update PauseScreen to use ScreenManager
    }
    return pauseScreen;
  }

  @Override
  public void switchTo(String fromScreen, String toScreen) throws UnknownTransitionException {
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

  public void setPendingBattleService(SimpleTurnBasedBattleService service) {
    this.pendingBattleService = service;
  }
  // NEU: wird vom BattleScreenController aufgerufen
  public void setPendingWinner(int playerId, Element element) {
    this.pendingWinnerPlayerId = playerId;
    this.pendingWinnerElement = element;
  }

}
