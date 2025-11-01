package de.hhn.it.devtools.components.towerdefensecomponents;

/**
 * Independent Thread, that manages everything that runs constantly,
 * and isn't directly tied to user inputs.
 */
public interface GameLoop extends Runnable {

  @Override
  void run();

  void endGame();

  void retry();

  void pauseGame();
}
