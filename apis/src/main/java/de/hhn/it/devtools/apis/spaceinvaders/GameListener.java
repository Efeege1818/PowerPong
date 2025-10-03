package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Models the capabilities of a GameListener.
 */
public interface GameListener {

  /**
   * Informs the listener that the field has changed.
   *
   * @param field field with the actual position of Entities elements
   */
  void updateBoard(Field field);

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
