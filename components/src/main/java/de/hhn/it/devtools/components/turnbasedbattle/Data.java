package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Move;
import de.hhn.it.devtools.apis.turnbasedbattle.MoveType;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides data game.
 */
public class Data {

  private final Monster[] monsters;
  private final Map<Element, Move[]> moves;

  public Data() {
    this.moves = createMovesMap();
    this.monsters = createMonsters();
  }

  /**
   * Creates a map of moves for each monster.
   *
   * @return a map of moves for each monster.
   */
  private Map<Element, Move[]> createMovesMap() {
    // Attack moves
    Move moveNormalAttack = new Move(MoveType.ATTACK, Element.NORMAL, 20, "health", 0, 0,
        false, "Normal attack");
    Move moveFireAttack = new Move(MoveType.ATTACK, Element.FIRE, 25, "health", 0, 1,
        false, "Fire attack");
    Move moveGrassAttack = new Move(MoveType.ATTACK, Element.GRASS, 20, "health", 0, 1,
        false, "Grass attack");
    Move moveWaterAttack = new Move(MoveType.ATTACK, Element.WATER, 20, "health", 0, 1,
        false, "Water attack");

    // Buff moves
    Move moveAttackBuff = new Move(MoveType.BUFF, Element.NORMAL, 30, "attack", 3, 2,
        false, "Increase attack of your monster");
    Move moveEvasionBuff = new Move(MoveType.BUFF, Element.NORMAL, 0.1, "evasionChance", 3, 2,
        false, "Increase evasion chance of your monster");
    Move moveDefenseBuff = new Move(MoveType.BUFF, Element.NORMAL, 10, "defense", 3, 2,
        false, "Increase defense of your monster");
    Move moveCriticalBuff = new Move(MoveType.BUFF, Element.NORMAL, 0.1, "critChance", 3, 2,
        false, "Increase critical hit chance of your monster");

    // Debuff moves
    Move moveAttackDebuff = new Move(MoveType.DEBUFF, Element.NORMAL, 20, "attack", 3, 1,
        true, "Decrease attack of your enemy's monster");
    Move moveEvasionDebuff = new Move(MoveType.DEBUFF, Element.NORMAL, 0.1, "evasionChance", 3, 1,
        true, "Decrease evasion chance of your enemy's monster");
    Move moveDefenseDebuff = new Move(MoveType.DEBUFF, Element.NORMAL, 10, "defense", 3, 1,
        true, "Decrease defense of your enemy's monster");
    Move moveCriticalDebuff = new Move(MoveType.DEBUFF, Element.NORMAL, 0.1, "critChance", 3, 1,
        true, "Decrease critical hit chance of your enemy's monster");

    // Special moves
    Move moveStrongFireAttack = new Move(MoveType.ATTACK, Element.FIRE,
        40, "health", 1, 10, true, "Strong fire attack");
    Move moveStrongGrassAttack = new Move(MoveType.ATTACK, Element.GRASS,
        30, "health", 1, 10, true, "Strong grass attack");
    Move moveStrongWaterAttack = new Move(MoveType.ATTACK, Element.WATER,
        30, "health", 1, 10, true, "Strong water attack");

    Move[] fireMonsterMoves = {moveNormalAttack, moveFireAttack, moveCriticalBuff,
        moveDefenseDebuff, moveStrongFireAttack};
    Move[] grassMonsterMoves = {moveNormalAttack, moveGrassAttack, moveAttackBuff,
        moveEvasionDebuff, moveStrongGrassAttack};
    Move[] waterMonsterMoves = {moveNormalAttack, moveWaterAttack, moveEvasionBuff,
        moveAttackDebuff, moveStrongWaterAttack};

    HashMap<Element, Move[]> movesMap = new HashMap<>();
    movesMap.put(Element.FIRE, fireMonsterMoves);
    movesMap.put(Element.GRASS, grassMonsterMoves);
    movesMap.put(Element.WATER, waterMonsterMoves);

    // Return all moves
    return movesMap;
  }

  /**
   * Creates an array of Monster objects.
   *
   * @return an array of Monster objects.
   */
  private Monster[] createMonsters() {
    return new Monster[] {
      new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, getFireMonsterMoves()),
      new Monster(120, 8, 8, 0.2, 0.2, Element.GRASS, getGrassMonsterMoves()),
      new Monster(150, 6, 6, 0.3, 0.3, Element.WATER, getWaterMonsterMoves())
    };
  }

  public Monster[] getMonsters() {
    return monsters;
  }

  /**
   * Returns all moves for FireMonster.
   *
   * @return Map with moves.
   */
  public HashMap<Integer, Move> getFireMonsterMoves() {
    HashMap<Integer, Move> movesMap = new HashMap<>();
    movesMap.put(1, createMovesMap().get(Element.FIRE)[0]);
    movesMap.put(2, createMovesMap().get(Element.FIRE)[1]);
    movesMap.put(3, createMovesMap().get(Element.FIRE)[2]);
    movesMap.put(4, createMovesMap().get(Element.FIRE)[3]);
    movesMap.put(5, createMovesMap().get(Element.FIRE)[4]);
    return movesMap;
  }

  /**
   * Returns all moves for GrassMonster.
   *
   * @return Map with moves.
   */
  public HashMap<Integer, Move> getGrassMonsterMoves() {
    HashMap<Integer, Move> movesMap = new HashMap<>();
    movesMap.put(1, createMovesMap().get(Element.GRASS)[0]);
    movesMap.put(2, createMovesMap().get(Element.GRASS)[1]);
    movesMap.put(3, createMovesMap().get(Element.GRASS)[2]);
    movesMap.put(4, createMovesMap().get(Element.GRASS)[3]);
    movesMap.put(5, createMovesMap().get(Element.GRASS)[4]);
    return movesMap;
  }

  /**
   * Returns all moves for GrassMonster.
   *
   * @return Map with moves.
   */
  public HashMap<Integer, Move> getWaterMonsterMoves() {
    HashMap<Integer, Move> movesMap = new HashMap<>();
    movesMap.put(1, createMovesMap().get(Element.WATER)[0]);
    movesMap.put(2, createMovesMap().get(Element.WATER)[1]);
    movesMap.put(3, createMovesMap().get(Element.WATER)[2]);
    movesMap.put(4, createMovesMap().get(Element.WATER)[3]);
    movesMap.put(5, createMovesMap().get(Element.WATER)[4]);
    return movesMap;
  }

  public Move[] getMonsterMoves(Element element) {
    return moves.get(element);
  }

  public Map<Element, Move[]> getMovesMap() {
    return moves;
  }
}
