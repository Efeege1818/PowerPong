package de.hhn.it.devtools.ui.fourconnect;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

  @Override
  public void start(Stage stage) {
    SceneManager.init(stage);
    SceneManager.showMain();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
