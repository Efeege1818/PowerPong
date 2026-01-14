package de.hhn.it.devtools.apis.turnbasedbattle;
/**
 * All battle logic (damage, moves?, turns) is delegated to a BattleManager implementation.
 */

public interface BattleManager {
  /**
   * Initializes players and their monsters and sets the starting player.
   *
   * @param p1 Player 1
   * @param p2 Player 2
   * @param m1 Player 1's monster
   * @param m2 Player 2's monster
   */
  void initializeBattle(Player p1, Player p2, Monster m1, Monster m2);

  boolean isBattleOver();

  int getTurnCount();

  Player getCurrentPlayer();

  Player getWinner();

  /**
   * Executes a selected move and checks if there is a winner yet.
   *
   * @param moveNumber index number of move from monster
   * @return 0 if there is no winner yet, 1 if player 1 won and 2 if player 2 won
   */
  int executeTurn(int moveNumber);

  /**
   * Flips who current and opponent players are to advance the turn to the next player.
   */
  void nextTurn();

  /**
   * Checks whether one player's monster is effective against the other.
   *
   * @return the player with the weaker monster. If both monsters have the same element, starting player is randomized
   */
  Player determineStartingPlayer();
}
