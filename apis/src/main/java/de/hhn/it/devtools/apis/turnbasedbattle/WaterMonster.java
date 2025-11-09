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
  public void normalAttack(Monster monster) {
    monster.takeDamage(damage(50, Element.NORMAL)); //TODO: replace Placeholder value
  }

  /**
   * Elemental attack that inflicts 50 damage with water element.
   */
  @Override
  public void elementalAttack(Monster monster) {
    int damage= 50; //TODO: replace Placeholder value

    if(monster.getElement() == Element.WATER) {
      monster.takeDamage(damage);
    } else if(monster.getElement() == Element.GRASS) {
      monster.takeDamage(damage / 2);
    } else if(monster.getElement() == Element.FIRE) {
      monster.takeDamage(damage * 2);
    }
  }

  /**
   * Increases this monster's defense.
   */
  @Override
  public void buff(Monster monster) {
    monster.setDefense(monster.getDefense() + 1); //TODO: replace Placeholder value
  }

  /**
   * Reduces the enemy monster's attack.
   */
  @Override
  public void debuff(Monster monster) {
    monster.takeDebuff(ATTACK, 1); //TODO: replace Placeholder value
  }
}
