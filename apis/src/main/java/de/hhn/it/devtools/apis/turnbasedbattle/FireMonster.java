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
  public void normalAttack(Monster monster) {
    monster.takeDamage(damage(50, Element.NORMAL)); //TODO: replace Placeholder value
  }

  /**
   * Elemental attack that inflicts 50 damage with fire element.
   */
  @Override
  public void elementalAttack(Monster monster) {
    int damage= 50; //TODO: replace Placeholder value

    if(monster.getElement() == Element.FIRE) {
      monster.takeDamage(damage);
    } else if(monster.getElement() == Element.WATER) {
      monster.takeDamage(damage / 2);
    } else if(monster.getElement() == Element.GRASS) {
      monster.takeDamage(damage * 2);
    }
  }

  /**
   * Increases this monster's attack.
   */
  @Override
  public void buff(Monster monster) {
    monster.setAttack(monster.getAttack() + 1); //TODO: replace Placeholder value
  }

  /**
   * Reduces the enemy monster's defense.
   */
  @Override
  public void debuff(Monster monster) {
    monster.takeDebuff(DEFENSE, 1); //TODO: replace Placeholder value
  }
}
