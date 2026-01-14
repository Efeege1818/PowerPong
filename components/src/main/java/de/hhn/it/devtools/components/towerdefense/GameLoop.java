package de.hhn.it.devtools.components.towerdefense;

/**
 * This Class runs in a loop every game tick and manages everything that has to run constantly.
 * This Class calls the tick() Method in the GameLoop once per Game-Tick.
 */
public class GameLoop extends Thread {

  private boolean running = false;
  private final int tickspeed = 100;

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
    start();
    logger.info("Started Game Loop on thread: {}", this.threadId());
  }

  @Override
  public void run() {
    outer: while (true) {

      synchronized (this) {
        try {
          this.wait();
        } catch (InterruptedException e) {
          logger.error("Game Loop wait was Interrupted");
          break outer;
        }
      }

      while (running) {
        logger.debug("tick");
        service.tick();
        try {
          sleep(tickspeed);
        } catch (InterruptedException e) {
          logger.error("Game Loop sleep was Interrupted");
          break outer;
        }
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
    synchronized (this) {
      notify();
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

  public boolean isRunning() {
    return running;
  }
}
