package de.hhn.it.devtools.javafx.shapesurvivor.view;

import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.components.shapesurvivor.GameMap;
import de.hhn.it.devtools.apis.shapesurvivor.Obstacle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders the game map including obstacles with camera support.
 */
public class MapRenderer {

  public void renderMap(GraphicsContext gc, GameMap map, Position cameraPos) {
    if (map == null) return;

    int canvasWidth = (int) gc.getCanvas().getWidth();
    int canvasHeight = (int) gc.getCanvas().getHeight();

    // Render background
    renderBackground(gc, cameraPos, canvasWidth, canvasHeight);

    // Render only visible obstacles
    renderObstacles(gc, map, cameraPos, canvasWidth, canvasHeight);
  }

  private void renderBackground(GraphicsContext gc, Position cameraPos, int width, int height) {
    // Base grass color
    gc.setFill(Color.rgb(34, 139, 34));
    gc.fillRect(0, 0, width, height);

    // Add subtle grid pattern for depth perception
    gc.setStroke(Color.rgb(30, 120, 30, 0.3));
    gc.setLineWidth(1);

    int gridSize = 50;

    // Calculate offset based on camera position
    int offsetX = cameraPos.x() % gridSize;
    int offsetY = cameraPos.y() % gridSize;

    // Draw vertical lines
    for (int x = -offsetX; x < width; x += gridSize) {
      gc.strokeLine(x, 0, x, height);
    }

    // Draw horizontal lines
    for (int y = -offsetY; y < height; y += gridSize) {
      gc.strokeLine(0, y, width, y);
    }
  }

  private void renderObstacles(GraphicsContext gc, GameMap map, Position cameraPos,
                               int canvasWidth, int canvasHeight) {
    // Get only obstacles visible in current view
    for (Obstacle obstacle : map.getVisibleObstacles(cameraPos, canvasWidth, canvasHeight)) {
      // Convert world coordinates to screen coordinates
      int screenX = obstacle.xpos() - cameraPos.x() + canvasWidth / 2;
      int screenY = obstacle.ypos() - cameraPos.y() + canvasHeight / 2;

      switch (obstacle.type()) {
        case ROCK -> renderRock(gc, screenX, screenY, obstacle.width(), obstacle.height());
        case TREE -> renderTree(gc, screenX, screenY, obstacle.width(), obstacle.height());
        case WALL -> renderWall(gc, screenX, screenY, obstacle.width(), obstacle.height());
        case PILLAR -> renderPillar(gc, screenX, screenY, obstacle.width(), obstacle.height());
      }
    }
  }

  private void renderRock(GraphicsContext gc, int x, int y, int width, int height) {
    // Main rock body
    gc.setFill(Color.rgb(105, 105, 105));
    gc.fillOval(x, y, width, height);

    // Highlight
    gc.setFill(Color.rgb(169, 169, 169));
    gc.fillOval(x + 5, y + 3, (double) width / 3, (double) height / 3);

    // Shadow
    gc.setFill(Color.rgb(70, 70, 70));
    gc.fillOval(x + width - 10, y + height - 10, (double) width / 4, (double) height / 4);

    // Border
    gc.setStroke(Color.rgb(85, 85, 85));
    gc.setLineWidth(2);
    gc.strokeOval(x, y, width, height);
  }

  private void renderTree(GraphicsContext gc, int x, int y, int width, int height) {
    int centerX = x + width / 2;
    int centerY = y + height / 2;

    // Tree trunk
    gc.setFill(Color.rgb(101, 67, 33));
    gc.fillRect(centerX - 3, centerY - 2, 6, height);

    // Tree foliage (3 circles)
    gc.setFill(Color.rgb(34, 139, 34));
    gc.fillOval(centerX - 10, centerY - 12, 20, 20);
    gc.fillOval(centerX - 12, centerY - 8, 24, 20);
    gc.fillOval(centerX - 8, centerY - 4, 16, 16);

    // Darker outline
    gc.setStroke(Color.rgb(0, 100, 0));
    gc.setLineWidth(1);
    gc.strokeOval(centerX - 10, centerY - 12, 20, 20);
  }

  private void renderWall(GraphicsContext gc, int x, int y, int width, int height) {
    // Wall base
    gc.setFill(Color.rgb(112, 128, 144));
    gc.fillRect(x, y, width, height);

    // Brick pattern
    gc.setStroke(Color.rgb(70, 80, 90));
    gc.setLineWidth(2);

    int brickWidth = 30;
    int brickHeight = 10;

    for (int by = 0; by < height; by += brickHeight) {
      int offset = (by / brickHeight % 2) * (brickWidth / 2);
      for (int bx = -offset; bx < width; bx += brickWidth) {
        gc.strokeRect(x + bx, y + by, brickWidth, brickHeight);
      }
    }

    // Border
    gc.setStroke(Color.rgb(50, 60, 70));
    gc.setLineWidth(3);
    gc.strokeRect(x, y, width, height);
  }

  private void renderPillar(GraphicsContext gc, int x, int y, int width, int height) {
    // Pillar body
    gc.setFill(Color.rgb(139, 137, 137));
    gc.fillRect(x, y, width, height);

    // Capital (top)
    gc.setFill(Color.rgb(169, 169, 169));
    gc.fillRect(x - 3, y, width + 6, 5);

    // Base (bottom)
    gc.fillRect(x - 3, y + height - 5, width + 6, 5);

    // Highlight
    gc.setFill(Color.rgb(192, 192, 192));
    gc.fillRect(x, y + 5, 3, height - 10);
  }
}