package de.hhn.it.devtools.apis.turnbasedbattle.move;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;

/**
 * Record for a buff move.
 *
 * @param type move type.
 * @param name move name.
 * @param element element of the move.
 * @param stat stat to be buffed.
 * @param amount amount of the buff.
 * @param duration duration of the buff.
 * @param cooldown cooldown of the move.
 * @param isSpecial whether the move is special.
 * @param description description of the move.
 */
public record BuffMove(MoveType type, String name, Element element, String stat, double amount, int duration, int cooldown, boolean isSpecial, String description, int executionCount, Move followUpMove) implements Move {

  @Override
  public double amount() {
    return amount;
  }

  @Override
  public String stat() {
    return stat;
  }

  @Override
  public boolean isTrueDamage() { return false;}

  @Override
  public Move attackMove() { return null;}

  /**
   * Custom constructor without type parameter - type is always BUFF.
   */
  public BuffMove(String name, Element element, String stat, double amount, int duration, int cooldown, boolean isSpecial, String description, int executionCount, Move followUpMove) {
    this(MoveType.BUFF, name, element, stat, amount, duration, cooldown, isSpecial, description, executionCount, followUpMove);
  }

  public BuffMove(String name, Element element, String stat, double amount, int duration, int cooldown, boolean isSpecial, String description, int executionCount) {
    this(MoveType.BUFF, name, element, stat, amount, duration, cooldown, isSpecial, description, executionCount, null);
  }

  /**
   * Compact constructor for validation.
   */
  public BuffMove {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name must not be null or empty");
    }
    if (element == null) {
      throw new IllegalArgumentException("element must not be null");
    }
    if (stat == null || stat.isEmpty()) {
      throw new IllegalArgumentException("stat must not be null or empty");
    }
    if (amount < 0) {
      throw new IllegalArgumentException("amount must be non-negative");
    }
    if (duration < 0 || cooldown < 0) {
      throw new IllegalArgumentException("duration and cooldown must be non-negative");
    }
    if (description == null || description.isEmpty()) {
      throw new IllegalArgumentException("description must not be null or empty");
    }
  }
}
