package de.hhn.it.devtools.javafx.towerdefense.controllers;

import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Class that starts the TowerDefense Game.
 */
public class TowerDefenseLauncher extends Application {

  TowerDefenseService service;

  /**
   * The main method.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {

    System.out.println("java version: " + System.getProperty("java.version"));
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    service = new SimpleTowerDefenseService();
    TowerDefenseViewModel viewModel = new TowerDefenseViewModel(service);
    ScreenManager screenManager = new ScreenManager(viewModel);
    screenManager.switchTo(ScreenType.TITLE_SCREEN);
    primaryStage.setMaximized(true);
    primaryStage.setScene(new Scene(screenManager));
    primaryStage.setTitle("Tower Defense");
    primaryStage.show();
  }

  @Override
  public void stop() {
    service.abortGame();
  }
}
