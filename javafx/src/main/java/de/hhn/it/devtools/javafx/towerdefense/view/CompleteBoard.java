package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.Direction;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CompleteBoard extends StackPane {
  TowerDefenseViewModel viewModel;
  ObjectProperty<Grid> gridProperty = new SimpleObjectProperty<>();
  GridPane mapGrid = new GridPane();
  ListProperty<Enemy> enemies = new SimpleListProperty<>();
  ListProperty<Tower> towers = new SimpleListProperty<>();

  public CompleteBoard(TowerDefenseViewModel viewModel) {
    alignmentProperty().set(Pos.TOP_LEFT);
    this.viewModel = viewModel;
    createGridDisplay();
  }


  public void createGridDisplay() {
    boardDisplay();
    enemyDisplay();
    towerDisplay();

    getChildren().addAll(mapGrid);
  }

  public void boardDisplay() {
    this.gridProperty.bind(viewModel.getMap());
    int length = gridProperty.get().grid().length;
    mapGrid.widthProperty().divide(2);
    mapGrid.scaleXProperty().setValue(mapGrid.scaleXProperty().get());
    mapGrid.scaleYProperty().setValue(mapGrid.scaleYProperty().get());
    for (int row = 0; row < length; row++) {
      for (int col = 0; col < length; col++) {
        Rectangle rectangle = new Rectangle(16, 16);
        if (gridProperty.get().grid()[row][col] == Direction.NONE) {
          rectangle.setStroke(Color.BLACK);
          rectangle.setFill(Color.DARKGREEN);

        } else {
          rectangle.setStroke(Color.BLACK);
          rectangle.setFill(Color.DARKGREY);
        }
        mapGrid.add(rectangle, col, row);
      }
    }
  }

  public void enemyDisplay() {
    enemies.bind(viewModel.getEnemies());
    enemies.addListener((obs, oldEx, newEx) -> Platform.runLater(this::update));
  }

  public void towerDisplay() {
    towers.bind(viewModel.getTowers());
    towers.addListener((obs, oldEx, newEx) -> Platform.runLater(this::update));
  }

  public void update() {
    getChildren().clear();
    getChildren().add(mapGrid);
    Rectangle rectangle;
    for (Enemy enemy : enemies) {
      switch (enemy.type()) {
        case LARGE -> {
          rectangle = new Rectangle(10, 10);
          rectangle.setStroke(Color.BLACK);
          rectangle.setFill(Color.DARKRED);
        }
        case MEDIUM -> {
          rectangle = new Rectangle(8, 8);
          rectangle.setStroke(Color.BLACK);
          rectangle.setFill(Color.CRIMSON);
        }
        case SMALL -> {
          rectangle = new Rectangle(5, 5);
          rectangle.setStroke(Color.BLACK);
          rectangle.setFill(Color.RED);
        }
        default -> rectangle = new Rectangle(5, 5);
      }
      rectangle.setTranslateX(enemy.coordinates().x() * 17 + 2.7);
      rectangle.setTranslateY(enemy.coordinates().y() * 17 + 2.7);
      getChildren().add(rectangle);
    }

    for (Tower tower : towers) {
      Rectangle towerRectangle = new Rectangle(10, 10);
      towerRectangle.setStroke(Color.BLACK);
      towerRectangle.setFill(viewModel.getTowerColors(tower.type()));
      towerRectangle.setTranslateX(tower.coordinates().x() * 17 + 2.7);
      towerRectangle.setTranslateY(tower.coordinates().y() * 17 + 2.7);
      getChildren().add(towerRectangle);
    }
  }
}