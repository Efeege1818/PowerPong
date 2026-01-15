package de.hhn.it.devtools.apis.turnbasedbattle.move;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;

/**
 * Record for a dot move.
 *
 * @param type move type.
 * @param name move name.
 * @param element element of the move.
 * @param damagePerTurn amount of damage dealt per turn.
 * @param duration duration of the dot.
 * @param cooldown cooldown of the move.
 * @param isSpecial whether the move is special.
 * @param description description of the move.
 */
public record DotMove(MoveType type, String name, Element element, double damagePerTurn, int duration, int cooldown, boolean isSpecial, String description, int executionCount, Move followUpMove) implements Move {

  @Override
  public double amount() {
    return damagePerTurn;
  }

  @Override
  public boolean isTrueDamage() { return false;}

  /**
   * Custom constructor without type parameter - type is always DOT.
   */
  public DotMove(String name, Element element, double damagePerTurn, int duration, int cooldown, boolean isSpecial, String description, int executionCount, Move followUpMove) {
    this(MoveType.DOT, name, element, damagePerTurn, duration, cooldown, isSpecial, description, executionCount, followUpMove);
  }

  public DotMove(String name, Element element, double damagePerTurn, int duration, int cooldown, boolean isSpecial, String description, int executionCount) {
    this(MoveType.DOT, name, element, damagePerTurn, duration, cooldown, isSpecial, description, executionCount, null);
  }

  /**
   * Compact constructor for validation.
   */
  public DotMove {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name must not be null or empty");
    }
    if (element == null) {
      throw new IllegalArgumentException("element must not be null");
    }
    if (damagePerTurn < 0) {
      throw new IllegalArgumentException("damagePerTurn must be non-negative");
    }
    if (duration < 0 || cooldown < 0) {
      throw new IllegalArgumentException("duration and cooldown must be non-negative");
    }
    if (description == null || description.isEmpty()) {
      throw new IllegalArgumentException("description must not be null or empty");
    }
  }
}
