package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.Configuration;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenManager;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TitleScreen extends StackPane {
  TowerDefenseViewModel viewModel;

  Label towerDefenseLbl = new Label("Tower defense");
  Button startGame = new Button("Start Game");
  Button config = new Button("config");
  Button exitGame = new Button("Exit Game");
  HBox box = new HBox();
  Label title = new Label();

  public TitleScreen(ScreenManager screenManager) {
    this.viewModel = viewModel;

    startGame.setOnAction(event -> {
      startGame();
    });

    exitGame.setOnAction(event -> {
      exitGame();
    });

    config.setOnAction(event -> {

    });

    createGrid();
  }

  public void createGrid() {
    title.textProperty().set("TOWER DEV");
    title.fontProperty().set(new Font("Impact", 20));
    box.getChildren().addAll(title, startGame, exitGame, config);
    getChildren().addAll(box);
  }

  public void startGame() {
    viewModel.startRound();
  }

  public void exitGame() {
    viewModel.abortGame();
  }

  public void setConfig() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    // TODO: implement config logic
  }
}
