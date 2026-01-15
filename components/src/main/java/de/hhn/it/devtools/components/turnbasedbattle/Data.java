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

  // FireMonster moves
  private final Move fireNormAtkFollowUp = new BuffMove("Increases attack", Element.NORMAL, "attack", 5, 3, 0, false, "Increases attack", 1);
  private final Move fireNormAtk = new AttackMove("Heating Flame", Element.NORMAL, 10, false, 2, false, "PLACEHOLDER DESCRIPTION!", 1, fireNormAtkFollowUp);
  private final Move fireFireAtk1 = new AttackMove("Fire spear", Element.FIRE, 25, false, 3, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move fireFireAtk2 = new AttackMove("Fire bomb", Element.FIRE, 15, false, 5, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move fireBuff = new BuffMove("Pump up", Element.NORMAL, "attack", 15, 3, 4, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move fireSpecial = new AttackMove("All out attack", Element.FIRE, 30, false, 1, true, "PLACEHOLDER DESCRIPTION!", 1);

  // WaterMonster moves
  private final Move waterWaterAtkFollowUp = new AttackMove("Water hit", Element.WATER, 5, false, 0, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move waterNormAtk = new AttackMove("Double Slice", Element.NORMAL, 7, false, 2, false, "2 weak hits", 2);
  private final Move waterWaterAtk = new AttackMove("Sharpening Strike", Element.WATER, 5, true, 3, false, "3 weak hits High Crit chance (ignores DEF)", 3);
  private final Move waterStance = new StanceMove("Water Dance", Element.NORMAL, 1, "Changes the stance of the monster (Crit stance or dodge stance)");
  //private final Move waterBuff
  private final Move waterSpecial = new AttackMove("Waterfall", Element.NORMAL, 10, false, 3, true, "PLACEHOLDER DESCRIPTION!", 3, waterWaterAtkFollowUp);

  public Data() {
    this.monsters = createMonsters();
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
    movesMap.put(1, waterStance);
    movesMap.put(2, waterStance);
    movesMap.put(3, waterStance);
    movesMap.put(4, waterStance);
    movesMap.put(5, waterStance);
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
}
