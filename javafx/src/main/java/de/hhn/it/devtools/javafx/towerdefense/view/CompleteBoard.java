package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.Direction;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import de.hhn.it.devtools.components.towerdefensecomponents.GameLoop;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class CompleteBoard extends StackPane {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(CompleteBoard.class);

  TowerDefenseViewModel viewModel;
  ObjectProperty<Grid> gridProperty = new SimpleObjectProperty<>();
  GridPane mapGrid = new GridPane();
  ListProperty<Enemy> enemies = new SimpleListProperty<>();
  GridPane enemyGrid = new GridPane();
  public final int size;

  public CompleteBoard(TowerDefenseViewModel viewModel) {
    alignmentProperty().set(Pos.TOP_LEFT);
    this.viewModel = viewModel;
    createGridDisplay();
    size = gridProperty.get().grid().length;
  }


  public void createGridDisplay() {
    boardDisplay();
    enemyDisplay();
    double scale = 0.0625;

    enemyGrid.setScaleX(scale);
    enemyGrid.setScaleY(scale);

    enemyGrid.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
      enemyGrid.setTranslateX(-(newBounds.getWidth() * (1 - scale)) / 2);
      enemyGrid.setTranslateY(-(newBounds.getHeight() * (1 - scale)) / 2);
    });

    getChildren().addAll(mapGrid, enemyGrid);

  }

  public void boardDisplay() {
    this.gridProperty.bind(viewModel.getMap());
    mapGrid.widthProperty().divide(2);
    mapGrid.scaleXProperty().setValue(mapGrid.scaleXProperty().get());
    mapGrid.scaleYProperty().setValue(mapGrid.scaleYProperty().get());
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
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
    enemies.addListener(new ChangeListener<ObservableList<Enemy>>() {
      @Override
      public void changed(ObservableValue<? extends ObservableList<Enemy>> observableValue, ObservableList<Enemy> enemies, ObservableList<Enemy> t1) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            enemyGrid.getChildren().clear();
            int length = gridProperty.get().grid().length;
            for (int row = 0; row < length * 16; row++) {
              for (int col = 0; col < length * 16; col++) {
                Rectangle rectangle = new Rectangle(16, 16);
                rectangle.setStroke(Color.BLACK);
                rectangle.setFill(Color.RED);
                rectangle.opacityProperty().setValue(1);
                enemyGrid.add(rectangle, col, row);
              }
            }
//            for (Enemy enemy : enemies) {
//              Rectangle rectangle = new Rectangle(16, 16);
//              rectangle.setStroke(Color.BLACK);
//              rectangle.setFill(Color.RED);
//              enemyGrid.getChildren().remove((int) (enemy.coordinates().x() * 16),
//                      (int) enemy.coordinates().y() * 16);
//              enemyGrid.getChildren().re
//            }
          }
        });
      }
    });
  }

  public void towerDisplay() {

  }

  public void placeTower(TowerType type, int x, int y) {
    Rectangle tile = (Rectangle) mapGrid.getChildren().get(x * size + y);
    tile.setFill(viewModel.getTowerColors(type));
  }
}




//            enemyGrid.getChildren().clear();
//            for (Enemy enemy : enemies) {
//              Rectangle rectangle = new Rectangle(10, 10);
//              rectangle.setStroke(Color.BLACK);
//              rectangle.setFill(Color.RED);
//              enemyGrid.add(rectangle, (int) (enemy.coordinates().x() * 16),
//                      (int) (enemy.coordinates().y() * 16));
//            }



//getChildren().clear();
//getChildren().add(mapGrid);
//            for (Enemy enemy : enemies) {
//Rectangle rectangle = new Rectangle(10, 10);
//              rectangle.setStroke(Color.BLACK);
//              rectangle.setFill(Color.RED);
//              rectangle.setX(enemy.coordinates().x());
//getChildren().addAll(rectangle);
//              rectangle.setX(enemy.coordinates().x());
//        System.out.println(rectangle.getX());
//        }