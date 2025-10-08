package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * The monster object contains all stats of a monster.
 * This class acts as a customizable parent class for the different monsters.
 */
public abstract class Monster {
  /**
   * The total amount of HP the monster can have.
   */
  private int maxHp;

  /**
   * The current amount of HP the monster has.
   */
  private int currentHp;

  /**
   * A multiplier which increases damage dealt by the monster.
   */
  private int attack;

  /**
   * A multiplier which reduces damage taken by the monster.
   */
  private int defense;

  /**
   * A chance for the monster to take no damage when hit by an attack.
   */
  private double evasionChance = 0.1;

  /**
   * A chance for the monster's attack to deal double the normal amount of damage
   */
  private double critChance = 0.05;

  /**
   * The elemental type of the monster.
   */
  private Element element;

  /**
   * Calculates the damage a move will do to the opposing monster
   *
   * @param damage flat amount of damage a move will do
   * @param element multiplier that can increase or decrease damage when the element is effective against the opponent's
   */
  private void damage(int damage, Element element) {

  }

  /**
   * Reduces the HP of this monster when being hit
   *
   * @param damage amount of HP to be subtracted
   */
  private void takeDamage(int damage) {

  }
}
