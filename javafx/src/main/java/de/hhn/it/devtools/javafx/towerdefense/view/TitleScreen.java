package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.Configuration;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TitleScreen extends StackPane {
  TowerDefenseViewModel viewModel;

  Label towerDefenseLbl = new Label("Tower defense");
  Button startGame = new Button("Start Game");
  Button config = new Button("config");
  Button exitGame = new Button("Exit Game");

  public TitleScreen(TowerDefenseViewModel viewModel) {
    this.viewModel = viewModel;

    startGame.setOnAction(event -> {
      startGame();
    });

    exitGame.setOnAction(event -> {
      exitGame();
    });

    config.setOnAction(event -> {

    });

  }

  public void createGrid(){

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
