package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the FireMonster.
 */
public class FireMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(FireMonster.class);

  // Passive stacks for increased attack. Decreases by 1 each turn.
  private int fireMonsterPassiveStacks = 10;

  private final int atkPassiveAmount = 2;
  private final int attacksHitThreshold = 7;

  /**
   * Creates a new FireMonster.
   *
   * @param monster Monster
   */
  public FireMonster(Monster monster) {
    this.maxHp = monster.maxHp();
    this.currentHp = monster.maxHp();
    this.attack = monster.attack() + fireMonsterPassiveStacks * atkPassiveAmount;
    this.defense = monster.defense();
    this.evasionChance = monster.evasionChance();
    this.critChance = monster.critChance();
    this.damageReduction = 0.0;
    this.element = monster.element();
    this.moves = monster.moves();

    this.name = "Fire Monster";
    this.focus = "Buffs the power of its hard-hitting moves";
    this.passiveInfo = "Smoldering embers\n" + "Starts with high attack buff that lowers each turn";
    this.imagePath = "/Monster Sprites/FeuerMon.png";
    this.imagePathBack = "/Monster Sprites/FeuerMon Back.png";

    lockMove(5);

    logger.debug("{} created: {}", name, toString());
  }

  @Override
  protected void tickMonsterEffects() {
    if (fireMonsterPassiveStacks > 0) {
      fireMonsterPassiveStacks--;
      logger.debug("FireMonster passive stacks decreased to {}", fireMonsterPassiveStacks);
      changeStat("attack", -atkPassiveAmount);
    }
    if (!isMoveLocked(5)) {
      return;
    } else if (attacksHit >= attacksHitThreshold) {
      unlockMove(5);
      attacksHit = 0;
    }
  }


  @Override
  public String getSpecialProgress() {
    return (attacksHit + "/" + attacksHitThreshold);
  }
}
