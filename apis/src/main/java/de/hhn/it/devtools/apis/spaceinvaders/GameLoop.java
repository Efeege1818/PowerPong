package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Generic GameLoop interface.
 */
public interface GameLoop extends Runnable {

  @Override
  void run();

  /**
   * Pauses the game.
   */
  void pauseGame();

  /**
   * resumes a paused game.
   */
  void resumeGame();

  /**
   * Aborts a running game.
   */
  void abortGame();
}
