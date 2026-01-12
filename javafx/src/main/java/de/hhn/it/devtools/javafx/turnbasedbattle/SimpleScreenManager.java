package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.ScreenManager;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import javafx.scene.layout.Pane;

public class SimpleScreenManager implements ScreenManager {

  Pane pane;
  private SelectScreen selectScreen;
  private BattleScreen battleScreen;
  private EndScreen endScreen;
  private PauseScreenFx pauseScreen;
  private InfoScreenFx infoScreen;

  public SimpleScreenManager(final Pane pane) {
    this.pane = pane;
  }

  private SelectScreen getSelectScreen() {
    if(selectScreen == null) {
      selectScreen = new SelectScreen(this);
    }
    return selectScreen;
  }

  private BattleScreen getBattleScreen() {
    if(battleScreen == null) {
      battleScreen = new BattleScreen(this);
    }
    return battleScreen;
  }

  private EndScreen getEndScreen() {
    if(endScreen == null) {
      endScreen = new EndScreen(this);
    }
    return endScreen;
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
}
