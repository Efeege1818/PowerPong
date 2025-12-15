package de.hhn.it.devtools.components.towerdefensecomponents;

//LOCKED : M.Albert
/**
 * This Class runs in a loop every game tick and manages everything that has to run constantly.
 * This Class should call the tick() Method in the GameLoop once per Game-Tick.
 */
public class GameLoop extends Thread {

  @Override
  public void run() {

  }

  /**
   * Starts the game and begins calling the tick() Method.
   *
   * @throws IllegalStateException if game is already running
   */
  public void startGame() throws IllegalStateException {

  }

  /**
   * Halts game and makes it possible to resume by calling startGame() again.
   *
   * @throws IllegalStateException if game is not running
   */
  public void stopGame() throws IllegalStateException {

  }

}
