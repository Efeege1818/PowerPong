package de.hhn.it.devtools.ui.fourconnect;

import de.hhn.it.devtools.ui.fourconnect.controller.GameController;
import de.hhn.it.devtools.ui.fourconnect.controller.MainController;
import de.hhn.it.devtools.ui.fourconnect.controller.MainMenuController;
import de.hhn.it.devtools.ui.fourconnect.controller.SelectController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

  private final Stage stage;

  public SceneManager(Stage stage) {
    this.stage = stage;
  }

public void showMain() {
  loadAndSet("/de/hhn/it/devtools/ui/fourconnect/fxml/main.fxml", MainMenuController.class);
}

public void showSelect() {
  loadAndSet("/de/hhn/it/devtools/ui/fourconnect/fxml/select.fxml", SelectController.class);
}

public void showGame() {
  loadAndSet("/de/hhn/it/devtools/ui/fourconnect/fxml/game.fxml", GameController.class);
}



  private void loadAndSet(String fxmlPath, Class<?> controllerClass) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

      // Controller-Factory: wir geben SceneManager rein
      loader.setControllerFactory(type -> {
        if (type == SelectController.class) return new SelectController(this);
        if (type == MainController.class) return new MainController(this);
        try {
          return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });

      Parent root = loader.load();
      Scene scene = new Scene(root);
      stage.setScene(scene);

    } catch (IOException e) {
      throw new RuntimeException("Could not load FXML: " + fxmlPath, e);
    }
  }
}
