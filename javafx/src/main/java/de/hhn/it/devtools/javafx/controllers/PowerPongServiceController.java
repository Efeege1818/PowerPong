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
    // Set PowerPong-themed gradient background - EXACTLY matches menu
    String gradient = "-fx-background-color: linear-gradient(to bottom, rgb(5, 5, 25), rgb(15, 10, 35));";
    powerPongAnchorPane.setStyle(gradient);

    // Also set on parent's parent if available
    powerPongAnchorPane.parentProperty().addListener((obs, oldParent, newParent) -> {
      if (newParent instanceof javafx.scene.layout.Region region) {
        region.setStyle(gradient);
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
    // Retro grid pattern for arcade aesthetic

    // Horizontal grid lines
    for (int i = 0; i < 15; i++) {
      javafx.scene.shape.Line hLine = new javafx.scene.shape.Line(0, 0, 1000, 0);
      hLine.setStroke(javafx.scene.paint.Color.web("#00f3ff", 0.08));
      hLine.setStrokeWidth(1);
      AnchorPane.setTopAnchor(hLine, (double) (i * 50 + 25));
      AnchorPane.setLeftAnchor(hLine, 0.0);
      AnchorPane.setRightAnchor(hLine, 0.0);
      hLine.setMouseTransparent(true);
      pane.getChildren().add(hLine);
    }

    // Vertical grid lines (fewer)
    for (int i = 0; i < 20; i++) {
      javafx.scene.shape.Line vLine = new javafx.scene.shape.Line(0, 0, 0, 800);
      vLine.setStroke(javafx.scene.paint.Color.web("#ff00ff", 0.05));
      vLine.setStrokeWidth(1);
      AnchorPane.setLeftAnchor(vLine, (double) (i * 50 + 25));
      AnchorPane.setTopAnchor(vLine, 0.0);
      AnchorPane.setBottomAnchor(vLine, 0.0);
      vLine.setMouseTransparent(true);
      pane.getChildren().add(vLine);
    }
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
