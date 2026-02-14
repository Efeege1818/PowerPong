package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Sound's for SpaceInvaders.
 */
public enum Sound {
  /**
   * Shoot sound.
   */
  SHOOT("shoot.mp3"),
  /**
   * Grrr sound.
   */
  GRRR("secret.mp3"),
  /**
   * Chicken sound.
   */
  CHICKEN("Chicken.mp3"),
  /**
   * Hit sound.
   */
  HIT("hit.mp3"),
  /**
   * Track sound.
   */
  TRACK("track.mp3"),
  /**
   * GameOver Sound.
   */
  GAMEOVER("GameOver.mp3"),
  /**
   * LevelPassed Sound.
   */
  LEVELUP("LevelPassed.mp3"),
  /**
   * Explosion Sound.
   */
  EXPLOSION("explosion.mp3"),
  /**
   * PlayerHit Sound.
   */
  PLAYER_HIT("player_hit.mp3");

  final String sound;

  Sound(String sound) {
    this.sound = sound;
  }

  /***
   * Getter for the sound file name.
   *
   * @return the name of the sound file associated with this Sound enum constant
   */
  public String getSound() {
    return sound;
  }
}
