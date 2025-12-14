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
    this.element = monster.element();
    this.moves = monster.moves();
    this.name = "Grass Monster";
    logger.debug("{} created: {}", name, toString());
  }

}
