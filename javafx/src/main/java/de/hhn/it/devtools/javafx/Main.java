package de.hhn.it.devtools.javafx;

import de.hhn.it.devtools.components.powerpong.provider.PowerPongMatchEngine;
import de.hhn.it.devtools.javafx.powerpong.view.PowerPongController;
import de.hhn.it.devtools.javafx.powerpong.viewmodel.PowerPongViewModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(Main.class);
  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;

  private PowerPongController powerPongController;

  /**
   * the main method.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    System.out.println("java version: " + System.getProperty("java.version"));
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    PowerPongViewModel viewModel = new PowerPongViewModel(new PowerPongMatchEngine());
    powerPongController = new PowerPongController(viewModel);

    primaryStage.setTitle("PowerPong");
    primaryStage.setScene(new Scene(powerPongController, WIDTH, HEIGHT));
    primaryStage.setMinWidth(WIDTH);
    primaryStage.setMinHeight(HEIGHT);
    primaryStage.show();

    powerPongController.resume();
  }

  @Override
  public void stop() {
    logger.info("stop: Shutting down");
    if (powerPongController != null) {
      powerPongController.shutdown();
    }
  }
}
