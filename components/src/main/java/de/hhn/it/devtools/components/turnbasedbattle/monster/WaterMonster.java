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
  private int specialConditionStacks = 0;

  private final double critPassiveAmount = 0.01;
  private final double defPassiveAmount = 0.015;
  private final int specialConditionStacksThreshold = 9;

  /**
   * Creates a new WaterMonster.
   *
   * @param monster Monster
   */
  public WaterMonster(Monster monster) {
    logger.info("WaterMonster: creating {} from monster", name);

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
    this.passiveInfo = "Water Flow \n"
            + "Buffs crit chance if this monster hits a crit hit in crit stance \n"
            + "Buffs dodge chance if this monster dodges an ATK in dodge stance";
    this.imagePath = "/Monster Sprites/WasserMon.png";
    this.imagePathBack = "/Monster Sprites/WasserMon Back.png";

    lockMove(5);

    logger.debug("{} created: {}", name, toString());
  }

  /**
   * Switches between defense and attack stance.
   */
  public void switchStance() {
    logger.info("switchStance: {}, currentDefenseStance = {}, currentAttackStance = {}",
            name, defenseStance, attackStance);

    defenseStance = !defenseStance;
    attackStance = !attackStance;
    if (passiveStacksDef > 0) {
      logger.debug("{} lost all {} stacks for it's defense stance!", name, passiveStacksDef);
      changeStat("evasionChance", -(passiveStacksDef * defPassiveAmount));
      passiveStacksDef = 0;
    } else if (passiveStacksCrit > 0) {
      logger.debug("{} lost all {} stacks for it's attacking stance!", name, passiveStacksCrit);
      changeStat("critChance", -(passiveStacksCrit * critPassiveAmount));
      passiveStacksCrit = 0;
    }
    logger.debug("{} stance switched to Defense: {}, Attack: {}",
        name, defenseStance, attackStance);
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

  /**
   * Increments the passive stacks for the attacking stance by 1.
   */
  private void increaseCritPassive() {
    logger.info("increaseCritPassive: {}, currentCritStacks = {}",
            name, passiveStacksCrit);

    if (passiveStacksCrit >= 20) {
      return;
    }
    passiveStacksCrit++;
    changeStat("critChance", critPassiveAmount);
    logger.debug("{} landed a critical hit and it's attacking passive stacks went up.", name);
  }

  /**
   * Increments the passive stacks for the defense stance by 1.
   */
  private void increaseDefPassive() {
    logger.info("increaseDefPassive: {}, currentDefStacks = {}",
            name, passiveStacksDef);

    if (passiveStacksDef >= 20) {
      return;
    }
    passiveStacksDef++;
    changeStat("evasionChance", defPassiveAmount);
    logger.debug("{} dodged an attack and it's defense passive stacks went up.", name);
  }

  @Override
  public void handleCriticalHit() {
    logger.info("handleCriticalHit: {}, attackStance = {}",
            name, attackStance);

    if (attackStance) {
      increaseCritPassive();
      increaseSpecialConditionStacks();
    }
  }

  @Override
  public void handleDodge() {
    logger.info("handleDodge: {}, defenseStance = {}",
            name, defenseStance);

    if (defenseStance) {
      increaseDefPassive();
      increaseSpecialConditionStacks();
    }
  }

  @Override
  public String getSpecialProgress() {
    logger.debug("getSpecialProgress: {}, progress = {}/{}",
            name,specialConditionStacks, specialConditionStacksThreshold);

    return (specialConditionStacks + "/" + specialConditionStacksThreshold);
  }

  private void increaseSpecialConditionStacks() {
    logger.info("increaseSpecialConditionStacks: {}, currentStacks = {}",
            name, specialConditionStacks);

    if (!isMoveLocked(5)) {
      return;
    } else if (specialConditionStacks >= specialConditionStacksThreshold) {
      return;
    } else {
      specialConditionStacks++;
      logger.debug("Increased special move condition stacks by one to {}/{} for {}",
          specialConditionStacks, specialConditionStacksThreshold, name);
    }

    if (specialConditionStacks == specialConditionStacksThreshold) {
      unlockMove(5);
      specialConditionStacks = 0;
    }
  }
}
