package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the FireMonster.
 */
public class FireMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(FireMonster.class);

  /**
   * Creates a new FireMonster.
   *
   * @param monster Monster
   */
  public FireMonster(Monster monster) {
    this.maxHp = monster.maxHp();
    this.currentHp = monster.maxHp();
    this.attack = monster.attack();
    this.defense = monster.defense();
    this.evasionChance = monster.evasionChance();
    this.critChance = monster.critChance();
    this.element = monster.element();
    this.moves = monster.moves();

    this.name = "Fire Monster";
    this.focus = "FOCUS INFO PLACEHOLDER";
    this.passiveInfo = "PASSIVE INFO PLACEHOLDER";
    this.imagePath = "/Monster Sprites/FeuerMon.png";

    logger.debug("{} created: {}", name, toString());
  }

}
