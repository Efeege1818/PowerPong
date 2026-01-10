package de.hhn.it.devtools.javafx.towerdefense.controllers;

import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ScreenManager extends Parent {

  private final TowerDefenseViewModel viewModel;

  public ScreenManager (TowerDefenseViewModel viewModel) {
    this.viewModel = viewModel;
  }

  public TowerDefenseViewModel getViewModel() {
    return viewModel;
  }

  public void addScreen(ScreenType screenType, Node node) {
    screensNodeMap.put(screenType, node);
  }

  public void switchTo(ScreenType screenType) {
    this.getChildren().clear();

    this.getChildren().add(screensNodeMap.get(screenType));
  }

  private static Map<ScreenType, Node> screensNodeMap;

}
