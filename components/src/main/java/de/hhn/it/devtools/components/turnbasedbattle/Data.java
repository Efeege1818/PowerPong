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
  private final Move waterWaterAtkCounter = new AttackMove("Water burst", Element.WATER, 25, false, 0, false, "PLACEHOLDER DESCRIPTION!", 1);
  private final Move waterNormAtk = new AttackMove("Double Slice", Element.NORMAL, 7, false, 2, false, "2 weak hits", 2);
  private final Move waterWaterAtk = new AttackMove("Sharpening Strike", Element.WATER, 5, true, 3, false, "3 weak hits High Crit chance (ignores DEF)", 3);
  private final Move waterStance = new StanceMove("Water Dance", Element.NORMAL, 1, "Changes the stance of the monster (Crit stance or dodge stance)");
  private final Move waterCounter = new CounterattackMove("River Reversal", Element.WATER, waterWaterAtkCounter, 3, false, "only ATKs if the enemy also ATKs in the same turn");
  private final Move waterSpecial = new AttackMove("Waterfall", Element.NORMAL, 10, false, 3, true,
          "3 normal hits + 3 water hits, +1 water/ normal hit per Crit with this Move\n" +
          "charge condition: Hit Crits/ dodge ATKs (15 combined)", 3, waterWaterAtkFollowUp);

  // GrassMonster moves
  private final Move grassPoison = new DotMove("Poison", Element.NORMAL, 5, 4, 0, false, "Poison", 1);
  private final Move grassNormAtk = new AttackMove("Poison Sting", Element.NORMAL, 14, false, 2, false, "procs poison on hit", 1);
  private final Move grassGrassAtk = new AttackMove("Poison Bomb", Element.GRASS, 21, false, 3, false, "mid DMG, poisons enemy", 1, grassPoison);
  private final Move grassBuff = new BuffMove("Harden Skin", Element.NORMAL, "damageReduction", 0.5, 2, 5, false, "takes 50% less DMG for 2 turns", 1);
  private final Move grassDebuff = new DebuffMove("Poison Absorb", Element.NORMAL, "attack", 1, 3, 3, false, "if this debuff is applied: doubles poison ticks", 1);
  private final Move grassSpecial = new AttackMove("Leaf Cannon", Element.GRASS, 5, false, 0, false, "charge only increases when poison does DMG", 1);

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
    movesMap.put(1, grassNormAtk);
    movesMap.put(2, grassGrassAtk);
    movesMap.put(3, grassBuff);
    movesMap.put(4, grassDebuff);
    movesMap.put(5, grassSpecial);
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
    movesMap.put(4, waterCounter);
    movesMap.put(5, waterSpecial);
    return movesMap;
  }
}
