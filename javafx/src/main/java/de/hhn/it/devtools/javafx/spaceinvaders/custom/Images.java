package de.hhn.it.devtools.javafx.spaceinvaders.custom;

import de.hhn.it.devtools.apis.spaceinvaders.APIConstants;
import java.net.URL;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enum for Images.
 */
public enum Images {
  alienImage1(loadImageSafe("/spaceinvaders/images/alien_1.png", APIConstants.HITBOX_SIZE,
          APIConstants.HITBOX_SIZE)),
  alienImage2(loadImageSafe("/spaceinvaders/images/alien_2.png", APIConstants.HITBOX_SIZE,
          APIConstants.HITBOX_SIZE)),
  alienImage3(loadImageSafe("/spaceinvaders/images/alien_3.png", APIConstants.HITBOX_SIZE,
          APIConstants.HITBOX_SIZE)),
  alienShot(loadImageSafe("/spaceinvaders/images/alien_shot.png", APIConstants.HITBOX_SIZE,
          APIConstants.HITBOX_SIZE)),
  shipImage(loadImageSafe("/spaceinvaders/images/ship.png", APIConstants.HITBOX_SIZE,
          APIConstants.HITBOX_SIZE)),
  logo(loadImageSafe("/spaceinvaders/images/logo.png", 200, 150)),
  projectileImage(loadImageSafe("/spaceinvaders/images/shot.png",
          APIConstants.SHOT_HITBOX_SIZE, APIConstants.SHOT_HITBOX_SIZE)),
  barrierImage(loadImageSafe("/spaceinvaders/images/barrier.png",
          APIConstants.BARRIER_HITBOX_WIDTH, APIConstants.BARRIER_HITBOX_HEIGHT)),
  background(new Image(Images.class
          .getResource("/spaceinvaders/images/background.png").toExternalForm())),
  settingsImage(loadImageSafe("/spaceinvaders/images/setting.png", 25, 25));
  private static final String TRANSPARENT_PNG_DATA =
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAA"
                  + "AC1HAwCAAAAC0lEQVQImWNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=";

  final javafx.scene.image.Image image;

  Images(javafx.scene.image.Image image) {
    this.image = image;
  }

  public javafx.scene.image.Image getImage() {
    return image;
  }

  private static javafx.scene.image.Image loadImageSafe(String path, int width, int height) {
    Logger logger = LoggerFactory.getLogger(Images.class);
    try {
      URL res = Images.class.getResource(path);
      if (res == null) {
        logger.warn("Resource not found: {}", path);
        // return a small transparent image to avoid NPEs in draw calls
        return new javafx.scene.image.Image(TRANSPARENT_PNG_DATA, width, height, true, true);
      }
      return new javafx.scene.image.Image(res.toExternalForm(), width, height, true, true);
    } catch (Exception e) {
      logger.warn("Failed to load image {}: {}", path, e.getMessage());
      return new javafx.scene.image.Image(TRANSPARENT_PNG_DATA, width, height, true, true);
    }
  }

}
