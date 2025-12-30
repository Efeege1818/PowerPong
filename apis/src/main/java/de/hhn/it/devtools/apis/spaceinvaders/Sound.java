package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Sound's for SpaceInvaders.
 */
public enum Sound {
  SHOOT(""),
  GRRR("secret.mp3"),
  CHICKEN("Chicken.mp3"),
  ;

  final String sound;

  Sound(String sound) {
    this.sound = sound;
  }

  public String getSound() {
    return sound;
  }
}
