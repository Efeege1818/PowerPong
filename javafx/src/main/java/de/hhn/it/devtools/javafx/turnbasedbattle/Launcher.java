package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class Launcher {

  public static void openNewWindow() {
    // Root container that screens will be swapped into
    StackPane rootPane = new StackPane();

    // Screen manager
    SimpleScreenManager screenManager = new SimpleScreenManager(rootPane);

    // Start with SelectScreen
    try {
      screenManager.switchTo(null, SelectScreen.SCREEN_NAME);
    } catch (UnknownTransitionException e) {
      throw new RuntimeException(e);
    }

    // Scene & Stage
    Stage stage = new Stage();
    Scene scene = new Scene(rootPane, 600, 400);
    stage.setTitle("Turn Based Battle");
    stage.setFullScreen(true);
    stage.setFullScreenExitHint("");
    stage.setScene(scene);
    stage.show();
  }
}