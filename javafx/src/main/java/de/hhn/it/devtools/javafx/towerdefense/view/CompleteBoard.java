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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CompleteBoard extends StackPane {
  TowerDefenseViewModel viewModel;
  int size;
  ObjectProperty<Grid> gridProperty = new SimpleObjectProperty<>();
  GridPane mapGrid = new GridPane();
  ListProperty<Enemy> enemies = new SimpleListProperty<>();
  ListProperty<Tower> towers = new SimpleListProperty<>();

  Group enemyContainer = new Group();
  Group towerContainer = new Group();

  public CompleteBoard(TowerDefenseViewModel viewModel) {

    alignmentProperty().set(Pos.TOP_LEFT);
    this.viewModel = viewModel;
    this.gridProperty.bind(viewModel.getMap());
    size = gridProperty.get().grid().length;
    enemyContainer.minHeight(size * 16);
    enemyContainer.minWidth(size * 16);
    towerContainer.minHeight(size * 16);
    towerContainer.minWidth(size * 16);
    createGridDisplay();
  }


  public void createGridDisplay() {
    boardDisplay();
    enemyDisplay();
    towerDisplay();

    getChildren().addAll(mapGrid, towerContainer, enemyContainer);
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
    enemies.addListener((obs, oldEx, newEx) -> Platform.runLater(this::updateEnemies));
  }

  public void towerDisplay() {
    towers.bind(viewModel.getTowers());
    towers.addListener((obs, oldEx, newEx) -> Platform.runLater(this::updateTowers));
  }

  public void updateTowers() {
    towerContainer.getChildren().clear();

    // Workaround for keeping Towers at their positions at all times
    towerContainer.getChildren().add(new Rectangle(0, 0));

    for (Tower tower : towers) {
      Rectangle towerRectangle = new Rectangle(10, 10);
      towerRectangle.setStroke(Color.BLACK);
      towerRectangle.setFill(viewModel.getTowerColors(tower.type()));
      // TODO: Remove Voodoo Constants
      towerRectangle.setTranslateX(tower.coordinates().x() * 17 + 3.5);
      towerRectangle.setTranslateY(tower.coordinates().y() * 17 + 3.5);
      towerContainer.getChildren().add(towerRectangle);
    }
  }

  public void updateEnemies() {
    enemyContainer.getChildren().clear();

    enemyContainer.getChildren().add(new Rectangle(0, 0));

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
      // TODO: Replace the 3.5 (Voodoo Constants) Values with the real calculations for different Enemy Types
      rectangle.setTranslateX(enemy.coordinates().x() * 17 + 3.5);
      rectangle.setTranslateY(enemy.coordinates().y() * 17 + 3.5);
      enemyContainer.getChildren().add(rectangle);
    }
  }
}