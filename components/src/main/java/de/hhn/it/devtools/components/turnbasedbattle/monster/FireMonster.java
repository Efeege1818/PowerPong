package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the FireMonster.
 */
public class FireMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(FireMonster.class);

  private int fireMonsterPassiveStacks = 10; // Passive stacks for increased attack. Decreases by 1 each turn.

  /**
   * Creates a new FireMonster.
   *
   * @param monster Monster
   */
  public FireMonster(Monster monster) {
    this.maxHp = monster.maxHp();
    this.currentHp = monster.maxHp();
    this.attack = monster.attack() + fireMonsterPassiveStacks * 2;
    this.defense = monster.defense();
    this.evasionChance = monster.evasionChance();
    this.critChance = monster.critChance();
    this.damageReduction = 0.0;
    this.element = monster.element();
    this.moves = monster.moves();

    this.name = "Fire Monster";
    this.focus = "FOCUS INFO PLACEHOLDER";
    this.passiveInfo = "PASSIVE INFO PLACEHOLDER";
    this.imagePath = "/Monster Sprites/FeuerMon.png";
    this.imagePathBack = "/Monster Sprites/FeuerMon Back.png";

    logger.debug("{} created: {}", name, toString());
  }

  public void tickFireMonsterEffects() {
    if (fireMonsterPassiveStacks > 0) {
      fireMonsterPassiveStacks--;
      logger.debug("FireMonster passive stacks decreased to {}", fireMonsterPassiveStacks);
      changeStat("attack", -2.0);
    }
  }
}
