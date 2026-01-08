package de.hhn.it.devtools.apis.turnbasedbattle.move;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;

/**
 * Record for an attack move.
 *
 * @param type move type.
 * @param name move name.
 * @param element element of the move.
 * @param damage amount of damage dealt.
 * @param cooldown cooldown of the move.
 * @param isSpecial whether the move is special.
 * @param description description of the move.
 */
public record AttackMove(MoveType type, String name, Element element, double damage, int cooldown, boolean isSpecial, String description, int executionCount, Move followUpMove) implements Move {

  @Override
  public double amount() {
    return damage;
  }

  @Override
  public String stat() {
    return "health";
  }

  /**
   * Custom constructor without type parameter - type is always ATTACK.
   */
  public AttackMove(String name, Element element, double damage, int cooldown, boolean isSpecial, String description, int executionCount, Move followUpMove) {
    this(MoveType.ATTACK, name, element, damage, cooldown, isSpecial, description, executionCount, followUpMove);
  }

  public AttackMove(String name, Element element, double damage, int cooldown, boolean isSpecial, String description, int executionCount) {
    this(MoveType.ATTACK, name, element, damage, cooldown, isSpecial, description, executionCount, null);
  }

  /**
   * Compact constructor for validation.
   */
  public AttackMove {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name must not be null or empty");
    }
    if (element == null) {
      throw new IllegalArgumentException("element must not be null");
    }
    if (damage < 0) {
      throw new IllegalArgumentException("damage must be non-negative");
    }
    if (cooldown < 0) {
      throw new IllegalArgumentException("cooldown must be non-negative");
    }
    if (description == null || description.isEmpty()) {
      throw new IllegalArgumentException("description must not be null or empty");
    }
  }
}
