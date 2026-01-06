package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the WaterMonster.
 */
public class WaterMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(WaterMonster.class);

  private boolean defenseStance = false;
  private boolean attackStance = false;

  /**
   * Creates a new WaterMonster.
   *
   * @param monster Monster
   */
  public WaterMonster(Monster monster) {
    this.maxHp = monster.maxHp();
    this.currentHp = monster.maxHp();
    this.attack = monster.attack();
    this.defense = monster.defense();
    this.evasionChance = monster.evasionChance();
    this.critChance = monster.critChance();
    this.damageReduction = 0.0;
    this.element = monster.element();
    this.moves = monster.moves();

    this.name = "Water Monster";
    this.focus = "Manipulates Dodge and Krit chances";
    this.passiveInfo = "Dodges are more likely";
    this.imagePath = "/Monster Sprites/WasserMon.png";
    this.imagePathBack = "/Monster Sprites/WasserMon Back.png";

    logger.debug("{} created: {}", name, toString());
  }

  /**
   * Switches between defense and attack stance.
   */
  public void switchStance() {
    defenseStance = !defenseStance;
    attackStance = !attackStance;
    logger.debug("{} stance switched to Defense: {}, Attack: {}", name, defenseStance, attackStance);
  }

  /**
   * Checks if the monster is in defense stance.
   *
   * @return true if in defense stance, false otherwise.
   */
  public boolean isDefenseStance() {
    return defenseStance;
  }

  /**
   * Checks if the monster is in attack stance.
   *
   * @return true if in attack stance, false otherwise.
   */
  public boolean isAttackStance() {
    return attackStance;
  }

}
