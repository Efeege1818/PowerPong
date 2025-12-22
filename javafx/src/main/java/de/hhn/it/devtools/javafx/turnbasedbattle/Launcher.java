package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Launcher extends Application {

  @Override
  public void start(Stage stage) {
    // Root container that screens will be swapped into
    AnchorPane rootPane = new AnchorPane();

    // Screen manager
    SimpleScreenManager screenManager = new SimpleScreenManager(rootPane);

    // Start with SelectScreen
    try {
      screenManager.switchTo(null, SelectScreen.SCREEN_NAME);
    } catch (UnknownTransitionException e) {
      throw new RuntimeException(e);
    }

    // Scene & Stage
    Scene scene = new Scene(rootPane, 600, 400);
    stage.setTitle("Turn Based Battle");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
