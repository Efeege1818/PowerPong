package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * The monster object contains all stats of a monster.
 * This class acts as a customizable parent class for the different monsters.
 */
public abstract class Monster {

  public static final String ATTACK = "attack";
  public static final String DEFENSE = "defense";
  public static final String EVASION = "evasion";
  public static final String CRIT = "crit";

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
   * A chance for the monster's attack to deal double the normal amount of damage.
   */
  private double critChance = 0.05;

  /**
   * The elemental type of the monster.
   */
  private Element element;

  /**
   * Calculates the damage a move will do to the opposing monster.
   *
   * @param damage flat amount of damage a move will do
   * @param element multiplier that can increase or decrease damage when the element is effective against the opponent's
   */
  public void damage(int damage, Element element) {

  }

  /**
   * Reduces the HP of this monster when being hit.
   *
   * @param damage amount of HP to be subtracted
   */
  public void takeDamage(int damage) {

  }

  /**
   * Reduces a stat of this monster when being debuffed by the opponent.
   *
   * @param stat name of stat to be reduced (should use one of the constant Strings in this class)
   * @param value amount by which the stat will be reduced
   */
  public void takeDebuff(String stat, int value) {

  }

  /**
   * A normal, pure damaging attack without elemental properties.
   */
  public void normalAttack() {

  }

  /**
   * An elemental attack, which can do more or less damage depending on the opponent's element.
   */
  public void elementalAttack() {

  }

  /**
   * Increases one of this monster's stats.
   */
  public void buff() {

  }

  /**
   * Decreases one of the opponent's monster's stats.
   *
   * @param stat name of stat to be reduced (should use one of the constant Strings in this class)
   * @param value amount by which the stat will be reduced
   */
  public void debuff(String stat, int value) {

  }

  public int getMaxHp() {
    return maxHp;
  }

  public void setMaxHp(int maxHp) {
    this.maxHp = maxHp;
  }

  public int getCurrentHp() {
    return currentHp;
  }

  public void setCurrentHp(int currentHp) {
    this.currentHp = currentHp;
  }

  public int getAttack() {
    return attack;
  }

  public void setAttack(int attack) {
    this.attack = attack;
  }

  public int getDefense() {
    return defense;
  }

  public void setDefense(int defense) {
    this.defense = defense;
  }

  public double getEvasionChance() {
    return evasionChance;
  }

  public void setEvasionChance(double evasionChance) {
    this.evasionChance = evasionChance;
  }

  public double getCritChance() {
    return critChance;
  }

  public void setCritChance(double critChance) {
    this.critChance = critChance;
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
    this.element = element;
  }
}
