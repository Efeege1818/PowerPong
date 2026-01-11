package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.Direction;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CompleteBoard extends StackPane {
  TowerDefenseViewModel viewModel;
  ObjectProperty<Grid> gridProperty = new SimpleObjectProperty<>();
  GridPane mapGrid = new GridPane();
  ListProperty<Enemy> enemies = new SimpleListProperty<>();
  GridPane enemyGrid = new GridPane();

  public CompleteBoard(TowerDefenseViewModel viewModel) {
    this.viewModel = viewModel;
    createGridDisplay();
  }


  public void createGridDisplay() {
    boardDisplay();
    enemyDisplay();
    getChildren().addAll(mapGrid, enemyGrid);
  }

  public void boardDisplay() {
    this.gridProperty.bind(viewModel.getMap());
    int length = gridProperty.get().grid().length;
    mapGrid.widthProperty().divide(2);


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

    for (Enemy enemy : enemies) {
      Rectangle rectangle = new Rectangle(10, 10);
      rectangle.setStroke(Color.BLACK);
      rectangle.setFill(Color.RED);
      enemyGrid.add(rectangle, (int) (enemy.coordinates().y() * 16),
              (int) (enemy.coordinates().x() * 16));
    }
  }

  public void towerDisplay() {

  }
}
