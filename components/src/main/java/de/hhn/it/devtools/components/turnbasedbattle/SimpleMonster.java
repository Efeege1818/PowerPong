package de.hhn.it.devtools.components.turnbasedbattle;

import static de.hhn.it.devtools.components.turnbasedbattle.SimpleDamageCalculator.calculateDamage;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Move;
import de.hhn.it.devtools.components.turnbasedbattle.monster.FireMonster;
import de.hhn.it.devtools.components.turnbasedbattle.monster.GrassMonster;
import de.hhn.it.devtools.components.turnbasedbattle.monster.WaterMonster;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of the Monster interface.
 */
public class SimpleMonster {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleMonster.class);

  protected int maxHp;
  protected int currentHp;
  protected int attack;
  protected int defense;
  protected double evasionChance;
  protected double critChance;
  protected Element element;
  protected HashMap<Integer, Move> moves;
  protected String name;

  // ========== Internal Tracking (Buffs, DOTs, Cooldowns) ==========

  /**
   * Tracks active buffs/debuffs on this monster.
   * Key: remaining duration in turns, Value: the Move that caused the buff/debuff.
   */
  private HashMap<Integer, Move> activeBuffs = new HashMap<>();

  /**
   * Tracks active damage-over-time effects on this monster.
   * Key: remaining duration in turns, Value: the Move that caused the DOT.
   */
  private HashMap<Integer, Move> activeDots = new HashMap<>();

  /**
   * Tracks cooldowns for this monster's moves.
   * Key: move index, Value: remaining cooldown turns.
   */
  private Map<Integer, Integer> moveCooldowns = new HashMap<>();


  /**
   * Factory method to create the appropriate monster type based on the element.
   *
   * @param monster the monster data to create from.
   * @return a SimpleMonster instance of the appropriate subclass
   *        (FireMonster, WaterMonster, or GrassMonster).
   */
  public static SimpleMonster create(Monster monster) {
    switch (monster.element()) {
      case FIRE:
        return new FireMonster(monster);
      case WATER:
        return new WaterMonster(monster);
      case GRASS:
        return new GrassMonster(monster);
      default:
        throw new IllegalArgumentException("Invalid element: " + monster.element());
    }
  }

  /**
   * Protected constructor for subclasses.
   */
  protected SimpleMonster() {
    // Subclasses will initialize the fields
  }



  /**
   * Takes damage from a move.
   *
   * @param move the move that caused the damage.
   * @param attackingMonster the monster that executed the move.
   */
  public void takeDamage(Move move, SimpleMonster attackingMonster) {
    int actualDamage = 0;

    // Check if the attack is evaded
    if (Math.random() < evasionChance) {
      logger.debug("{} evaded the attack.", name);
      return;
    }

    boolean isCritical = Math.random() < attackingMonster.getCritChance();
    boolean isEffective = isElementEffective(this, move.element());

    actualDamage = calculateDamage(move, this, attackingMonster, isCritical, isEffective);

    if (actualDamage > currentHp) {
      currentHp = 0;
    } else {
      currentHp -= actualDamage;
    }

    logger.debug("{} took {} damage and has {} hp left.", name, actualDamage, currentHp);
  }

  /**
   * Takes true damage, ignoring evasion, critical hits, elemental effectiveness,
   * attack, and defense.
   *
   * @param amount the amount of damage to take.
   */
  public void takeTrueDamage(int amount) {
    if (amount > currentHp) {
      currentHp = 0;
    } else {
      currentHp -= amount;
    }
    logger.debug("{} took {} true damage and has {} hp left.", name, amount, currentHp);
  }

  /**
   * Checks if the move is effective against the target's element.
   *
   * @param moveElement the element of the move.
   * @return true if the move is effective, false otherwise.
   */
  public boolean isElementEffective(SimpleMonster target, Element moveElement) {
    //Fire
    if (target.getElement() == Element.FIRE) {
      return moveElement == Element.WATER;
    }

    //Water
    if (target.getElement() == Element.WATER) {
      return moveElement == Element.GRASS;
    }

    //Grass
    if (target.getElement() == Element.GRASS) {
      return moveElement == Element.FIRE;
    }

    return false;
  }

  /**
   * Adds health to the monster.
   *
   * @param amount the amount of health to add.
   */
  public void addHealth(int amount) {
    currentHp += amount;
    if (currentHp > maxHp) {
      currentHp = maxHp;
    }
    logger.debug("{} health increased by {} to {}", name, amount, currentHp);
  }

  /**
   * Applies raw damage to this monster that ignores evasion, critical hits and
   * elemental effectiveness. Used for damage-over-time (DOT) effects.
   *
   * @param amount the amount of health to subtract
   */
  public void takeDotDamage(int amount) {
    if (amount <= 0) {
      return;
    }
    currentHp -= amount;
    if (currentHp < 0) {
      currentHp = 0;
    }
    logger.debug("{} took {} DOT damage, current HP: {}", name, amount, currentHp);
  }

  /**
   * Applies a buff to the monster based on the provided move.
   *
   * @param move the move that contains the buff information.
   */
  public void buffMonster(Move move) {
    String stat = move.stat();
    double amount = move.amount();
    switch (stat) {
      case "health":
        addHealth((int) amount);
        break;
      case "attack":
        attack += (int) amount;
        break;
      case "evasionChance":
        evasionChance = Math.max(0.0, Math.min(1.0, evasionChance + amount));
        break;
      case "critChance":
        critChance = Math.max(0.0, Math.min(1.0, critChance + amount));
        break;
      case "defense":
        defense += (int) amount;
        break;
      default:
        logger.warn("{} Invalid stat for buff: {}", name, stat);
    }
    logger.debug("{} buffed: {} {} and has now {}", name, amount, stat, getStat(stat));
  }

  /**
   * Applies a debuff to the Monster based on the provided Move.
   *
   * @param move the Move that causes the debuff.
   */
  public void debuffMonster(Move move) {
    String stat = move.stat();
    double amount = move.amount();
    switch (stat) {
      case "attack":
        attack -= (int) amount;
        break;
      case "defense":
        defense -= (int) amount;
        break;
      case "evasionChance":
        evasionChance = Math.max(0.0, Math.min(1.0, evasionChance - amount));
        break;
      case "critChance":
        critChance = Math.max(0.0, Math.min(1.0, critChance - amount));
        break;
      default:
        logger.warn("{} Invalid stat for debuff: {}", name, stat);
    }
    logger.debug("{} debuffed: {} {} and has now {}", name, amount, stat, getStat(stat));
  }

  /**
   * Removes a buff from the monster.
   *
   * @param move that was applied as a buff.
   */
  public void removeBuff(Move move) {
    String stat = move.stat();
    double amount = move.amount();
    switch (stat) {
      case "attack":
        attack -= (int) amount;
        break;
      case "evasionChance":
        evasionChance = Math.max(0.0, Math.min(1.0, evasionChance - amount));
        break;
      case "critChance":
        critChance = Math.max(0.0, Math.min(1.0, critChance - amount));
        break;
      case "defense":
        defense -= (int) amount;
        break;
      default:
        logger.warn("{} Invalid stat for removing buff: {}", name, stat);
    }
    logger.debug("{} buff removed: {} and has now {}", name, stat, getStat(stat));
  }

  /**
   * Removes a debuff from the monster.
   *
   * @param move that was applied as a debuff.
   */
  public void removeDebuff(Move move) {
    String stat = move.stat();
    double amount = move.amount();
    switch (stat) {
      case "attack":
        attack += (int) amount;
        break;
      case "evasionChance":
        evasionChance = Math.max(0.0, Math.min(1.0, evasionChance + amount));
        break;
      case "critChance":
        critChance = Math.max(0.0, Math.min(1.0, critChance + amount));
        break;
      case "defense":
        defense += (int) amount;
        break;
      default:
        logger.warn("{} Invalid stat for removing debuff: {}", name, stat);
    }
    logger.debug("{} debuff removed: {} and has now {}", name, stat, getStat(stat));
  }

  /**
   * Checks if the monster is still alive.
   *
   * @return true if the monster has HP remaining, false otherwise.
   */
  public boolean isAlive() {
    return currentHp > 0;
  }

  /**
   * Checks if the monster is at full health.
   *
   * @return true if current HP equals max HP, false otherwise.
   */
  public boolean isAtFullHealth() {
    return currentHp == maxHp;
  }

  /**
   * Resets the monster's health to maximum HP.
   */
  public void resetToFullHealth() {
    currentHp = maxHp;
    logger.debug("Monster health reset to max: {}", maxHp);
  }

  @Override
  public String toString() {
    return String.format("SimpleMonster[HP: %d/%d, ATK: %d, DEF: %d, Element: %s,"
            + " Evasion: %.2f, Critical: %.2f]",
      currentHp, maxHp, attack, defense, element, evasionChance, critChance);
  }

  /**
   * Gets a move by its index.
   *
   * @param index the index of the move.
   * @return the move at the specified index.
   */
  public Move getMove(int index) {
    return moves.get(index);
  }

  /**
   * Checks if the monster has a move at the specified index.
   *
   * @param index the index to check.
   * @return true if a move exists at the index, false otherwise.
   */
  public boolean hasMove(int index) {
    return moves.containsKey(index);
  }

  /**
   * Gets the maximum HP of the monster.
   *
   * @return the maximum HP.
   */
  public int getMaxHp() {
    return maxHp;
  }

  /**
   * Gets the current HP of the monster.
   *
   * @return the current HP.
   */
  public int getCurrentHp() {
    return currentHp;
  }

  /**
   * Gets the attack stat of the monster.
   *
   * @return the attack value.
   */
  public int getAttack() {
    return attack;
  }

  /**
   * Gets the defense stat of the monster.
   *
   * @return the defense value.
   */
  public int getDefense() {
    return defense;
  }

  /**
   * Gets the evasion chance of the monster.
   *
   * @return the evasion chance (0.0 to 1.0).
   */
  public double getEvasionChance() {
    return evasionChance;
  }

  /**
   * Gets the critical hit chance of the monster.
   *
   * @return the critical hit chance (0.0 to 1.0).
   */
  public double getCritChance() {
    return critChance;
  }

  /**
   * Returns requested stat.
   *
   * @param stat stat to be returned
   * @return selected stat
   */
  public String getStat(String stat) {
    switch (stat) {
      case "health":
        return String.valueOf(currentHp);
      case "attack":
        return String.valueOf(attack);
      case "defense":
        return String.valueOf(defense);
      case "evasionChance":
        return String.valueOf(evasionChance);
      case "critChance":
        return String.valueOf(critChance);
      default:
        throw new IllegalArgumentException("Invalid stat: " + stat);
    }
  }

  /**
   * Gets the element type of the monster.
   *
   * @return the element.
   */
  public Element getElement() {
    return element;
  }

  /**
   * Gets all moves of the monster.
   *
   * @return a HashMap containing all moves indexed by their position.
   */
  public HashMap<Integer, Move> getMoves() {
    return moves;
  }

  /**
   * Ticks all active effects (buffs, DOTs, cooldowns).
   */
  public void tickAllEffects() {
    tickBuffs();
    applyAndTickDots();
    tickCooldowns();
  }

  // ========== Buff/Debuff Tracking Methods ==========

  /**
   * Adds a buff or debuff to this monster and applies it immediately.
   *
   * @param move the buff/debuff move to add.
   */
  public void addBuffOrDebuff(Move move) {
    activeBuffs.put(move.duration(), move);

    // Apply the buff/debuff immediately
    switch (move.type()) {
      case BUFF:
        buffMonster(move);
        logger.debug("Added buff '{}' to {} for {} turns",
                move.description(), name, move.duration());
        break;
      case DEBUFF:
        debuffMonster(move);
        logger.debug("Added debuff '{}' to {} for {} turns",
                move.description(), name, move.duration());
        break;
      default:
        logger.warn("Attempted to add non-buff/debuff move as buff: {}", move.type());
    }
  }

  /**
   * Ticks all active buffs/debuffs, reducing their duration by 1.
   * Removes buffs/debuffs that have expired.
   */
  public void tickBuffs() {
    if (activeBuffs.isEmpty()) {
      return;
    }

    HashMap<Integer, Move> updatedBuffs = new HashMap<>();

    for (Map.Entry<Integer, Move> entry : activeBuffs.entrySet()) {
      int duration = entry.getKey();
      Move move = entry.getValue();

      int newDuration = duration - 1;
      if (newDuration > 0) {
        updatedBuffs.put(newDuration, move);
        logger.debug("Buff/Debuff '{}' for {} ticked: {} turns remaining",
                move.description(), name, newDuration);
      } else {
        // Buff/Debuff expired - remove its effects
        switch (move.type()) {
          case BUFF:
            removeBuff(move);
            logger.debug("Buff '{}' for {} expired and was removed",
                    name, move.description());
            break;
          case DEBUFF:
            removeDebuff(move);
            logger.debug("Debuff '{}' for {} expired and was removed",
                    name, move.description());
            break;
          default:
            break;
        }
      }
    }

    activeBuffs = updatedBuffs;
  }

  /**
   * Gets all active buffs/debuffs on this monster.
   *
   * @return a copy of the active buffs map.
   */
  public Map<Integer, Move> getActiveBuffs() {
    return new HashMap<>(activeBuffs);
  }

  // ========== DOT Tracking Methods ==========

  /**
   * Adds a damage-over-time effect to this monster.
   *
   * @param move the DOT move to add.
   */
  public void addDot(Move move) {
    activeDots.put(move.duration(), move);
    logger.debug("Added DOT '{}' to {} for {} turns", move.description(), name, move.duration());
  }

  /**
   * Applies all active DOT effects to this monster and ticks their duration.
   * Called when this monster is attacked.
   */
  public void applyAndTickDots() {
    if (activeDots.isEmpty()) {
      return;
    }

    HashMap<Integer, Move> updatedDots = new HashMap<>();

    for (Map.Entry<Integer, Move> entry : activeDots.entrySet()) {
      int duration = entry.getKey();
      Move move = entry.getValue();

      if (duration > 0 && isAlive()) {
        // Apply DOT damage
        int damage = (int) move.amount();
        if (damage > 0) {
          takeDotDamage(damage);
          logger.debug("DOT '{}' dealt {} damage to {} ({} turns left after this)",
              move.description(), damage, name, duration - 1);
        }

        // Reduce duration
        int newDuration = duration - 1;
        if (newDuration > 0) {
          updatedDots.put(newDuration, move);
        } else {
          logger.debug("DOT '{}' for {} expired", move.description(), name);
        }
      }
    }

    activeDots = updatedDots;
  }

  /**
   * Gets all active DOT effects on this monster.
   *
   * @return a copy of the active DOTs map.
   */
  public Map<Integer, Move> getActiveDots() {
    return new HashMap<>(activeDots);
  }

  // ========== Cooldown Tracking Methods ==========

  /**
   * Checks if a move is currently on cooldown.
   *
   * @param moveIndex the index of the move to check.
   * @return true if the move is on cooldown, false otherwise.
   */
  public boolean isMoveOnCooldown(int moveIndex) {
    Integer remaining = moveCooldowns.get(moveIndex);
    return remaining != null && remaining > 0;
  }

  /**
   * Gets the remaining cooldown for a specific move.
   *
   * @param moveIndex the index of the move.
   * @return the remaining cooldown turns, or 0 if not on cooldown.
   */
  public int getRemainingCooldown(int moveIndex) {
    return moveCooldowns.getOrDefault(moveIndex, 0);
  }

  /**
   * Applies cooldown to a move after it has been used.
   *
   * @param moveIndex the index of the move.
   * @param move the move that was used.
   */
  public void applyCooldown(int moveIndex, Move move) {
    int cooldown = move.cooldown();
    if (cooldown > 0) {
      moveCooldowns.put(moveIndex, cooldown);
      logger.debug("{}: Applied {} turns cooldown to move {} ({})",
              name, cooldown, moveIndex, move.description());
    }
  }

  /**
   * Ticks all cooldowns for this monster, reducing them by 1.
   * Removes cooldowns that have reached 0.
   */
  public void tickCooldowns() {
    if (moveCooldowns.isEmpty()) {
      return;
    }

    Map<Integer, Integer> updatedCooldowns = new HashMap<>();

    for (Map.Entry<Integer, Integer> entry : moveCooldowns.entrySet()) {
      int moveIndex = entry.getKey();
      int remaining = entry.getValue();

      if (remaining > 0) {
        int newRemaining = remaining - 1;
        if (newRemaining > 0) {
          updatedCooldowns.put(moveIndex, newRemaining);
        }
        logger.debug("{}: Cooldown ticked for move {}: {} turns left",
                name, moveIndex, newRemaining);
      }
    }

    moveCooldowns = updatedCooldowns;
  }

  /**
   * Gets all active cooldowns for this monster.
   *
   * @return a copy of the cooldowns map.
   */
  public Map<Integer, Integer> getMoveCooldowns() {
    return new HashMap<>(moveCooldowns);
  }
}
