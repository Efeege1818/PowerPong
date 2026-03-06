package de.hhn.it.devtools.javafx.fourconnect.controller;

import de.hhn.it.devtools.javafx.controllers.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class RootController extends Controller {

	@FXML
	private StackPane contentPane;

	@FXML
	private void initialize() {
		showConnect4Toxic();
	}

	private void showConnect4Toxic() {
		setContent("/fxml/connect4toxic.fxml");
	}

	private void setContent(String fxmlPath) {
		try {
			Parent view = new FXMLLoader(getClass().getResource(fxmlPath)).load();
			contentPane.getChildren().setAll(view);
		} catch (Exception e) {
			throw new RuntimeException("Konnte View nicht laden: " + fxmlPath, e);
		}
	}
}