package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.scene.media.AudioClip;

/**
 * SoundListener triggered by ViewModel.
 */
public class SoundListener implements PropertyChangeListener {
  private final AudioClip shootSound = new AudioClip(getClass().getResource("/spaceinvaders/sounds/"
          + Sound.SHOOT.getSound()).toExternalForm());
  private final AudioClip hitSound = new AudioClip(getClass().getResource("/spaceinvaders/sounds/"
          + Sound.HIT.getSound()).toExternalForm());

  /**
   * Constructor.
   */
  public SoundListener() {
    this.shootSound.setVolume(0.5);
    this.hitSound.setVolume(0.5);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getPropertyName().equalsIgnoreCase("Sound")) {
      Sound sound = (Sound) event.getNewValue();
      switch (sound) {
        case Sound.SHOOT -> this.shootSound.play();
        case Sound.HIT -> this.hitSound.play();
        default -> {}
      }
    }
  }



}
