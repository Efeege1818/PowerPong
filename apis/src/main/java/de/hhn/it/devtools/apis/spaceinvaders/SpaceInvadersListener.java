package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;

/**
 * Models the capabilities of a GameListener.
 */
public interface SpaceInvadersListener {

  /**
   * Informs the listener that the barrier has changed.
   *
   * @param barrier the barrier with its current health state
   */
  void updateBarrier(Barrier barrier);

  /**
   * Informs the listener when aliens are updated.
   *
   * @param aliens the array of updated aliens
   */
  void updateAliens(Alien[] aliens);

  /**
   * Informs the listener when the ship is updated.
   *
   * @param ship the updated ship
   */
  void updateShip(Ship ship);

  /**
   * Informs the listener when an alien takes damage.
   *
   * @param alien the alien that took damage
   */
  void damageAlien(Alien alien);

  /**
   * Informs the listener when a sound is updated.
   *
   * @param sound the name of the updated sound
   */
  void updateSound(String sound);

  /**
   * Informs the listener that the game state has changed.
   *
   * @param gameState new state of the game
   */
  void changedGameState(GameState gameState);

  /**
   * Informs the listener when the round is updated.
   *
   * @param round the current round number
   */
  void updateRound(int round);

  /**
   * Informs the user that the game has ended.
   */
  void gameEnded();

  /**
   * Informs the listener that the score has changed.
   *
   * @param score new score value
   */
  void updateScore(int score);

  /**
   * Informs about a newly accepted game configuration.
   *
   * @param configuration new game configuration
   */
  void updateGameConfiguration(GameConfiguration configuration);

}
