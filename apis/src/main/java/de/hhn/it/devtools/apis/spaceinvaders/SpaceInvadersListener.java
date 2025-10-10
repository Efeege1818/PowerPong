package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Models the capabilities of a GameListener.
 */
public interface SpaceInvadersListener {

  /**
   * Informs the listener that the field has changed.
   *
   * @param field field with the actual position of Entities elements
   */
  void updateField(Field field);

  /**
   * Informs the listener when an alien is updated.
   */
  void updateAlien();

  /**
   * Informs the listener when an alien takes damage.
   */
  void damageAlien();

  /**
   * Informs the listener when a sound is updated.
   *
   * @param sound the name of the updated sound
   */
  void updateSound(String sound);

  /**
   * Informs the listener that the game state has changed.
   *
   * @param gameState new state of the game
   */
  void changedGameState(GameState gameState);

  /**
   * Informs the user that the game has ended.
   */
  void gameEnded();

  /**
   * Informs the listener that the score has changed.
   *
   * @param score new score value
   */
  void updateScore(int score);

  /**
   * Informs about a newly accepted game configuration.
   *
   * @param configuration new game configuration
   */
  void updateGameConfiguration(GameConfiguration configuration);

}
