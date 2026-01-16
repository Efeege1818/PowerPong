package de.hhn.it.devtools.javafx.towerdefense.controllers;

import de.hhn.it.devtools.apis.towerdefense.TowerDefenseService;
import de.hhn.it.devtools.components.towerdefense.SimpleTowerDefenseService;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class that starts the TowerDefense Game.
 */
public class TowerDefenseLauncher {

  TowerDefenseService service;

  /**
   * Launches the Tower Defense Game.
   */
  public static void launch() {
    TowerDefenseLauncher launcher = new TowerDefenseLauncher();
    launcher.start(new Stage());
  }

  private void start(Stage primaryStage) {
    service = new SimpleTowerDefenseService();
    TowerDefenseViewModel viewModel = new TowerDefenseViewModel(service);
    ScreenManager screenManager = new ScreenManager(viewModel);
    screenManager.switchTo(ScreenType.TITLE_SCREEN);
    primaryStage.setOnHiding((event) -> {
      service.terminate();
    });
    primaryStage.setMaximized(true);
    primaryStage.setScene(new Scene(screenManager));
    primaryStage.setTitle("Tower Defense");
    primaryStage.show();
  }
}
