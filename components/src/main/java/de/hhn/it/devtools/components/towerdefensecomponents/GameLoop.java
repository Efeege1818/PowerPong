package de.hhn.it.devtools.components.towerdefensecomponents;

/**
 * This Class runs in a loop every game tick and manages everything that has to run constantly.
 * This Class should call the tick() Method in the GameLoop once per Game-Tick.
 */
public class GameLoop extends Thread {

  private boolean running = false;

  private SimpleTowerDefenseService service;

  public GameLoop(SimpleTowerDefenseService service) {
    this.service = service;
  }

  @Override
  public void run() {
    while (running) {
      service.tick();
    }
  }

  /**
   * Starts the game and begins calling the tick() Method.
   *
   * @throws IllegalStateException if game is already running
   */
  public void startGame() throws IllegalStateException {
    if (running) {
      throw new IllegalStateException("GameLoop is already running");
    }
    running = true;
    this.start();
  }

  /**
   * Halts game and makes it possible to resume by calling startGame() again.
   *
   * @throws IllegalStateException if game is not running
   */
  public void stopGame() throws IllegalStateException {
    if (!running) {
      throw new IllegalStateException("GameLoop is not running");
    }
    running = false;
  }

}
