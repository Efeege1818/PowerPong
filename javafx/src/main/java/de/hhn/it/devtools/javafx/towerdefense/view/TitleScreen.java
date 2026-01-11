package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenManager;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenType;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class TitleScreen extends StackPane {
  ScreenManager screenManager;
  TowerDefenseViewModel viewModel;

  Button startGame = new Button("Start Game");
  Button config = new Button("config");
  Button exitGame = new Button("Exit Game");
  HBox box = new HBox();
  Label title = new Label();

  public TitleScreen(ScreenManager screenManager) {
    alignmentProperty().set(Pos.CENTER_RIGHT);
    this.screenManager = screenManager;
    this.viewModel = screenManager.getViewModel();
    createScreen();
  }

  public void createScreen() {
    startGame.setOnAction(event -> {
      startGame();
      screenManager.switchTo(ScreenType.GAME_SCREEN);
    });

    exitGame.setOnAction(event -> {
      exitGame();
    });

    config.setOnAction(event -> {

    });

    title.textProperty().set("TOWER DEV");
    title.fontProperty().set(new Font("Impact", 20));
    box.getChildren().addAll(title, startGame, exitGame, config);
    getChildren().addAll(box);
  }

  public void startGame() {
    viewModel.startRound();
    viewModel.startNextRound();
  }

  public void exitGame() {
    System.exit(0);
  }

  public void setConfig() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    // TODO: implement config logic
  }
}
