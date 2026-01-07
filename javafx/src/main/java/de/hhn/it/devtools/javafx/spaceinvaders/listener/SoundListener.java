package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.scene.media.AudioClip;

/**
 * SoundListener triggered by ViewModel.
 */
public class SoundListener implements PropertyChangeListener {
  private final AudioClip shootSound = new AudioClip(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.SHOOT.getSound()).toExternalForm());
  private final AudioClip hitSound = new AudioClip(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.HIT.getSound()).toExternalForm());
  private final AudioClip explosionSound = new AudioClip(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.EXPLOSION.getSound()).toExternalForm());
  private final AudioClip gameOverSound = new AudioClip(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.GAMEOVER.getSound()).toExternalForm());
  private final AudioClip levelUpSound = new AudioClip(getClass()
          .getResource("/spaceinvaders/sounds/" + Sound.LEVELUP.getSound()).toExternalForm());

  /**
   * Constructor.
   */
  public SoundListener() {
    this.shootSound.setVolume(0.15);
    this.hitSound.setVolume(0.05);
    this.explosionSound.setVolume(0.05);
    this.gameOverSound.setVolume(0.15);
    this.levelUpSound.setVolume(0.15);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getPropertyName().equalsIgnoreCase("Sound")) {
      Sound sound = (Sound) event.getNewValue();
      switch (sound) {
        case Sound.SHOOT -> this.shootSound.play();
        case Sound.HIT -> this.hitSound.play();
        case Sound.EXPLOSION -> this.explosionSound.play();
        case Sound.GAMEOVER -> this.gameOverSound.play();
        case Sound.LEVELUP -> this.levelUpSound.play();
        default -> {}
      }
    }
  }



}
