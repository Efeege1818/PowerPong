package de.hhn.it.devtools.components.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.GameState;

/**
 * Simple GameLoop for the game SpaceInvaders.
 */
public class SimpleGameLoop extends Thread {
  private final SimpleSpaceInvadersService service;
  private static final int TICKS_PER_SECOND = 20;

  /**
   * Default Constructor.
   *
   * @param service SpaceInvadersService to be notified every loop.
   */
  public SimpleGameLoop(SimpleSpaceInvadersService service) {
    this.service = service;
  }

  @Override
  public void run() {
    while (!this.isInterrupted()) {
      synchronized (this) {
        if (service.getGameState().equals(GameState.PAUSED)) {
          try {
            this.wait();
          } catch (InterruptedException e) {
            return;
          }
        }
      }
      service.triggeredByGameLoop();
      try {
        wait(TICKS_PER_SECOND);
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  /**
   * Stop GameLoop.
   */
  public void stopGame() {
    interrupt();
  }

}
