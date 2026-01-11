package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefenseapi.*;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenManager;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenType;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.NoSuchElementException;

public class GameScreen extends StackPane {
  ScreenManager screenManager;
  TowerDefenseViewModel viewModel;
  CompleteBoard completeBoard;
  VBox mainLayout = new VBox();
  StackPane overlayPane = new StackPane();


  public GameScreen(ScreenManager screenManager) {
    this.screenManager = screenManager;
    this.viewModel = screenManager.getViewModel();
    this.completeBoard = new CompleteBoard(viewModel);

    setAlignment(Pos.CENTER);
    createDisplay();
  }


  public void createDisplay() {
    mainLayout.setSpacing(10);
    mainLayout.setAlignment(Pos.CENTER);

    mainLayout.getChildren().addAll(
            createTowerDisplay(),
            completeBoard,
            createStatsDisplay(),
            createButtonDisplay()
    );
    getChildren().add(mainLayout);

    viewModel.getGameOver().addListener((obs, oldEx, newEx) -> {
      if (newEx == true) {
        getChildren().add(createOverlayDisplay());
      }
    });

    double scale = 3;

    setScaleX(scale);
    setScaleY(scale);

    layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
      setTranslateX(-(newBounds.getWidth() * (1 - scale)) / 2);
      setTranslateY(-(newBounds.getHeight() * (1 - scale)) / 2);
    });

  }

  public GridPane createTowerDisplay() {
    GridPane towerDisplay = new GridPane();
    towerDisplay.setAlignment(Pos.CENTER);
    towerDisplay.setHgap(10);

    int columnIndex = 0;
    int rowIndex = 0;
    for (TowerType type : viewModel.getTowerTypes().keySet()) {
      StackPane towerIcon = new StackPane();
      Rectangle towerIconRect = new Rectangle(20, 20);

      towerIconRect.setFill(viewModel.getTowerColors(type));
      int towerCostNumber = viewModel.getTowerTypes().get(type);
      Label towerCost = new Label(String.valueOf(towerCostNumber));
      towerCost.setTextFill(Color.GOLD);

      towerIcon.getChildren().addAll(towerIconRect, towerCost);
      towerDisplay.add(towerIcon, columnIndex++, rowIndex);
    }

    return towerDisplay;
  }


  public GridPane createStatsDisplay() {
    GridPane statsDisplay = new GridPane();
    statsDisplay.setAlignment(Pos.CENTER);
    statsDisplay.setHgap(10);

    Label moneyLabel = new Label();
    moneyLabel.setTextFill(Color.GOLD);

    Label healthLabel = new Label();
    healthLabel.setTextFill(Color.LIME);

    Label roundsLabel = new Label();
    roundsLabel.setTextFill(Color.DARKGRAY);

    moneyLabel.textProperty().bind(viewModel.getMoney().asString("💵: %d"));
    healthLabel.textProperty().bind(viewModel.getHealth().asString("❤: %d"));
    roundsLabel.textProperty().bind(viewModel.getRound().asString("Round: %d"));

    statsDisplay.add(moneyLabel, 0, 0);
    statsDisplay.add(roundsLabel, 1, 0);
    statsDisplay.add(healthLabel, 2, 0);

    return statsDisplay;
  }

  public GridPane createButtonDisplay() {
    // TODO: Grey out when impossible to do
    Button startWaveButton = new Button("Start next Round");
    startWaveButton.setOnAction((event) -> {
      startWaveOnAction();
    });
    Button abortGameButton = new Button("Exit Game");
    abortGameButton.setOnAction((event) -> {
      abortGameOnAction();
    });

    startWaveButton.disableProperty().bind(viewModel.getCurrentGameState().isEqualTo(GameState.RUNNING));


    GridPane buttonDisplay = new GridPane();
    buttonDisplay.setAlignment(Pos.CENTER);
    buttonDisplay.setHgap(10);

    buttonDisplay.add(startWaveButton, 0, 0);
    buttonDisplay.add(abortGameButton, 1, 0);

    return buttonDisplay;
  }

  public GridPane createOverlayDisplay() {
    // TODO: in "Game-Over" Overlay
    Button retryWaveButton = new Button("Retry this Round");
    retryWaveButton.setOnAction((event) -> {
      retryWaveOnAction();
    });
    GridPane overlayDisplay = new GridPane();
    overlayDisplay.setAlignment(Pos.CENTER);
    overlayDisplay.setHgap(10);
    overlayDisplay.add(retryWaveButton, 0, 0);

    return overlayDisplay;
  }

  public void startWaveOnAction() {
    try {
      viewModel.startNextRound();
    } catch (IllegalStateException e) {
      // Temporary Solution for Illegal Button press
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText("Wave can't be started");
      alert.showAndWait();
    }
  }

  public void retryWaveOnAction() {
    try {
      // TODO: retryRound();
      viewModel.retryRound();
    } catch (IllegalStateException e) {
      // Temporary Solution for Illegal Button press
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText("Wave can't be started");
      alert.showAndWait();
    }
  }

  public void abortGameOnAction() {
    try {
      viewModel.abortGame();
      screenManager.switchTo(ScreenType.TITLE_SCREEN);
    } catch (IllegalStateException e) {
      // Temporary Solution for Illegal Button press
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText("Wave can't be started");
      alert.showAndWait();
    }
  }
}
