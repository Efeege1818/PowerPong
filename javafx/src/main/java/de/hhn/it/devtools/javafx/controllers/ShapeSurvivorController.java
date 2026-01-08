package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.javafx.shapesurvivor.view.ShapeSurvivorScreen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
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
        screen.setOnExit(() -> {
            shapeSurvivorPane.getChildren().clear();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShapeSurvivor.fxml"));
                Parent root = loader.load();
                shapeSurvivorPane.getChildren().add(root);
                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        Parent gameView = screen.getView();

        shapeSurvivorPane.getChildren().clear();
        shapeSurvivorPane.getChildren().add(gameView);

        AnchorPane.setTopAnchor(gameView, 0.0);
        AnchorPane.setBottomAnchor(gameView, 0.0);
        AnchorPane.setLeftAnchor(gameView, 0.0);
        AnchorPane.setRightAnchor(gameView, 0.0);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void shutdown() {
    }
}