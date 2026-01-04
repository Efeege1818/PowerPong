package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.ScreenManager;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import javafx.scene.layout.Pane;

public class SimpleScreenManager implements ScreenManager {

  Pane pane;
  private SelectScreen selectScreen;
  private BattleScreen battleScreen;

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


      default: throw new UnknownTransitionException("Unknown screen: ", fromScreen, toScreen);
    }
  }
}
