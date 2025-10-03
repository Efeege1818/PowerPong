package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Generic GameLoop interface.
 */
public interface Gameloop extends Runnable{
  @Override
  void run();

  void pauseGame();

  void resumeGame();

  void stopGame();
}
