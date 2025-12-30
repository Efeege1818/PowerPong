package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

/**
 * Listener for Ship entity changes.
 */
public class ShipListener implements ChangeListener<Ship> {
  private final Image shipImage = Images.shipImage.getImage();
  private final CanvasProvider canvasProvider;

  /**
   * Constructor for ShipListener.
   *
   * @param canvasProvider CanvasProvider to draw on.
   */
  public ShipListener(CanvasProvider canvasProvider) {
    this.canvasProvider = canvasProvider;
  }

  @Override
  public void changed(ObservableValue<? extends Ship> observableValue, Ship ship, Ship newShip) {
    canvasProvider.clearEntity(ship.coordinate(), APIConstants.PLAYER_SIZE,
            APIConstants.PLAYER_SIZE);
    canvasProvider.drawEntity(this.shipImage, newShip.coordinate(), APIConstants.PLAYER_SIZE,
            APIConstants.PLAYER_SIZE);
  }
}
