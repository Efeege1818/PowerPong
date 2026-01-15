package de.hhn.it.devtools.javafx.towerdefense.view;

import de.hhn.it.devtools.apis.towerdefense.Difficulty;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenManager;
import de.hhn.it.devtools.javafx.towerdefense.controllers.ScreenType;
import de.hhn.it.devtools.javafx.towerdefense.viewmodel.TowerDefenseViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TitleScreen extends StackPane {
  ScreenManager screenManager;
  TowerDefenseViewModel viewModel;

  HBox box = new HBox();
  Button startGame = new Button("Start Game");
  Button config = new Button("config");
  Button exitGame = new Button("Exit Game");
  Label title = new Label();
  private ChoiceBox<Difficulty> difficultyBox;


  public TitleScreen(ScreenManager screenManager) {
    alignmentProperty().set(Pos.CENTER_RIGHT);
    this.screenManager = screenManager;
    this.viewModel = screenManager.getViewModel();
    createScreen();
  }

  public void createScreen() {
    double scale = 5;

    setScaleX(scale);
    setScaleY(scale);

    layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
      setTranslateX(-(newBounds.getWidth() * (1 - scale)) / 2);
      setTranslateY(-(newBounds.getHeight() * (1 - scale)) / 2);
    });
    startGame.setOnAction(event -> {
      startGame();
      screenManager.switchTo(ScreenType.GAME_SCREEN);
    });

    exitGame.setOnAction(event -> {
      exitGame();
    });


    difficultyBox = new ChoiceBox<>();
    difficultyBox.getItems().setAll(Difficulty.values());
    difficultyBox.valueProperty()
            .bindBidirectional(viewModel.difficultyProperty());


    title.textProperty().set("TOWER DEV");
    title.fontProperty().set(new Font("Impact", 20));
    box.getChildren().addAll(title, startGame, exitGame, difficultyBox);
    getChildren().addAll(box);
  }

  public void startGame() {
    viewModel.startGame();
  }

  public void exitGame() {
    ((Stage) (getScene().getWindow())).close();
  }

}
