package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefense.*;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenManager;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenType;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class GameScreen extends StackPane {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(GameScreen.class);

  private final GridPane gameEndedOverlay;
  private final GridPane roundCompletedOverlay;

  ScreenManager screenManager;
  TowerDefenseViewModel viewModel;
  CompleteBoard completeBoard;
  VBox mainLayout = new VBox();
  TowerType selectedTower = null;

  BooleanProperty showRoundCompletedOverlay = new SimpleBooleanProperty();
  BooleanProperty showGameEndedOverlay = new SimpleBooleanProperty();
  BooleanProperty enableNextWaveButton = new SimpleBooleanProperty();

  public GameScreen(ScreenManager screenManager) {
    this.screenManager = screenManager;
    this.viewModel = screenManager.getViewModel();
    this.completeBoard = new CompleteBoard(viewModel);

    gameEndedOverlay = createGameEndedOverlay();
    roundCompletedOverlay = createRoundCompletedOverlay();

    setAlignment(Pos.CENTER);
    createDisplay();
  }

  private void createDisplay() {
    mainLayout.setSpacing(1);
    mainLayout.setAlignment(Pos.CENTER);

    mainLayout.getChildren().addAll(
        createStatsDisplay(),
        completeBoard,
        createTowerDisplay()
    );
    getChildren().add(mainLayout);

    VBox tutorialLayout = new VBox();
    tutorialLayout.setSpacing(1);
    tutorialLayout.setAlignment(Pos.CENTER_RIGHT);
    tutorialLayout.getChildren().add(createTowerDefenseTutorialDisplay());

    HBox rootLayout = new HBox();
    rootLayout.setSpacing(5);
    rootLayout.setAlignment(Pos.TOP_CENTER);
    rootLayout.getChildren().addAll(mainLayout, tutorialLayout);

    getChildren().add(rootLayout);

    prepareTowerPlacement();

    viewModel.getGameState().addListener((obs, oldState, newState) -> {
      enableNextWaveButton.set(newState != GameState.RUNNING
          && !showGameEndedOverlay.get()
          && !showRoundCompletedOverlay.get());

      Platform.runLater(() -> {
        if(newState == GameState.GAME_OVER) {
          showGameEndedOverlay.setValue(true);
        }
        if (newState == GameState.PAUSED && oldState == GameState.RUNNING) {
          showRoundCompletedOverlay.setValue(true);
        }
      });
    });

    showGameEndedOverlay.addListener((obs, oldVal, newVal) -> {
      enableNextWaveButton.set(viewModel.getGameState().get() != GameState.RUNNING && !newVal);
      if (newVal) {
        logger.debug("Showing Game Ended Display");
        getChildren().add(gameEndedOverlay);
      } else {
        getChildren().remove(gameEndedOverlay);
      }
    });

    showRoundCompletedOverlay.addListener((obs, oldVal, newVal) -> {
      enableNextWaveButton.set(viewModel.getGameState().get() != GameState.RUNNING && !newVal);
      if (newVal) {
        logger.debug("Showing Round Completed Display");
        getChildren().add(roundCompletedOverlay);
      } else {
        getChildren().remove(roundCompletedOverlay);
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

  private GridPane createTowerDisplay() {
    GridPane towerDisplay = new GridPane();
    towerDisplay.setAlignment(Pos.CENTER_LEFT);
    towerDisplay.setHgap(1);

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

      // Input clickable event here
      towerIcon.setOnMouseClicked(e -> {
        selectedTower = type;
      });

      towerDisplay.add(towerIcon, columnIndex++, rowIndex);
    }

    Button startWaveButton = new Button("Start next Round");
    startWaveButton.setOnAction((event) -> {
      startWaveOnAction();
    });
    startWaveButton.disableProperty().bind(enableNextWaveButton.not());

    towerDisplay.add(startWaveButton,4,0);

    return towerDisplay;
  }

  private GridPane createStatsDisplay() {
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

    statsDisplay.setMaxHeight(5);

    return statsDisplay;
  }

  private GridPane createGameEndedOverlay() {
    GridPane display = new GridPane();
    display.setAlignment(Pos.CENTER);
    display.setHgap(10);

    Button retryButton = new Button("Retry");
    retryButton.setOnAction((event) -> {
      retryWaveOnAction();
      showGameEndedOverlay.set(false);
    });
    Label lostLabel = new Label("You Lost");
    display.add(lostLabel, 1, 0);
    display.add(retryButton, 0, 1);

    Button abortGameButton = new Button("Exit Game");
    abortGameButton.setOnAction((event) -> {
      abortGameOnAction();
      showGameEndedOverlay.set(false);
    });
    display.add(abortGameButton, 2, 1);


    display.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

    return display;
  }

  private GridPane createRoundCompletedOverlay() {
    GridPane display = new GridPane();
    display.setAlignment(Pos.CENTER);
    display.setHgap(10);

    Button continueButton = new Button("Continue");
    continueButton.setOnAction((event) -> {
      showRoundCompletedOverlay.set(false);
    });
    Label wonLabel = new Label("You Completed Round " + viewModel.getRound().getValue());
    display.add(wonLabel, 1, 0);
    display.add(continueButton, 0, 1);

    Button abortGameButton = new Button("Exit Game");
    abortGameButton.setOnAction((event) -> {
      abortGameOnAction();
      showRoundCompletedOverlay.set(false);
    });
    display.add(abortGameButton, 2, 1);

    display.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

    return display;

  }

  private GridPane createTowerDefenseTutorialDisplay() {
    GridPane tutorialDisplay = new GridPane();
    tutorialDisplay.setAlignment(Pos.CENTER);
    tutorialDisplay.setHgap(10);
    tutorialDisplay.setVgap(5);

    Label title = new Label("Tower Defense Tutorial - How to play:");
    title.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
    title.setWrapText(true);
    title.setTextAlignment(TextAlignment.CENTER);
    title.setMaxWidth(400);
    GridPane.setHalignment(title, HPos.CENTER);
    tutorialDisplay.add(title, 0, 0, 2, 1);

    int rowIndex = 1;

    Label towerTut = new Label("To defend yourself, you need to place towers that kill enemies.\n" +
            "Click the tower you want to place and select a free tile to place it. \n" +
            "Enemies spawn at the first part of the path and move to the end.\n" +
            "Once reached. You lose health based on the enemy type.");
    towerTut.setStyle("-fx-font-style: italic; -fx-font-size: 12;");
    towerTut.setWrapText(true);
    towerTut.setTextAlignment(TextAlignment.LEFT);
    towerTut.setMaxWidth(400);
    GridPane.setHalignment(towerTut, HPos.LEFT);
    tutorialDisplay.add(towerTut, 0, ++rowIndex, 2, 1);

    StackPane towerIcon1 = new StackPane();
    Rectangle towerIconRect1 = new Rectangle(20, 20);
    towerIconRect1.setFill(viewModel.getTowerColors(TowerType.MONEYMAKER));
    int towerCostNumber1 = viewModel.getTowerTypes().get(TowerType.MONEYMAKER);
    Label towerCost1 = new Label(String.valueOf(towerCostNumber1));
    towerCost1.setTextFill(Color.GOLD);
    towerIcon1.getChildren().addAll(towerIconRect1, towerCost1);

    Label descLabel1 = new Label("The Moneymaker Tower generates some money throughout \nthe round" +
            "- does no damage however.");
    descLabel1.setWrapText(true);

    tutorialDisplay.add(towerIcon1, 0, ++rowIndex);
    tutorialDisplay.add(descLabel1, 1, rowIndex);

    StackPane towerIcon2 = new StackPane();
    Rectangle towerIconRect2 = new Rectangle(20, 20);
    towerIconRect2.setFill(viewModel.getTowerColors(TowerType.MELEE));
    int towerCostNumber2 = viewModel.getTowerTypes().get(TowerType.MELEE);
    Label towerCost2 = new Label(String.valueOf(towerCostNumber2));
    towerCost2.setTextFill(Color.GOLD);
    towerIcon2.getChildren().addAll(towerIconRect2, towerCost2);

    Label descLabel2 = new Label("The Melee Tower does good damage, however has a small range.");
    descLabel2.setWrapText(true);

    tutorialDisplay.add(towerIcon2, 0, ++rowIndex);
    tutorialDisplay.add(descLabel2, 1, rowIndex);

    StackPane towerIcon3 = new StackPane();
    Rectangle towerIconRect3 = new Rectangle(20, 20);
    towerIconRect3.setFill(viewModel.getTowerColors(TowerType.RANGED));
    int towerCostNumber3 = viewModel.getTowerTypes().get(TowerType.RANGED);
    Label towerCost3 = new Label(String.valueOf(towerCostNumber3));
    towerCost3.setTextFill(Color.GOLD);
    towerIcon3.getChildren().addAll(towerIconRect3, towerCost3);

    Label descLabel3 = new Label("The Ranged Tower does little damage within a wide range.");
    descLabel3.setWrapText(true);

    tutorialDisplay.add(towerIcon3, 0, ++rowIndex);
    tutorialDisplay.add(descLabel3, 1, rowIndex);

    return tutorialDisplay;
  }

  private String getTowerDescription(TowerType type) {
    return switch (type) {
      case MONEYMAKER -> "Generates extra money each round.";
      case MELEE -> "Hurts enemies in a close range";
      case RANGED -> "Shoots enemies in wide range.";
      default -> "No description available.";
    };
  }

  private void prepareTowerPlacement() {
    int gridSize = viewModel.getMap().get().grid().length;

    for (int row = 0; row < gridSize; row++) {
      for (int column = 0; column < gridSize; column++) {
        // give easdcv rect an index, and cast to rectanle from node
        Rectangle allRect = (Rectangle) completeBoard
                .mapGrid
                .getChildren()
                .get(row * gridSize + column);

        int perfectRow = row;
        int perfectColumn = column;

        allRect.setOnMouseClicked(event -> {
          if (selectedTower != null) {
            try {
              // TODO: uuid fix not int id 2
              viewModel.addTower(new Tower(new Coordinates(perfectColumn, perfectRow), selectedTower));
              selectedTower = null; // reset if didn't work
            } catch (IllegalArgumentException e) {
              Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid action");
              alert.showAndWait();
            }
          }
        });
      }
    }
  }

  private void startWaveOnAction() {
    try {
      viewModel.startNextRound();
    } catch (IllegalStateException e) {
      // Temporary Solution for Illegal Button press
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText("Wave can't be started");
      alert.showAndWait();
    }
  }

  private void retryWaveOnAction() {
    try {
      viewModel.retryRound();
    } catch (IllegalStateException e) {
      // Temporary Solution for Illegal Button press
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText("Wave can't be started");
      alert.showAndWait();
    }
  }

  private void abortGameOnAction() {
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
