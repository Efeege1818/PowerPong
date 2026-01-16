package de.hhn.it.devtools.javafx.shapesurvivor.view;

import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.components.shapesurvivor.GameMap;
import de.hhn.it.devtools.apis.shapesurvivor.Obstacle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * Renders the game map including obstacles with camera support.
 */
public class MapRenderer {

  private static final Image BUSH_IMAGE =
          new Image(
                  Objects.requireNonNull(
                          MapRenderer.class.getResourceAsStream(
                                  "/fxml/shapesurvivor/bush.png"
                          )
                  )
          );
  private static final long WORLD_SEED = 12345L;
  private static final int DECAL_GRID = 50;
  public void renderMap(GraphicsContext gc, GameMap map, Position cameraPos) {
    if (map == null) return;

    int canvasWidth = (int) gc.getCanvas().getWidth();
    int canvasHeight = (int) gc.getCanvas().getHeight();

    // Render background
    renderBackground(gc, cameraPos, canvasWidth, canvasHeight);
    // Render decals
    renderGroundDecals(gc, cameraPos, canvasWidth, canvasHeight);
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
        case BUSH -> renderBush(gc, screenX, screenY);
      }
    }
  }

  private void renderRock(GraphicsContext gc, int x, int y, int width, int height) {
    double cx = x + width / 2.0;
    double cy = y + (height / 2.0) - 0.2;

    double[] px = {
            cx - 16, cx - 8, cx + 12, cx + 18, cx + 10, cx - 14
    };
    double[] py = {
            cy + 12,  cy + 10, cy + 11, cy + 8,  cy - 12,  cy - 10
    };

    // Shadow
    gc.setFill(Color.rgb(0, 0, 0, 0.25));
    gc.fillOval(cx - 14, cy + 6, 28, 6);

    // Base
    gc.setFill(Color.rgb(140, 145, 155));
    gc.fillPolygon(px, py, px.length);

    // Outline
    gc.setStroke(Color.rgb(90, 95, 105));
    gc.setLineWidth(1);
    gc.strokePolygon(px, py, px.length);
  }

  private void renderTree(GraphicsContext gc, int x, int y, int width, int height) {


    int centerX = x + width / 2;
    int centerY = y + height / 2;

    // Shadow
    gc.setFill(Color.rgb( 0, 0, 0, 0.25));
    gc.fillOval(centerX - 20, centerY + 16, 40, 16);

    // Trunk
    gc.setFill(Color.rgb( 130, 90, 50));
    gc.fillRect(centerX - 6, centerY, 12, 20);

    // Canopy
    gc.setFill(Color.rgb(2, 66, 15));
    gc.fillOval(centerX - 32, centerY - 40, 64, 52);

  }

  private void renderWall(GraphicsContext gc, int x, int y, int width, int height) {
    // Base
    gc.setFill(Color.rgb(112, 128, 144));
    gc.fillRect(x, y, width, height);

    // CLIP START
    gc.save();
    gc.beginPath();
    gc.rect(x, y, width, height);
    gc.clip();

    // Brick pattern
    int brickWidth = 30;
    int brickHeight = 10;

    gc.setStroke(Color.rgb(70, 80, 90));
    gc.setLineWidth(1);

    for (int by = 0; by < height; by += brickHeight) {
      int offset = ((by / brickHeight) % 2) * (brickWidth / 2);
      for (int bx = -offset; bx < width; bx += brickWidth) {
        gc.strokeRect(x + bx, y + by, brickWidth, brickHeight);
      }
    }

    // CLIP END
    gc.restore();

    // Border (drawn after clipping!)
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

  private void renderBush(GraphicsContext gc, int x, int y){
    gc.drawImage(BUSH_IMAGE, x, y, 100, 100);
  }

  private void renderGroundDecals(GraphicsContext gc, Position cameraPos, int width, int height) {
    int worldLeft = cameraPos.x() - width / 2;
    int worldTop  = cameraPos.y() - height / 2;

    int startX = floorToGrid(worldLeft, DECAL_GRID);
    int startY = floorToGrid(worldTop, DECAL_GRID);

    int endX = worldLeft + width + DECAL_GRID;
    int endY = worldTop + height + DECAL_GRID;

    for (int wx = startX; wx <= endX; wx += DECAL_GRID) {
      for (int wy = startY; wy <= endY; wy += DECAL_GRID) {

        long h = hash2D(wx, wy, WORLD_SEED);
        // dirt, grass or pebbles
        int kind = (int) ((h >>> 16) % 3);

        if ((h & 0xFF) > 50) continue;

        int sx = wx - cameraPos.x() + width / 2;
        int sy = wy - cameraPos.y() + height / 2;

        int size = 12 + (int) ((h >>> 8) % 25);


        if (kind == 0) { // dirt
          gc.setFill(Color.rgb(87, 42, 3, 0.17));
          gc.fillOval(sx - size / 2, sy - size / 3, size, (int) (size * 0.55));
        } else if (kind == 1){ // grass
          gc.setFill(Color.rgb(11, 64, 4, 0.09));
          gc.fillOval(sx - size / 2, sy - size / 3, size, (int) (size * 0.60));
        } else {
          gc.setFill(Color.rgb(133, 133, 133, 0.5));

          int count = 3 + (int) ((h >>> 20) % 5); // 3-7 pebbles
          int baseR = 2 + (int) ((h >>> 24) % 3);

          for (int i = 0; i < count; i++) {
            long hi = hash2D(wx + i * 31, wy + i * 17, WORLD_SEED);

            int dx = (int) ((hi >>> 8) % 14) - 7;
            int dy = (int) ((hi >>> 12) % 10) - 5;
            int r  = baseR + (int) (hi % 2);

            gc.fillOval(sx + dx - r, sy + dy - r, 2 * r, 2 * r);
          }
        }
      }
    }
  }

  private static int floorToGrid(int v, int grid) {
    int r = v % grid;
    if (r < 0) r += grid;
    return v - r;
  }

  private static long hash2D(int x, int y, long seed) {
    long h = seed;
    h ^= (long) x * 0x9E3779B97F4A7C15L;
    h ^= (long) y * 0xC2B2AE3D27D4EB4FL;
    h ^= (h >>> 33);
    h *= 0xFF51AFD7ED558CCDL;
    h ^= (h >>> 33);
    h *= 0xC4CEB9FE1A85EC53L;
    h ^= (h >>> 33);
    return h;
  }
}