package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.ScreenManager;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import javafx.scene.layout.Pane;

public class SimpleScreenManager implements ScreenManager {

  Pane pane;
  private SimpleSelectScreen selectScreen;
  private P1Screen p1Screen;
  private P2Screen p2Screen;

  public SimpleScreenManager(final Pane pane) {
    this.pane = pane;
  }

  private SimpleSelectScreen getSelectScreen() {
    if(selectScreen == null) {
      selectScreen = new SimpleSelectScreen();
    }
    return selectScreen;
  }

  private P1Screen getP1Screen() {
    if(p1Screen == null) {
      p1Screen = new P1Screen();
    }
    return p1Screen;
  }

  private P2Screen getP2Screen() {
    if(p2Screen == null) {
      p2Screen = new P2Screen();
    }
    return p2Screen;
  }

  @Override
  public void switchTo(String fromScreen, String toScreen) throws UnknownTransitionException {
    switch (toScreen) {
      case SimpleSelectScreen.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getSelectScreen());
        break;
      case P1Screen.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getP1Screen());
        break;
      case P2Screen.SCREEN_NAME:
        pane.getChildren().clear();
        pane.getChildren().add(getP2Screen());
        break;

      default: throw new UnknownTransitionException("Unknown screen: ", fromScreen, toScreen);
    }
  }
}
