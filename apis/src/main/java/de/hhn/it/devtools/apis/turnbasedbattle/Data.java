package de.hhn.it.devtools.apis.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;

import java.util.HashMap;

public interface Data {

  /**
   * Creates an array of Monster objects.
   *
   * @return an array of Monster objects.
   */
  Monster[] createMonsters();

  Monster[] getMonsters();

  /**
   * Returns all moves for FireMonster.
   *
   * @return Map with moves.
   */
  HashMap<Integer, Move> getFireMonsterMoves();

  /**
   * Returns all moves for GrassMonster.
   *
   * @return Map with moves.
   */
  HashMap<Integer, Move> getGrassMonsterMoves();

  /**
   * Returns all moves for WaterMonster.
   *
   * @return Map with moves.
   */
  HashMap<Integer, Move> getWaterMonsterMoves();
}
