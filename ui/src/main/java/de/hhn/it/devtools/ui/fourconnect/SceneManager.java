package de.hhn.it.devtools.ui.fourconnect;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

  private static Stage stage;

  public static void init(Stage primaryStage) {
    stage = primaryStage;
  }

  public static void showMain() {
    show("/de/hhn/it/devtools/ui/fourconnect/fxml/main.fxml", "Connect Four Toxic - Main");
  }

  public static void showSelect() {
    show("/de/hhn/it/devtools/ui/fourconnect/fxml/select.fxml", "Connect Four Toxic - Select");
  }

  public static void showGame() {
    show("/de/hhn/it/devtools/ui/fourconnect/fxml/game.fxml", "Connect Four Toxic - Game");
  }

  private static void show(String fxmlPath, String title) {
    try {
      FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
      Parent root = loader.load();
      stage.setTitle(title);
      stage.setScene(new Scene(root));
      stage.show();
    } catch (IOException e) {
      throw new RuntimeException("Cannot load FXML: " + fxmlPath, e);
    }
  }
}
