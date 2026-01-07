package de.hhn.it.devtools.components.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.components.spaceinvaders.utils.Constants;

/**
 * Simple GameLoop for the game SpaceInvaders.
 */
public class SimpleGameLoop extends Thread {
  private final SimpleSpaceInvadersService service;
  double speedModifier;

  /**
   * Default Constructor.
   *
   * @param service SpaceInvadersService to be notified every loop.
   */
  public SimpleGameLoop(SimpleSpaceInvadersService service) {
    this.service = service;
    switch (service.getDifficulty()) {
      case Difficulty.EASY -> speedModifier =  Constants.THREAD_WAIT * 1.2;
      case Difficulty.NORMAL -> speedModifier =  Constants.THREAD_WAIT;
      case Difficulty.HARD -> speedModifier =  Constants.THREAD_WAIT * 0.8;
      default -> {}
    }
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
        synchronized (this) {
          wait(Constants.THREAD_WAIT);
        }
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
