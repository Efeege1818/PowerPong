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
  private boolean attackStance = true;
  private int passiveStacksCrit = 0;
  private int passiveStacksDef = 0;

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
    this.focus = "Manipulates Dodge and Crit chances";
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

  private void increaseCritPassive() {
    if(passiveStacksCrit >= 20) {
      return;
    }
    passiveStacksCrit++;
    critChance += 0.01;
    if (critChance > 1) {
      critChance = 1;
    }
    logger.debug("{} landed a critical hit and it's attacking passive stacks went up.", name);
    logger.debug("{} crit chance went up to {}", name, critChance);

  }

  @Override
  public void handleCriticalHit() {
    if (attackStance) {
      increaseCritPassive();
    }
  }
}
