package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the GrassMonster.
 */
public class GrassMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(GrassMonster.class);

  private int passiveStacks = 0;
  private int oldPassiveStacks = 0;
  private int passiveDefAmount = 2;

  /**
   * Creates a new GrassMonster.
   *
   * @param monster Monster
   */
  public GrassMonster(Monster monster) {
    this.maxHp = monster.maxHp();
    this.currentHp = monster.maxHp();
    this.attack = monster.attack();
    this.defense = monster.defense();
    this.evasionChance = monster.evasionChance();
    this.critChance = monster.critChance();
    this.damageReduction = 0.0;
    this.element = monster.element();
    this.moves = monster.moves();

    this.name = "Grass Monster";
    this.focus = "Poisons the enemy while buffing its own defense";
    this.passiveInfo = "Emergency Defense\n" + "Increases DEF with lower HP";
    this.imagePath = "/Monster Sprites/PflanzeMon.png";
    this.imagePathBack = "/Monster Sprites/PflanzeMon Back.png";


    logger.debug("{} created: {}", name, toString());
  }

  @Override
  protected void tickMonsterEffects() {
    double currentPercentHp = (double) currentHp / maxHp;
    oldPassiveStacks = passiveStacks;
    if (currentPercentHp > 0.4) {
      passiveStacks = 0;
    } else if (currentPercentHp > 0.3) {
      passiveStacks = 1;
    } else if (currentPercentHp > 0.2) {
      passiveStacks = 2;
    } else if (currentPercentHp > 0.1) {
      passiveStacks = 3;
    } else if (currentPercentHp > 0.0) {
      passiveStacks = 4;
    }

    if (oldPassiveStacks > passiveStacks) {
      changeStat("defense", -(oldPassiveStacks - passiveStacks) * passiveDefAmount);
    } else if (passiveStacks > oldPassiveStacks) {
      changeStat("defense", (passiveStacks - oldPassiveStacks) * passiveDefAmount);
    }
    logger.debug("{} has {} passive stacks!", name, passiveStacks);

  }

  @Override
  public String getSpecialProgress() {
    return (timesHitPoison + " charges");
  }

}
