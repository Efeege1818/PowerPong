package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;

/**
 * ProjectileListener triggered by ViewModel.
 */
public class ProjectileListener implements MapChangeListener<Integer, Projectile> {
  private final Image projectileImage = Images.projectileImage.getImage();
  private final CanvasProvider canvasProvider;

  /**
   * Constructor.
   *
   * @param canvasProvider canvasProvider.
   */
  public ProjectileListener(CanvasProvider canvasProvider) {
    this.canvasProvider = canvasProvider;
  }

  @Override
  public void onChanged(Change<? extends Integer, ? extends Projectile> change) {
    if (change.wasAdded() && change.wasRemoved()) {
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
              APIConstants.SHOT_HITBOX_SIZE);
      canvasProvider.drawEntity(this.projectileImage, change.getValueAdded().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
              APIConstants.SHOT_HITBOX_SIZE);
    } else if (change.wasAdded()) {
      canvasProvider.drawEntity(this.projectileImage, change.getValueAdded().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
              APIConstants.SHOT_HITBOX_SIZE);
    } else if (change.wasRemoved()) {
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(), APIConstants.SHOT_HITBOX_SIZE,
              APIConstants.SHOT_HITBOX_SIZE);
    }
  }
}
