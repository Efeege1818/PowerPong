package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

/**
 * A simple implementation of the WaterMonster.
 */
public class WaterMonster extends SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(WaterMonster.class);

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
    this.element = monster.element();
    this.moves = monster.moves();

    this.name = "Water Monster";
    this.focus = "Manipulates Dodge and Krit chances";
    this.passiveInfo = "Dodges are more likely";
    this.imagePath = "/Monster Sprites/WasserMon.png";
    this.imagePathBack = "/Monster Sprites/WasserMon Back.png";

    logger.debug("{} created: {}", name, toString());
  }

}
