package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.components.powerPong.provider.PowerPongMatchEngine;
import de.hhn.it.devtools.javafx.powerpong.view.PowerPongController;
import de.hhn.it.devtools.javafx.powerpong.viewmodel.PowerPongViewModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class PowerPongServiceController extends Controller implements Initializable {

  @FXML
  AnchorPane powerPongAnchorPane;

  private final PowerPongController powerPongController;

  public PowerPongServiceController() {
    PowerPongViewModel viewModel = new PowerPongViewModel(new PowerPongMatchEngine());
    powerPongController = new PowerPongController(viewModel);
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    powerPongAnchorPane.getChildren().add(powerPongController);
  }

  @Override
  public void resume() {
    powerPongController.resume();
  }

  @Override
  public void pause() {
    powerPongController.pause();
  }

  @Override
  public void shutdown() {
    powerPongController.shutdown();
  }
}
