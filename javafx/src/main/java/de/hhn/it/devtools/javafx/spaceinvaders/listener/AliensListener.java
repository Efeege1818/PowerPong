package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;

/**
 * AliensListener triggered by ViewModel.
 */
public class AliensListener implements MapChangeListener<Integer, Alien> {
  private final Image alienImage = Images.alienImage1.getImage();
  private final CanvasProvider canvasProvider;
  private Alien dummyAlien;

  /**
   * Constructor for AliensListener.
   *
   * @param canvasProvider the CanvasProvider to draw on.
   */
  public AliensListener(CanvasProvider canvasProvider) {
    this.canvasProvider = canvasProvider;

  }

  @Override
  public void onChanged(Change<? extends Integer, ? extends Alien> change) {
    if (dummyAlien != null) {
      canvasProvider.drawEntity(this.alienImage, dummyAlien.coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
      dummyAlien = null;
    }
    if (change.wasAdded() && change.wasRemoved()) {
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
      canvasProvider.drawEntity(this.alienImage, change.getValueAdded().coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    } else if (change.wasAdded()) {
      canvasProvider.drawEntity(this.alienImage, change.getValueAdded().coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    } else if (change.wasRemoved()) {
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    }
  }
}
