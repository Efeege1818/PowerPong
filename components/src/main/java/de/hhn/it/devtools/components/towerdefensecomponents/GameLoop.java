package de.hhn.it.devtools.components.towerdefensecomponents;

/**
 * This Class runs in a loop every game tick and manages everything that has to run constantly.
 * This Class calls the tick() Method in the GameLoop once per Game-Tick.
 */
public class GameLoop extends Thread {

  private boolean running = false;
  private boolean started = false;
  private final int tickspeed = 1000;

  private final SimpleTowerDefenseService service;

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(GameLoop.class);

  /**
   * Constructor.
   *
   * @param service the Service that this GameLoop should affect
   */
  public GameLoop(SimpleTowerDefenseService service) {
    this.service = service;
  }

  @Override
  public void run() {
    while (running) {
      logger.debug("tick");
      service.tick();
      try {
        sleep(tickspeed);
      } catch (InterruptedException e) {
        logger.error("Game Loop was Interrupted");
        throw new RuntimeException(e);
      }
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
    if (!started) {
      this.start();
      started = true;
    }
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
