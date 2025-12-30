package de.hhn.it.devtools.javafx.powerPong;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.components.powerPong.provider.PowerPongMatchEngine;
import de.hhn.it.devtools.javafx.controllers.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class PowerPongController extends Controller {

    private PowerPongService service;

    public PowerPongController() {
        // In a real app, this might come from a service locator or DI
        this.service = new PowerPongMatchEngine();
    }

    @FXML
    public void onStartGame(ActionEvent event) {
        try {
            System.out.println("Starting PowerPong Game...");
            service.startGame(GameMode.CLASSIC_DUEL);
            System.out.println("Game started successfully!");
        } catch (GameLogicException e) {
            e.printStackTrace();
        }
    }
}
