package de.hhn.it.devtools.apis.turnbasedbattle;

public class WaterMonster extends Monster {

  /**
   * Constructs a WaterMonster object and assigns it different stats.
   */
  public WaterMonster() {
    setMaxHp(500);  //TODO: replace Placeholder value
    setCurrentHp(getMaxHp());
    setAttack(0);
    setDefense(0);
    setElement(Element.WATER);
  }

  /**
   * Normal attack that inflicts 50 damage.
   */
  @Override
  public void normalAttack() {
    damage(50, Element.NORMAL); //TODO: replace Placeholder value
  }

  /**
   * Elemental attack that inflicts 50 damage with water element.
   */
  @Override
  public void elementalAttack() {
    damage(50, Element.WATER); //TODO: replace Placeholder value
  }

  /**
   * Increases this monster's defense.
   */
  @Override
  public void buff() {
    setDefense(getDefense() + 1); //TODO: replace Placeholder value
  }

  /**
   * Reduces the enemy monster's attack.
   */
  @Override
  public void debuff() {
    inflictDebuff(ATTACK, 1); //TODO: replace Placeholder value
  }
}
