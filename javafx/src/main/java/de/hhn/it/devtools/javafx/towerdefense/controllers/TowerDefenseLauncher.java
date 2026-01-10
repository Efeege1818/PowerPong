package de.hhn.it.devtools.javafx.towerdefense.controllers;

import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import de.hhn.it.devtools.javafx.towerdefense.view.TitleScreen;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TowerDefenseLauncher extends Application {

  /**
   * the main method.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {

    System.out.println("java version: " + System.getProperty("java.version"));
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    TowerDefenseService service = new SimpleTowerDefenseService();
    TowerDefenseViewModel viewModel = new TowerDefenseViewModel(service);
    ScreenManager screenManager = new ScreenManager(viewModel);
    screenManager.switchTo(ScreenType.TITLE_SCREEN);

    primaryStage.setScene(new Scene(screenManager));
    primaryStage.setTitle("Tower Defense");
    primaryStage.show();
  }
}
