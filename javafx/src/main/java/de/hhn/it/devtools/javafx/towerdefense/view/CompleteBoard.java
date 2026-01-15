package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefense.Direction;
import de.hhn.it.devtools.apis.towerdefense.Enemy;
import de.hhn.it.devtools.apis.towerdefense.Grid;
import de.hhn.it.devtools.apis.towerdefense.Tower;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Node that displays the Board, Towers and Enemies.
 */
public class CompleteBoard extends StackPane {
  TowerDefenseViewModel viewModel;
  private final int gridSize;
  ObjectProperty<Grid> gridProperty = new SimpleObjectProperty<>();
  GridPane mapGrid = new GridPane();
  ListProperty<Enemy> enemies = new SimpleListProperty<>();
  ListProperty<Tower> towers = new SimpleListProperty<>();

  Group enemyContainer = new Group();
  Group towerContainer = new Group();

  /**
   * Constructor.
   *
   * @param viewModel the current ViewModel
   */
  public CompleteBoard(TowerDefenseViewModel viewModel) {

    this.viewModel = viewModel;

    alignmentProperty().set(Pos.TOP_LEFT);
    gridProperty.bind(viewModel.getMap());
    gridSize = gridProperty.get().grid().length;
    boardDisplay();
    gridProperty.addListener((obs, oldEx, newEx) -> Platform.runLater(this::boardDisplay));


    enemies.bind(viewModel.getEnemies());
    enemies.addListener((obs, oldEx, newEx) -> Platform.runLater(this::updateEnemies));

    towers.bind(viewModel.getTowers());
    towers.addListener((obs, oldEx, newEx) -> Platform.runLater(this::updateTowers));

    getChildren().addAll(mapGrid, towerContainer, enemyContainer);
  }

  private void boardDisplay() {
    mapGrid.widthProperty().divide(2);
    mapGrid.scaleXProperty().setValue(mapGrid.scaleXProperty().get());
    mapGrid.scaleYProperty().setValue(mapGrid.scaleYProperty().get());
    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {

        Rectangle rectangle = new Rectangle(16, 16);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(gridProperty.get().grid()[row][col]
            == Direction.NONE ? Color.DARKGREEN : Color.DARKGRAY);

        mapGrid.add(rectangle, col, row);
      }
    }
  }

  private void updateTowers() {
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

  private void updateEnemies() {
    enemyContainer.getChildren().clear();

    // Workaround for keeping Enemies at their positions at all times
    enemyContainer.getChildren().add(new Rectangle(0, 0));

    Rectangle rectangle;
    for (Enemy enemy : enemies) {
      double size = viewModel.getEnemySize(enemy.type());
      rectangle = new Rectangle(size, size);
      rectangle.setFill(viewModel.getEnemyColour(enemy.type()));
      rectangle.setStroke(Color.BLACK);
      rectangle.setTranslateX(enemy.coordinates().x() * 17 + (17 - size) / 2);
      rectangle.setTranslateY(enemy.coordinates().y() * 17 + (17 - size) / 2);
      enemyContainer.getChildren().add(rectangle);
    }
  }
}