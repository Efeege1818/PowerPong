package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.Direction;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenManager;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameScreen extends StackPane {
  ScreenManager screenManager;
  TowerDefenseViewModel viewModel;
  CompleteBoard completeBoard;
  VBox box = new VBox();




  public GameScreen(ScreenManager screenManager) {
    alignmentProperty().set(Pos.CENTER_RIGHT);
    this.screenManager = screenManager;
    this.viewModel = screenManager.getViewModel();
    this.completeBoard = new CompleteBoard(viewModel);
    createDisplay();
  }


  public void createDisplay() {
    createTowerDisplay();
    createStatsDisplay();
    createButtonDisplay();
    box.getChildren().addAll(completeBoard);
    getChildren().addAll(box);
  }

  public void createTowerDisplay() {
  }


  public void createStatsDisplay() {

  }

  public void createButtonDisplay() {

  }

}
