package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.javafx.shapesurvivor.view.ShapeSurvivorScreen;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the ShapeSurvivor game module.
 */
public class ShapeSurvivorController extends Controller implements Initializable {

    @FXML
    private AnchorPane shapeSurvivorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void doActionStart() {
        Stage mainStage = (Stage) shapeSurvivorPane.getScene().getWindow();

        ShapeSurvivorScreen screen = new ShapeSurvivorScreen(mainStage);
        Parent gameView = screen.getView();

        shapeSurvivorPane.getChildren().clear();
        shapeSurvivorPane.getChildren().add(gameView);

        AnchorPane.setTopAnchor(gameView, 0.0);
        AnchorPane.setBottomAnchor(gameView, 0.0);
        AnchorPane.setLeftAnchor(gameView, 0.0);
        AnchorPane.setRightAnchor(gameView, 0.0);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void shutdown() {}
}