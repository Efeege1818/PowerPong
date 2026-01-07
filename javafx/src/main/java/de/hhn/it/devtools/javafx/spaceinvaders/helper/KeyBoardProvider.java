package de.hhn.it.devtools.javafx.spaceinvaders.helper;

import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Provides keyboard input handling for the Space Invaders game.
 */
public class KeyBoardProvider extends AnimationTimer {
  private final Timeline shooting = new Timeline(new KeyFrame(Duration.seconds(0.25)));
  private final Scene scene;
  private final SpaceInvadersService service;
  private final List<KeyCode> pressedKeys = new ArrayList<>();

  /**
   * Constructor for KeyBoardProvider.
   *
   * @param scene the scene to attach keyboard listeners to.
   */
  public KeyBoardProvider(Scene scene, SpaceInvadersService service) {
    this.scene = scene;
    this.service = service;
    initKeyListeners();
  }

  @Override
  public void handle(long l) {
    if (this.scene.getWindow().isFocused()) {
      if (pressedKeys.contains(KeyCode.LEFT) && !pressedKeys.contains(KeyCode.RIGHT)) {
        this.service.move(Direction.LEFT);
      } else if (pressedKeys.contains(KeyCode.RIGHT) && !pressedKeys.contains(KeyCode.LEFT)) {
        this.service.move(Direction.RIGHT);
      }
      if (pressedKeys.contains(KeyCode.SPACE)) {
        if (shooting.getStatus() == Animation.Status.STOPPED) {
          this.service.shoot();
          shooting.setCycleCount(1);
          shooting.play();
        }
      }
    } else {
      pressedKeys.clear();
    }
  }

  private void initKeyListeners() {
    scene.setOnKeyPressed(event -> {
      KeyCode code = event.getCode();
      if (code.equals(KeyCode.ESCAPE)) {
        this.service.pause();
        this.pressedKeys.clear();
      } else if (!pressedKeys.contains(code)) {
        pressedKeys.add(code);
      }
    });

    scene.setOnKeyReleased(event -> {
      KeyCode code = event.getCode();
      pressedKeys.remove(code);
    });
  }

}
