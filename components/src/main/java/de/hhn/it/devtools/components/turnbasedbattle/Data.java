package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.move.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides data game.
 */
public class Data {

  private final Monster[] monsters;
  private final Map<Element, Move[]> moves;

  // FireMonster moves
  private final Move fireNormAtkFollowUp = new BuffMove("Increases attack", Element.NORMAL, "attack", 5, 3, 0, false, "Increases attack", 1);
  private final Move fireNormAtk = new AttackMove("Heating Flame", Element.NORMAL, 10, 2, false, "PLACEHOLDER DESCRIPTION!", 1, fireNormAtkFollowUp);
  private final Move fireFireAtk1 = new AttackMove("Fire spear", Element.FIRE, 25, 3, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move fireFireAtk2 = new AttackMove("Fire bomb", Element.FIRE, 15, 5, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move fireBuff = new BuffMove("Pump up", Element.NORMAL, "attack", 15, 3, 4, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move fireSpecial = new AttackMove("All out attack", Element.FIRE, 30, 1, true, "PLACEHOLDER DESCRIPTION!", 1);

  // WaterMonster moves
  private final Move waterWaterAtkFollowUp = new AttackMove("Water hit", Element.WATER, 5, 0, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move waterNormAtk = new AttackMove("Double Slice", Element.NORMAL, 7, 2, false, "PLACEHOLDER DESCRIPTION!", 2);
  private final Move waterWaterAtk = new AttackMove("Sharpening Strike", Element.WATER, 5, 3, false, "PLACEHOLDER DESCRIPTION!", 3);
  private final Move waterStance = new StanceMove("Water Dance", Element.NORMAL, 1, "PLACEHOLDER DESCRIPTION! Changes Stance");
  //private final Move waterBuff
  private final Move waterSpecial = new AttackMove("Waterfall", Element.NORMAL, 10, 3, true, "PLACEHOLDER DESCRIPTION!", 3, waterWaterAtkFollowUp);

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
        false, "Normal attack", 1);
    AttackMove moveFireAttack = new AttackMove("Fire attack", Element.FIRE, 25, 1,
        false, "Fire attack", 1);
    AttackMove moveGrassAttack = new AttackMove("Grass attack", Element.GRASS, 20, 1,
        false, "Grass attack", 3);
    AttackMove moveWaterAttack = new AttackMove("Water attack", Element.WATER, 20, 1,
        false, "Water attack", 1);

    // Buff moves
    BuffMove moveAttackBuff = new BuffMove(
            "Attack buff", Element.NORMAL, "attack",
            30, 3, 2, false, "Increase attack of your monster", 1);
    BuffMove moveEvasionBuff = new BuffMove(
            "Evasion buff", Element.NORMAL, "evasionChance",
            0.1, 3, 2, false, "Increase evasion chance of your monster", 1);
    BuffMove moveDefenseBuff = new BuffMove(
            "Defense buff", Element.NORMAL, "defense",
            10, 3, 2, false, "Increase defense of your monster", 1);
    BuffMove moveCriticalBuff = new BuffMove(
            "Critical buff", Element.NORMAL, "critChance",
            0.1, 3, 2, false, "Increase critical hit chance of your monster", 1);

    // Debuff moves
    DebuffMove moveAttackDebuff = new DebuffMove(
            "Attack debuff", Element.NORMAL, "attack",
            20, 3, 1, true, "Decrease attack of your enemy's monster", 1);
    DebuffMove moveEvasionDebuff = new DebuffMove(
            "Evasion debuff", Element.NORMAL, "evasionChance",
            0.1, 3, 1, true, "Decrease evasion chance of your enemy's monster", 1);
    DebuffMove moveDefenseDebuff = new DebuffMove(
            "Defense debuff", Element.NORMAL, "defense",
            10, 3, 1, true, "Decrease defense of your enemy's monster", 1);
    DebuffMove moveCriticalDebuff = new DebuffMove(
            "Critical debuff", Element.NORMAL, "critChance",
            0.1, 3, 1, true, "Decrease critical hit chance of your enemy's monster", 1);

    // Special moves
    AttackMove moveStrongFireAttack = new AttackMove("Strong fire attack", Element.FIRE,
        40, 10, true, "Strong fire attack", 1);
    AttackMove moveStrongGrassAttack = new AttackMove("Strong grass attack", Element.GRASS,
        30, 10, true, "Strong grass attack", 1);
    AttackMove moveStrongWaterAttack = new AttackMove("Strong water attack", Element.WATER,
        30, 10, true, "Strong water attack", 1);

    // Dot moves
    DotMove moveDotFire = new DotMove("Fire dot", Element.FIRE,
        5, 2, 10, true, "Fire dot", 1);

    // Overtuned moves
    Move moveOvertunedFire = new AttackMove(
        "Overtuned fire attack", Element.FIRE,
            10, 1, true, "Overtuned fire attack", 1);
    Move moveOvertunedGrass = new AttackMove(
        "Overtuned grass attack", Element.GRASS,
            10, 1, true, "Overtuned grass attack", 1);
    Move moveOvertunedWater = new AttackMove(
        "Overtuned water attack", Element.WATER,
            10, 1, true, "Overtuned water attack", 1);
    DotMove moveOverTunedDot = new DotMove(
        "Overtuned dot", Element.NORMAL,
            20, 5, 1, true, "Overtuned dot", 1);
    BuffMove moveOverTunedBuff = new BuffMove(
        "Overtuned buff", Element.NORMAL, "defense",
            20, 5, 1, true, "Overtuned buff", 1);
    DebuffMove moveOverTunedDebuff = new DebuffMove(
        "Overtuned debuff", Element.NORMAL, "defense",
            20, 5, 1, true, "Overtuned debuff", 1);

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
      new Monster(150, 6, 6, 0.1, 0.1, Element.WATER, getWaterMonsterMoves()),
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
    movesMap.put(1, fireNormAtk);
    movesMap.put(2, fireFireAtk1);
    movesMap.put(3, fireFireAtk2);
    movesMap.put(4, fireBuff);
    movesMap.put(5, fireSpecial);
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
    movesMap.put(1, waterNormAtk);
    movesMap.put(2, waterWaterAtk);
    movesMap.put(3, waterStance);
    movesMap.put(4, waterWaterAtk);
    movesMap.put(5, waterSpecial);
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
