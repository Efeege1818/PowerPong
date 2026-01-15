package de.hhn.it.devtools.ui.fourconnect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(
        MainApp.class.getResource("/de/hhn/it/devtools/ui/fourconnect/fxml/game.fxml")
    );

    Scene scene = new Scene(loader.load(), 900, 650);
    stage.setTitle("Connect Four Toxic - JavaFX");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}





