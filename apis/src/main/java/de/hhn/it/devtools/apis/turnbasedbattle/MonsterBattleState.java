package de.hhn.it.devtools.apis.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import java.util.HashMap;

/**
 * Read-only view of a Monster's state during battle.
 * This interface provides access to both static monster data and runtime battle state
 * without exposing the concrete implementation.
 */
public interface MonsterBattleState {

  /**
   * Gets the name of the monster.
   *
   * @return the monster's name
   */
  String getName();

  /**
   * Gets the maximum HP of the monster.
   *
   * @return the maximum HP
   */
  int getMaxHp();

  /**
   * Gets the current HP of the monster.
   *
   * @return the current HP
   */
  int getCurrentHp();

  /**
   * Gets the attack stat of the monster.
   *
   * @return the attack stat
   */
  int getAttack();

  /**
   * Gets the defense stat of the monster.
   *
   * @return the defense stat
   */
  int getDefense();

  /**
   * Gets the element of the monster.
   *
   * @return the element
   */
  Element getElement();

  /**
   * Gets all moves of the monster.
   *
   * @return a map of move index to Move
   */
  HashMap<Integer, Move> getMoves();

  /**
   * Gets a specific move by index.
   *
   * @param moveIndex the index of the move
   * @return the move, or null if not found
   */
  Move getMove(int moveIndex);

  /**
   * Checks if a move exists at the given index.
   *
   * @param moveIndex the index of the move
   * @return true if the move exists, false otherwise
   */
  boolean hasMove(int moveIndex);

  /**
   * Checks if a move is on cooldown.
   *
   * @param moveIndex the index of the move
   * @return true if the move is on cooldown, false otherwise
   */
  boolean isMoveOnCooldown(int moveIndex);

  /**
   * Gets the remaining cooldown turns for a move.
   *
   * @param moveIndex the index of the move
   * @return the remaining cooldown turns, or 0 if not on cooldown
   */
  int getRemainingCooldown(int moveIndex);

  /**
   * Checks if a move is locked.
   *
   * @param moveIndex the index of the move
   * @return true if the move is locked, false otherwise
   */
  boolean isMoveLocked(int moveIndex);

  /**
   * Gets the special move progress.
   *
   * @return a string representation of the special move progress
   */
  String getSpecialProgress();

  /**
   * Gets the focus info string.
   *
   * @return the focus info
   */
  String getFocus();

  /**
   * Gets the passive info string.
   *
   * @return the passive info
   */
  String getPassiveInfo();

  /**
   * Gets the image path for the monster.
   *
   * @return the image path
   */
  String getImagePath();
}

