package de.hhn.it.devtools.ui.fourconnect;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

  @Override
  public void start(Stage stage) {
    SceneManager sceneManager = new SceneManager(stage);
    sceneManager.showSelect(); // Startscreen zuerst
    stage.setTitle("ConnectFourToxic");
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
