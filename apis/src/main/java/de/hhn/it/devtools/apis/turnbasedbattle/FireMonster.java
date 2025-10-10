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

  /**
   * Normal attack that inflicts 50 damage.
   */
  @Override
  public int normalAttack() {
    return damage(50, Element.NORMAL); //TODO: replace Placeholder value
  }

  /**
   * Elemental attack that inflicts 50 damage with fire element.
   */
  @Override
  public int elementalAttack() {
    return damage(50, Element.FIRE); //TODO: replace Placeholder value
  }

  /**
   * Increases this monster's attack.
   */
  @Override
  public void buff() {
    setAttack(getAttack() + 1); //TODO: replace Placeholder value
  }

  /**
   * Reduces the enemy monster's defense.
   */
  @Override
  public Debuff debuff() {
    return inflictDebuff(DEFENSE, 1); //TODO: replace Placeholder value
  }
}
