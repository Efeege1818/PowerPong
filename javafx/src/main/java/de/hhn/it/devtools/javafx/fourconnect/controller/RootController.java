package de.hhn.it.devtools.javafx.fourconnect.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class RootController {

	@FXML private StackPane contentPane;

	@FXML
	private void initialize() {
		showConnect4(); // Default-Ansicht
	}

	@FXML
	private void onShowConnect4() {
		showConnect4();
	}

	@FXML
	private void onShowConnect4Toxic() {
		showConnect4Toxic();
	}

	private void showConnect4() {
		setContent("/connect4.fxml");
	}

	private void showConnect4Toxic() {
		setContent("/connect4toxic.fxml");
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