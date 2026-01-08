package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the GrassMonster.
 */
public class GrassMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(GrassMonster.class);

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
    this.focus = "FOCUS INFO PLACEHOLDER";
    this.passiveInfo = "PASSIVE INFO PLACEHOLDER";
    this.imagePath = "/Monster Sprites/PflanzeMon.png";
    this.imagePathBack = "/Monster Sprites/PflanzeMon Back.png";


    logger.debug("{} created: {}", name, toString());
  }

}
