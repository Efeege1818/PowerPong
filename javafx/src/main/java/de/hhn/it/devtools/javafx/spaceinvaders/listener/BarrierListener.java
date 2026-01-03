package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.Images;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.CanvasProvider;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

/**
 * BarrierListener triggered by ViewModel.
 */
public class BarrierListener implements MapChangeListener<Integer, Barrier> {
  private final Image barrier = Images.barrierImage.getImage();
  private final CanvasProvider canvasProvider;

  /**
   * Constructor for BarrierListener.
   *
   * @param canvasProvider canvas provider to draw on.
   */
  public BarrierListener(CanvasProvider canvasProvider) {
    this.canvasProvider = canvasProvider;
  }

  @Override
  public void onChanged(Change<? extends Integer, ? extends Barrier> barrier) {
    if (barrier.wasAdded()) {
      int size = APIConstants.BARRIER_HITBOX_HEIGHT * APIConstants.BARRIER_HITBOX_WIDTH;
      Barrier bar = barrier.getValueAdded();
      if (bar.barrierId() > size * 3) {
        return; // invalid barrier id, skip drawing
      } else if (bar.barrierId() > size * 2) {
        int s = bar.barrierId() - (size * 2);
        int x = s % APIConstants.BARRIER_HITBOX_WIDTH;
        int y = s / APIConstants.BARRIER_HITBOX_WIDTH;
        Color color = getSafeColor(barrierImage(), x, y);
        canvasProvider.drawEntity(color, bar.coordinate());
      } else if (bar.barrierId() > size) {
        int s = bar.barrierId() - (size);
        int x = s % APIConstants.BARRIER_HITBOX_WIDTH;
        int y = s / APIConstants.BARRIER_HITBOX_WIDTH;
        Color color = getSafeColor(barrierImage(), x, y);
        canvasProvider.drawEntity(color, bar.coordinate());
      } else {
        int s = bar.barrierId();
        int x = s % APIConstants.BARRIER_HITBOX_WIDTH;
        int y = s / APIConstants.BARRIER_HITBOX_WIDTH;
        Color color = getSafeColor(barrierImage(), x, y);
        canvasProvider.drawEntity(color, bar.coordinate());
      }
    } else if (barrier.wasRemoved()) {
      canvasProvider.clearEntity(barrier.getValueRemoved().coordinate(), 1, 1);
    }
  }

  private Image barrierImage() {
    return this.barrier;
  }

  private Color getSafeColor(Image img, int x, int y) {
    if (img == null) {
      return Color.TRANSPARENT;
    }
    PixelReader reader = img.getPixelReader();
    if (reader == null) {
      return Color.TRANSPARENT;
    }
    int width = (int) img.getWidth();
    int height = (int) img.getHeight();
    if (x < 0 || y < 0 || x >= width || y >= height) {
      return Color.TRANSPARENT;
    }
    return reader.getColor(x, y);
  }
}

