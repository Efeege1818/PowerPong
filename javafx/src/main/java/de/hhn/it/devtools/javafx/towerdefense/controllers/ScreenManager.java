package de.hhn.it.devtools.javafx.towerdefense.controllers;

import de.hhn.it.devtools.javafx.towerdefense.view.GameScreen;
import de.hhn.it.devtools.javafx.towerdefense.view.TitleScreen;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ScreenManager extends Parent {

  private final TowerDefenseViewModel viewModel;
  private static Map<ScreenType, Node> screensNodeMap;

  public ScreenManager(TowerDefenseViewModel viewModel) {
    this.viewModel = viewModel;
    screensNodeMap = new HashMap<>();
  }

  public TowerDefenseViewModel getViewModel() {
    return viewModel;
  }

  /**
   * Switches screen when needed.
   *
   * @param screenType of the screen we need to see.
   */
  public void switchTo(ScreenType screenType) {
    this.getChildren().clear();
    Node newChild = screensNodeMap.get(screenType);
    if (Objects.isNull(newChild)) {
      switch (screenType) {
        case GAME_SCREEN -> newChild = new GameScreen(this);
        case TITLE_SCREEN -> newChild = new TitleScreen(this);
        default -> {
          throw new IllegalArgumentException("Screen Type does not exist");
        }
      }
      screensNodeMap.put(screenType, newChild);
    }
    this.getChildren().add(newChild);
  }
}
