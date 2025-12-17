package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.apis.turnbasedbattle.move.AttackMove;
import de.hhn.it.devtools.apis.turnbasedbattle.move.BuffMove;
import de.hhn.it.devtools.apis.turnbasedbattle.move.DebuffMove;
import de.hhn.it.devtools.apis.turnbasedbattle.move.DotMove;
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
    AttackMove moveNormalAttack = new AttackMove("Normal attack", Element.NORMAL, 20, 0,
        false, "Normal attack");
    AttackMove moveFireAttack = new AttackMove("Fire attack", Element.FIRE, 25, 1,
        false, "Fire attack");
    AttackMove moveGrassAttack = new AttackMove("Grass attack", Element.GRASS, 20, 1,
        false, "Grass attack");
    AttackMove moveWaterAttack = new AttackMove("Water attack", Element.WATER, 20, 1,
        false, "Water attack");

    // Buff moves
    BuffMove moveAttackBuff = new BuffMove("Attack buff", Element.NORMAL, "attack", 30, 3, 2,
        false, "Increase attack of your monster");
    BuffMove moveEvasionBuff = new BuffMove("Evasion buff", Element.NORMAL, "evasionChance", 0.1, 3, 2,
        false, "Increase evasion chance of your monster");
    BuffMove moveDefenseBuff = new BuffMove("Defense buff", Element.NORMAL, "defense", 10, 3, 2,
        false, "Increase defense of your monster");
    BuffMove moveCriticalBuff = new BuffMove("Critical buff", Element.NORMAL, "critChance", 0.1, 3, 2,
        false, "Increase critical hit chance of your monster");

    // Debuff moves
    DebuffMove moveAttackDebuff = new DebuffMove("Attack debuff", Element.NORMAL, "attack", 20, 3, 1,
        true, "Decrease attack of your enemy's monster");
    DebuffMove moveEvasionDebuff = new DebuffMove("Evasion debuff", Element.NORMAL, "evasionChance", 0.1, 3, 1,
        true, "Decrease evasion chance of your enemy's monster");
    DebuffMove moveDefenseDebuff = new DebuffMove("Defense debuff", Element.NORMAL, "defense", 10, 3, 1,
        true, "Decrease defense of your enemy's monster");
    DebuffMove moveCriticalDebuff = new DebuffMove("Critical debuff", Element.NORMAL, "critChance", 0.1, 3, 1,
        true, "Decrease critical hit chance of your enemy's monster");

    // Special moves
    AttackMove moveStrongFireAttack = new AttackMove("Strong fire attack", Element.FIRE,
        40, 10, true, "Strong fire attack");
    AttackMove moveStrongGrassAttack = new AttackMove("Strong grass attack", Element.GRASS,
        30, 10, true, "Strong grass attack");
    AttackMove moveStrongWaterAttack = new AttackMove("Strong water attack", Element.WATER,
        30, 10, true, "Strong water attack");

    // Dot moves
    DotMove moveDotFire = new DotMove("Fire dot", Element.FIRE,
        5, 2, 10, true, "Fire dot");

    // Overtuned moves
    Move moveOvertunedFire = new AttackMove(
        "Overtuned fire attack", Element.FIRE, 10, 1, true, "Overtuned fire attack");
    Move moveOvertunedGrass = new AttackMove(
        "Overtuned grass attack", Element.GRASS, 10, 1, true, "Overtuned grass attack");
    Move moveOvertunedWater = new AttackMove(
        "Overtuned water attack", Element.WATER, 10, 1, true, "Overtuned water attack");
    DotMove moveOverTunedDot = new DotMove(
        "Overtuned dot", Element.NORMAL, 20, 5, 1, true, "Overtuned dot");
    BuffMove moveOverTunedBuff = new BuffMove(
        "Overtuned buff", Element.NORMAL, "defense", 20, 5, 1, true, "Overtuned buff");
    DebuffMove moveOverTunedDebuff = new DebuffMove(
        "Overtuned debuff", Element.NORMAL, "defense", 20, 5, 1, true, "Overtuned debuff");

    Move[] fireMonsterMoves = {moveDotFire, moveFireAttack, moveCriticalBuff,
        moveDefenseDebuff, moveStrongFireAttack};
    Move[] grassMonsterMoves = {moveNormalAttack, moveGrassAttack, moveAttackBuff,
        moveEvasionDebuff, moveStrongGrassAttack};
    Move[] waterMonsterMoves = {moveNormalAttack, moveWaterAttack, moveEvasionBuff,
        moveAttackDebuff, moveStrongWaterAttack};
    Move[] overTunedMoves = {moveOvertunedFire, moveOvertunedGrass, moveOvertunedWater,
            moveOverTunedBuff, moveOverTunedDebuff, moveOverTunedDot};

    HashMap<Element, Move[]> movesMap = new HashMap<>();
    movesMap.put(Element.FIRE, fireMonsterMoves);
    movesMap.put(Element.GRASS, grassMonsterMoves);
    movesMap.put(Element.WATER, waterMonsterMoves);
    movesMap.put(Element.NORMAL, overTunedMoves);

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
      new Monster(150, 6, 6, 0.3, 0.3, Element.WATER, getWaterMonsterMoves()),
      new Monster(150, 10, 10, 0.1, 0.1, Element.WATER, getOverTunedMonsterMoves())
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

  /**
   * Returns all moves for OverTunedMonster.
   *
   * @return Map with moves.
   */
  public HashMap<Integer, Move> getOverTunedMonsterMoves() {
    HashMap<Integer, Move> movesMap = new HashMap<>();
    movesMap.put(1, createMovesMap().get(Element.NORMAL)[0]);
    movesMap.put(2, createMovesMap().get(Element.NORMAL)[1]);
    movesMap.put(3, createMovesMap().get(Element.NORMAL)[2]);
    movesMap.put(4, createMovesMap().get(Element.NORMAL)[3]);
    movesMap.put(5, createMovesMap().get(Element.NORMAL)[4]);
    movesMap.put(6, createMovesMap().get(Element.NORMAL)[5]);
    return movesMap;
  }

  public Move[] getMonsterMoves(Element element) {
    return moves.get(element);
  }

  public Map<Element, Move[]> getMovesMap() {
    return moves;
  }
}
