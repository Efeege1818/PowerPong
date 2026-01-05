package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Listener for Ship entity changes.
 */
public class ShipListener implements ChangeListener<Ship> {
  private final Image shipImage = Images.shipImage.getImage();
  private final CanvasProvider canvasProvider;
  private final ImageView leben1;
  private final ImageView leben2;
  private final ImageView leben3;

  /**
   * Constructor for ShipListener.
   *
   * @param canvasProvider CanvasProvider to draw on.
   */
  public ShipListener(CanvasProvider canvasProvider,
                      ImageView leben1, ImageView leben2, ImageView leben3) {
    this.canvasProvider = canvasProvider;
    this.leben1 = leben1;
    this.leben2 = leben2;
    this.leben3 = leben3;
  }

  @Override
  public void changed(ObservableValue<? extends Ship> observableValue, Ship ship, Ship newShip) {
    if (ship != null) {
      canvasProvider.clearEntity(ship.coordinate(), APIConstants.PLAYER_SIZE,
              APIConstants.PLAYER_SIZE);
    }
    canvasProvider.drawEntity(this.shipImage, newShip.coordinate(), APIConstants.PLAYER_SIZE,
            APIConstants.PLAYER_SIZE);

    if (ship != null && ship.hitPoints().equals(newShip.hitPoints())) {
      return; // no change in hit points, skip updating lives display
    }
    if (newShip.hitPoints() == 3) {
      leben1.setImage(Images.heart.getImage());
      leben2.setImage(Images.heart.getImage());
      leben3.setImage(Images.heart.getImage());
    } else if (newShip.hitPoints() == 2) {
      leben1.setImage(Images.heart_lost.getImage());
      leben2.setImage(Images.heart.getImage());
      leben3.setImage(Images.heart.getImage());
    } else if (newShip.hitPoints() == 1) {
      leben1.setImage(Images.heart_lost.getImage());
      leben2.setImage(Images.heart_lost.getImage());
      leben3.setImage(Images.heart.getImage());
    } else {
      leben1.setImage(Images.heart_lost.getImage());
      leben2.setImage(Images.heart_lost.getImage());
      leben3.setImage(Images.heart_lost.getImage());
    }
  }
}
