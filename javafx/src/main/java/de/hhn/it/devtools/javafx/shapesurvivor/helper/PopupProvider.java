package de.hhn.it.devtools.javafx.shapesurvivor.helper;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopupProvider {
    private final Stage popup;
    private final ArrayList<Node> elements = new ArrayList<>();

    public PopupProvider(Stage owner) {
        this.popup = new Stage();
        this.popup.initOwner(owner);
        this.popup.initModality(Modality.WINDOW_MODAL);
    }

    public PopupProvider setTitle(String title) {
        this.popup.setTitle(title);
        return this;
    }

    public void addButton(EventHandler<ActionEvent> buttonEvent, String buttonText) {
        Button button = new Button(buttonText);
        button.setPrefWidth(200);
        button.setPrefHeight(50);

        button.setOnAction((e) -> {
            try {
                if (buttonEvent != null) {
                    buttonEvent.handle(e);
                }
            } finally {
                Stage stage = (Stage) button.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        });
        elements.add(button);
    }

    public PopupProvider addLabel(String labelText) {
        Label label = new Label(labelText);
        label.setStyle(
                "-fx-text-fill: #FFFFFF; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 20px; "
                        + "-fx-effect: dropshadow(one-pass-box, black, 3, 0.0, 1, 1); "
                        + "-fx-padding: 0 0 10 0;"
        );
        elements.add(label);
        return this;
    }

    public <T> PopupProvider addSelector(String labelText, T[] options, int defaultIndex) {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER);

        Label label = new Label(labelText);
        label.setStyle(
                "-fx-text-fill: #FFFFFF; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 16px; "
                        + "-fx-effect: dropshadow(one-pass-box, black, 2, 0.0, 1, 1);"
        );

        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(options);
        comboBox.getSelectionModel().select(defaultIndex);
        comboBox.setPrefWidth(250);
        comboBox.setStyle(
                "-fx-background-color: #2a2a3e; "
                        + "-fx-text-fill: #FFFFFF; "
                        + "-fx-font-size: 14px; "
                        + "-fx-border-color: #FFD700; "
                        + "-fx-border-width: 2; "
                        + "-fx-border-radius: 5; "
                        + "-fx-background-radius: 5;"
        );

        container.getChildren().addAll(label, comboBox);
        elements.add(container);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> ComboBox<T> getSelector(int index) {
        int selectorIndex = 0;
        for (Node element : elements) {
            if (element instanceof VBox vbox) {
                for (Node child : vbox.getChildren()) {
                    if (child instanceof ComboBox) {
                        if (selectorIndex == index) {
                            return (ComboBox<T>) child;
                        }
                        selectorIndex++;
                    }
                }
            }
        }
        return null;
    }

    public Stage build() {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(30, 30, 30, 30));

        if (!elements.isEmpty()) {
            vbox.getChildren().addAll(elements);
        }

        vbox.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(20,20,30,0.95),"
                + " rgba(40,40,60,0.95)); "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 25, 0.8, 0, 10); "
                + "-fx-padding: 40;");

        Scene scene = new Scene(vbox, 450, 450);
        this.popup.setScene(scene);
        this.popup.setMinWidth(400);
        this.popup.setMinHeight(400);
        this.popup.setResizable(false);
        return this.popup;
    }

}