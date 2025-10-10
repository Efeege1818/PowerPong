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
  public void normalAttack(Monster monster) {
    monster.takeDamage(damage(50, Element.NORMAL)); //TODO: replace Placeholder value
  }

  /**
   * Elemental attack that inflicts 50 damage with grass element.
   */
  @Override
  public void elementalAttack(Monster monster) {
    int damage= 50; //TODO: replace Placeholder value

    if(monster.getElement() == Element.GRASS) {
      monster.takeDamage(damage);
    } else if(monster.getElement() == Element.FIRE) {
      monster.takeDamage(damage / 2);
    } else if(monster.getElement() == Element.WATER) {
      monster.takeDamage(damage * 2);
    }
  }

  /**
   * Increases this monster's crit chance.
   */
  @Override
  public void buff(Monster monster) {
    monster.setCritChance(monster.getCritChance() + 0.1); //TODO: replace Placeholder value
  }

  /**
   * Reduces the enemy monster's evasion chance.
   */
  @Override
  public void debuff(Monster monster) {
    monster.takeDebuff(EVASION, 1); //TODO: replace Placeholder value
  }
}
