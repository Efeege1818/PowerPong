package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;

/**
 * ProjectileListener triggered by ViewModel.
 */
public class ProjectileListener implements MapChangeListener<Integer, Projectile> {
  private final Image shipProjectileImage = Images.projectileImage.getImage();
  private final Image alienProjectileImage = Images.alien_shot.getImage();
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
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(),
              APIConstants.SHOT_HITBOX_SIZE,
              APIConstants.SHOT_HITBOX_SIZE);
      drawProjectile(change.getValueAdded());
    } else if (change.wasAdded()) {
      drawProjectile(change.getValueAdded());
    } else if (change.wasRemoved()) {
      canvasProvider.clearEntity(change.getValueRemoved().coordinate(),
              APIConstants.SHOT_HITBOX_SIZE,
              APIConstants.SHOT_HITBOX_SIZE);
    }
  }

  private void drawProjectile(Projectile projectile) {
    Image p = this.alienProjectileImage;
    if (projectile.direction().equals(Direction.UP)) {
      p = this.shipProjectileImage;
    }
    canvasProvider.drawEntity(p, projectile.coordinate(),
            APIConstants.SHOT_HITBOX_SIZE,
            APIConstants.SHOT_HITBOX_SIZE);
  }

}
