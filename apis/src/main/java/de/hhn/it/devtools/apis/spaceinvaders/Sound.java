package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Sound's for SpaceInvaders.
 */
public enum Sound {
  SHOOT("shoot.mp3"),
  GRRR("secret.mp3"),
  CHICKEN("Chicken.mp3"),
  HIT("hit.mp3"),
  TRACK("track.mp3"),
  EXPLOSION("explosion.mp3");

  final String sound;

  Sound(String sound) {
    this.sound = sound;
  }

  public String getSound() {
    return sound;
  }
}
