package de.hhn.it.devtools.javafx.spaceinvaders.helper;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Helper class for Popups.
 */
public class PopupProvider {
  private final Stage popup;
  private final ArrayList<Node> elements = new ArrayList<>();

  /**
   * Initialize PopupProvider.
   *
   * @param owner the owner stage.
   */
  public PopupProvider(Stage owner) {
    this.popup = new Stage();
    this.popup.initOwner(owner);
    this.popup.initModality(Modality.WINDOW_MODAL);
    this.popup.getIcons().add(new Image(getClass()
            .getResource("/spaceinvaders/images/logo.png").toExternalForm()));
  }

  /**
   * Set title of the popup.
   *
   * @param title the title of the popup.
   * @return the PopupProvider instance.
   */
  public PopupProvider setTitle(String title) {
    this.popup.setTitle(title);
    return this;
  }

  /**
   * Add button to the popup.
   *
   * @param buttonEvent the event handler for the button.
   * @param buttonText the text of the button.
   * @return the PopupProvider instance.
   */
  public PopupProvider addButton(EventHandler<ActionEvent> buttonEvent, String buttonText) {
    Button button = new Button(buttonText);
    button.setPrefWidth(150);
    button.setOnAction((e) -> {
      try {
        if (buttonEvent != null) {
          buttonEvent.handle(e);
        }
      } finally {
        // ensure the popup is closed even if handler throws
        Stage stage = (Stage) button.getScene().getWindow();
        if (stage != null) {
          stage.close();
        }
      }
    });
    elements.add(button);
    return this;
  }

  /**
   * Add label to the popup.
   *
   * @param labelText the text of the label.
   * @return the PopupProvider instance.
   */
  public PopupProvider addLabel(String labelText) {
    Label label = new Label(labelText);
    elements.add(label);
    return this;
  }

  /**
   * Set close request handler for the popup.
   *
   * @param closeEvent the event handler for the close request.
   * @return the PopupProvider instance.
   */
  public PopupProvider setCloseRequest(EventHandler<WindowEvent> closeEvent) {
    this.popup.setOnCloseRequest(closeEvent);
    return this;
  }

  /**
   * Build the popup stage.
   *
   * @return the built popup stage.
   */
  public Stage build() {
    VBox vbox = new VBox(20);
    vbox.setAlignment(Pos.CENTER);
    vbox.setPadding(new Insets(30, 30, 30, 30));
    if (!elements.isEmpty()) {
      vbox.getChildren().addAll(elements);
    }
    this.popup.setScene(new Scene(vbox, 500, 400));
    this.popup.setMinWidth(300);
    this.popup.setMinHeight(200);
    return this.popup;
  }

  public Stage getPopup() {
    return popup;
  }

}
