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
  private final Image alienImage3 = Images.alienImage3.getImage();
  private final Image alienImage2 = Images.alienImage2.getImage();
  private final Image alienImag1 = Images.alienImage1.getImage();
  private final Image explosion = Images.explosion.getImage();
  private Alien dummyAlien;
  private final CanvasProvider canvasProvider;

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
      canvasProvider.clearEntity(dummyAlien.coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
      dummyAlien = null;
    }
    if (change.wasAdded() && change.wasRemoved()) {
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
      drawAlien(change.getValueAdded());
    } else if (change.wasAdded()) {
      drawAlien(change.getValueAdded());
    } else if (change.wasRemoved()) {
      dummyAlien = change.getValueRemoved();
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
      canvasProvider.drawEntity(explosion, dummyAlien.coordinate(),
              APIConstants.HITBOX_SIZE,
              APIConstants.HITBOX_SIZE);
    }
  }

  private void drawAlien(Alien alien) {
    Image a = this.alienImage3;
    if (alien.hitPoints() == 1) {
      a = this.alienImag1;
    } else if (alien.hitPoints() == 2) {
      a = this.alienImage2;
    }
    canvasProvider.drawEntity(a, alien.coordinate(),
            APIConstants.HITBOX_SIZE,
            APIConstants.HITBOX_SIZE);
  }

}
