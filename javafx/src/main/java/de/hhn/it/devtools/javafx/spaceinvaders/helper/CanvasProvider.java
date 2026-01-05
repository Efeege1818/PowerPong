package de.hhn.it.devtools.javafx.spaceinvaders.helper;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * CanvasProvider class.
 */
public class CanvasProvider {
  private final Canvas canvas;

  /**
   * Constructor for CanvasProvider.
   *
   * @param canvas the canvas to provide.
   */
  public CanvasProvider(Canvas canvas) {
    this.canvas = canvas;
  }

  /**
   * Clear Entity.
   *
   * @param coordinate coordinate from entity.
   * @param a size x.
   * @param b size y.
   */
  public void clearEntity(Coordinate coordinate, int a, int b) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.clearRect(coordinate.x(), coordinate.y(), a, b);
  }

  /**
   * Draw Entity.
   *
   * @param image image from entity.
   * @param coordinate coordinate from entity.
   * @param a size x.
   * @param b size y.
   */
  public void drawEntity(Image image, Coordinate coordinate, int a, int b) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(image, coordinate.x() + 0.5, coordinate.y() + 0.5, a, b);
  }

  /**
   * Draw Entity.
   *
   * @param color color from entity.
   * @param coordinate coordinate from entity.
   */
  public void drawEntity(Color color, Coordinate coordinate) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(color);
    gc.fillRect(coordinate.x(), coordinate.y(), 1, 1);
  }

}
