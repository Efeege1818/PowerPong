package de.hhn.it.devtools.apis.turnbasedbattle;

public class WaterMonster extends Monster {
  public WaterMonster() {
    setMaxHp(500);
    setCurrentHp(getMaxHp());
    setAttack(0);
    setDefense(0);
    setElement(Element.WATER);
  }
}
