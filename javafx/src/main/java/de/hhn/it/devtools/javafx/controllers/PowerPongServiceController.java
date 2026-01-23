package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.components.powerpong.provider.PowerPongMatchEngine;
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
    // Set PowerPong-themed gradient background - EXACTLY matches menu
    String gradient = "-fx-background-color: linear-gradient(to bottom, rgb(5, 5, 25), rgb(15, 10, 35));";
    powerPongAnchorPane.setStyle(gradient);

    // Also set on parent's parent if available
    powerPongAnchorPane.parentProperty().addListener((obs, oldParent, newParent) -> {
      if (newParent instanceof javafx.scene.layout.Region region) {
        System.out.println("PowerPongController attached to parent: " + newParent);
        // Force anchor constraints on SELF when attached to parent (which is
        // RootController's AnchorPane)
        if (newParent instanceof AnchorPane) {
          AnchorPane.setTopAnchor(powerPongAnchorPane, 0.0);
          AnchorPane.setBottomAnchor(powerPongAnchorPane, 0.0);
          AnchorPane.setLeftAnchor(powerPongAnchorPane, 0.0);
          AnchorPane.setRightAnchor(powerPongAnchorPane, 0.0);
        }
        // region.setStyle(gradient); // Optional: tint parent too
      }
    });

    // Add decorative neon glow circles in corners
    addDecorativeGlow(powerPongAnchorPane);

    // Set anchor constraints to fill the entire AnchorPane
    AnchorPane.setTopAnchor(powerPongController, 0.0);
    AnchorPane.setBottomAnchor(powerPongController, 0.0);
    AnchorPane.setLeftAnchor(powerPongController, 0.0);
    AnchorPane.setRightAnchor(powerPongController, 0.0);
    powerPongAnchorPane.getChildren().add(powerPongController);
  }

  private void addDecorativeGlow(AnchorPane pane) {
    // Simple background - no extra elements to avoid lag
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
