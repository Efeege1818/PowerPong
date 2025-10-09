package de.hhn.it.devtools.apis.turnbasedbattle;

public class FireMonster extends Monster {

  /**
   * Constructs a FireMonster object and assigns it different stats.
   */
  public FireMonster() {
    setMaxHp(500);  //TODO: replace Placeholder value
    setCurrentHp(getMaxHp());
    setAttack(0);
    setDefense(0);
    setElement(Element.FIRE);
  }

  @Override
  public void normalAttack() {
    damage(50, Element.NORMAL); //TODO: replace Placeholder value
  }

  @Override
  public void elementalAttack() {
    damage(50, Element.FIRE); //TODO: replace Placeholder value
  }

  @Override
  public void buff() {
    setAttack(getAttack() + 1); //TODO: replace Placeholder value
  }

  @Override
  public void debuff(String stat, int value) {

  }
}
