package de.hhn.it.devtools.javafx.fourconnect.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Connect4Controller {

	@FXML private Label infoLabel;

	@FXML
	private void initialize() {
		infoLabel.setText("Connect4 (ohne Toxic) – optional. Nutze links Connect4Toxic für eure Abgabe.");
	}
}