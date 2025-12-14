package de.hhn.it.devtools.components.turnbasedbattle.monster;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;

public class GrassMonster extends SimpleMonster {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GrassMonster.class);

    public String name = "Grass Monster";

  public GrassMonster(Monster monster) {
      this.maxHp = monster.maxHp();
      this.currentHp = monster.maxHp();
      this.attack = monster.attack();
      this.defense = monster.defense();
      this.evasionChance = monster.evasionChance();
      this.critChance = monster.critChance();
      this.element = monster.element();
      this.moves = monster.moves();

      logger.debug("{} created: {}", name, toString());
  }

}
