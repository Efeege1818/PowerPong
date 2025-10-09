package de.hhn.it.devtools.apis.turnbasedbattle;

public class GrassMonster extends Monster {

  /**
   * Constructs a GrassMonster object and assigns it different stats.
   */
  public GrassMonster() {
    setMaxHp(500);  //TODO: replace Placeholder value
    setCurrentHp(getMaxHp());
    setAttack(0);
    setDefense(0);
    setElement(Element.GRASS);
  }

  /**
   * Normal attack that inflicts 50 damage.
   */
  @Override
  public void normalAttack() {
    damage(50, Element.NORMAL); //TODO: replace Placeholder value
  }

  /**
   * Elemental attack that inflicts 50 damage with grass element.
   */
  @Override
  public void elementalAttack() {
    damage(50, Element.GRASS); //TODO: replace Placeholder value
  }

  /**
   * Increases this monster's crit chance.
   */
  @Override
  public void buff() {
    setCritChance(getCritChance() + 0.1); //TODO: replace Placeholder value
  }

  /**
   * Reduces the enemy monster's evasion chance.
   */
  @Override
  public void debuff() {
    inflictDebuff(EVASION, 1); //TODO: replace Placeholder value
  }
}
