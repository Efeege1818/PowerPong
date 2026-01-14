package de.hhn.it.devtools.javafx.shapesurvivor.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Info screen displaying game instructions and mechanics for Shape Survivor.
 */
public class InfoScreen {

  private final Stage owner;

  public InfoScreen(Stage owner) {
    this.owner = owner;
  }

  /**
   * Shows the info screen as a modal popup.
   */
  public void show() {
    show(null);
  }

  /**
   * Shows the info screen as a modal popup.
   *
   * @param onClose Optional callback to run when the info screen closes
   */
  public void show(EventHandler<ActionEvent> onClose) {
    Stage infoStage = new Stage();
    infoStage.initOwner(owner);
    infoStage.initModality(Modality.WINDOW_MODAL);
    infoStage.setTitle("How to Play - Shape Survivor");
    infoStage.setResizable(false);

    VBox mainContainer = new VBox(25);
    mainContainer.setAlignment(Pos.TOP_CENTER);
    mainContainer.setPadding(new Insets(30));
    mainContainer.setStyle(
        "-fx-background-color: linear-gradient(to bottom, "
            + "rgba(20,20,40,0.98), rgba(40,94,46,0.95));"
    );

    Label titleLabel = new Label("SHAPE SURVIVOR");
    titleLabel.setStyle(
        "-fx-text-fill: #c6c6c4; "
            + "-fx-font-weight: bold; "
            + "-fx-font-size: 32px; "
            + "-fx-effect: dropshadow(one-pass-box, black, 5, 0.0, 2, 2);"
    );

    Label subtitleLabel = new Label("How to Play");
    subtitleLabel.setStyle(
        "-fx-text-fill: #FFFFFF; "
            + "-fx-font-size: 18px; "
            + "-fx-effect: dropshadow(one-pass-box, black, 3, 0.0, 1, 1);"
    );

    VBox contentBox = new VBox(20);
    contentBox.setPadding(new Insets(15));
    contentBox.setAlignment(Pos.TOP_LEFT);

    contentBox.getChildren().add(createSectionTitle("OBJECTIVE"));
    contentBox.getChildren().add(createInfoText(
        "Survive for 15 minutes against endless waves of geometric enemies! "
            + "Kill enemies to gain experience, level up, and unlock powerful upgrades."
    ));

    contentBox.getChildren().add(createSectionTitle("CONTROLS"));
    VBox controlsBox = new VBox(8);
    controlsBox.getChildren().addAll(
        createControlRow("W / ↑", "Move Up"),
        createControlRow("A / ←", "Move Left"),
        createControlRow("S / ↓", "Move Down"),
        createControlRow("D / →", "Move Right"),
        createControlRow("ESC", "Pause Game")
    );
    contentBox.getChildren().add(controlsBox);

    contentBox.getChildren().add(createSectionTitle("GAMEPLAY"));
    contentBox.getChildren().add(createInfoText(
        """
            • Your weapons automatically attack nearby enemies
            • Take damage when enemies touch you
            • Enemy waves increase in size and difficulty over time
            • Watch your health bar above your character
            • Keep an eye on the timer - survive until it reaches 00:00!"""
    ));

    contentBox.getChildren().add(createSectionTitle("LEVELING & UPGRADES"));
    contentBox.getChildren().add(createInfoText(
        """
            • Kill enemies to earn experience points
            • Fill the XP bar to level up
            • Choose from powerful upgrades when you level up:
              - Increase Health, Damage, Speed, or Attack Speed
              - Unlock new weapons every 5 levels
              - Upgrade existing weapons for enhanced effects"""
    ));

    contentBox.getChildren().add(createSectionTitle("WEAPONS"));
    contentBox.getChildren().add(createInfoText(
        """
            SWORD: Orbits around you, slicing enemies on contact
              Upgrades: Larger radius, multiple swords
            
            AURA: Pulses damage in a circle around you
              Upgrades: Bigger area, faster damage ticks
            
            WHIP: Lashes out in front and behind you
              Upgrades: Extended reach, multiple whips"""
    ));

    // Tips Section
    contentBox.getChildren().add(createSectionTitle("TIPS"));
    contentBox.getChildren().add(createInfoText(
        """
            • Keep moving to avoid getting surrounded
            • Balance offensive and defensive upgrades
            • Different weapons synergize well together
            • Higher difficulties spawn tougher enemies faster
            • Plan your build strategy from the start!"""
    ));

    ScrollPane scrollPane = new ScrollPane(contentBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle(
        "-fx-background: transparent; "
            + "-fx-background-color: transparent; "
            + "-fx-border-color: #4a90e2; "
            + "-fx-border-width: 2; "
            + "-fx-border-radius: 8;"
    );
    scrollPane.setPrefHeight(450);
    scrollPane.setMaxHeight(450);

    // Close button
    Button closeButton = getButton(onClose, infoStage);

    mainContainer.getChildren().addAll(
        titleLabel,
        subtitleLabel,
        scrollPane,
        closeButton
    );

    Scene scene = new Scene(mainContainer, 600, 700);
    infoStage.setScene(scene);
    infoStage.show();
  }

  private static Button getButton(EventHandler<ActionEvent> onClose, Stage infoStage) {
    Button closeButton = new Button("Got It!");
    closeButton.setPrefWidth(200);
    closeButton.setPrefHeight(45);
    closeButton.setStyle(
        "-fx-background-color: #4a90e2; "
            + "-fx-text-fill: white; "
            + "-fx-font-size: 16px; "
            + "-fx-font-weight: bold; "
            + "-fx-background-radius: 8; "
            + "-fx-cursor: hand;"
    );
    closeButton.setOnMouseEntered(e -> closeButton.setStyle(
        "-fx-background-color: #c6c6c4;  "
            + "-fx-text-fill: black; "
            + "-fx-font-size: 16px; "
            + "-fx-font-weight: bold; "
            + "-fx-background-radius: 8; "
            + "-fx-cursor: hand;"
    ));
    closeButton.setOnMouseExited(e -> closeButton.setStyle(
        "-fx-background-color: #4a90e2; "
            + "-fx-text-fill: white; "
            + "-fx-font-size: 16px; "
            + "-fx-font-weight: bold; "
            + "-fx-background-radius: 8; "
            + "-fx-cursor: hand;"
    ));
    closeButton.setOnAction(e -> {
      infoStage.close();
      if (onClose != null) {
        onClose.handle(e);
      }
    });
    return closeButton;
  }

  private Label createSectionTitle(String title) {
    Label label = new Label(title);
    label.setStyle(
        "-fx-text-fill: #c6c6c4;  "
            + "-fx-font-weight: bold; "
            + "-fx-font-size: 20px; "
            + "-fx-effect: dropshadow(one-pass-box, black, 3, 0.0, 1, 1);"
    );
    return label;
  }

  private Label createInfoText(String text) {
    Label label = new Label(text);
    label.setStyle(
        "-fx-text-fill: #CCCCCC; "
            + "-fx-font-size: 14px; "
            + "-fx-line-spacing: 3px;"
    );
    label.setWrapText(true);
    label.setMaxWidth(550);
    label.setTextAlignment(TextAlignment.LEFT);
    return label;
  }

  private HBox createControlRow(String key, String action) {
    HBox row = new HBox(15);
    row.setAlignment(Pos.CENTER_LEFT);

    Label keyLabel = new Label(key);
    keyLabel.setStyle(
        "-fx-background-color: rgba(70,70,110,0.8); "
            + "-fx-text-fill: #c6c6c4;  "
            + "-fx-font-weight: bold; "
            + "-fx-font-size: 14px; "
            + "-fx-padding: 6 12 6 12; "
            + "-fx-background-radius: 5; "
            + "-fx-border-color: #4a90e2; "
            + "-fx-border-width: 1; "
            + "-fx-border-radius: 5;"
    );
    keyLabel.setMinWidth(80);
    keyLabel.setAlignment(Pos.CENTER);

    Label actionLabel = new Label(action);
    actionLabel.setStyle(
        "-fx-text-fill: #FFFFFF; "
            + "-fx-font-size: 14px;"
    );

    row.getChildren().addAll(keyLabel, actionLabel);
    return row;
  }
}