package de.hhn.it.devtools.components.towerdefensecomponents;

/**
 * Independent Thread, that manages everything that runs constantly,
 * and isn't directly tied to user inputs.
 */
public interface GameLoop extends Runnable {

  @Override
  void run();

  /**
   * Triggers calculation of the next game-tick.
   *
   * @throws IllegalStateException if game is not running
   */
  void updateGame() throws IllegalStateException;

  /**
   * End game tick, no tick after this is possible.
   *
   * @throws IllegalStateException if game is not running
   */
  void endGame();

  /**
   * Saves last game round and makes it possible to retry that round if failed.
   *
   * @throws IllegalStateException if game is not running
   */
  void retry();

  /**
   * Halts game and makes it possible to resume.
   *
   * @throws IllegalStateException if game is not running
   */
  void pauseGame();
}
