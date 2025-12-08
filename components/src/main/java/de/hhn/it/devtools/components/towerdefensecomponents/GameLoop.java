package de.hhn.it.devtools.components.towerdefensecomponents;

//LOCKED : M.Albert
/**
 * This Class runs in a loop every game tick and manages everything that has to run constantly.
 */
public class GameLoop extends Thread{
  @Override
  public void run() {

  }


  /**
   * Triggers calculation of the next game-tick.
   *
   * @throws IllegalStateException if game is not running
   */
  public void updateGame() throws IllegalStateException {

  }

  /**
   * End game tick, no tick after this is possible.
   *
   * @throws IllegalStateException if game is not running
   */
  public void endGame() throws IllegalStateException {

  }

  /**
   * Saves last game round and makes it possible to retry that round if failed.
   *
   * @throws IllegalStateException if game is not running
   */
  public void retry() throws IllegalStateException {

  }

  /**
   * Halts game and makes it possible to resume.
   *
   * @throws IllegalStateException if game is not running
   */
  public void pauseGame() throws IllegalStateException {

  }

  /**
   * Resumes the game or starts the next round if possible.
   *
   * @throws IllegalStateException if game is running
   */
  public void resumeRound() throws IllegalStateException {

  }
}
