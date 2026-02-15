package de.hhn.it.devtools.apis.turnbasedbattle.move;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;

/**
 * Record for a stance move.
 *
 * @param type move type.
 * @param name move name.
 * @param element element of the move.
 * @param cooldown cooldown of the move.
 * @param description description of the move.
 */
public record StanceMove(MoveType type, String name, Element element, int cooldown, String description) implements Move {

  @Override
  public boolean isSpecial() {
    return false;
  }

  @Override
  public int executionCount() {
    return 1;
  }

  @Override
  public Move followUpMove() {
    return null;
  }

  @Override
  public boolean isTrueDamage() { return false;}

  @Override
  public Move attackMove() { return null;}

  /**
   * Custom constructor without type parameter - type is always STANCE.
   */
  public StanceMove(String name, Element element, int cooldown, String description) {
    this(MoveType.STANCE, name, element, cooldown, description);
  }

  /**
   * Compact constructor for validation.
   */
  public StanceMove {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name must not be null or empty");
    }
    if (element == null) {
      throw new IllegalArgumentException("element must not be null");
    }
    if (cooldown < 0) {
      throw new IllegalArgumentException("cooldown must be non-negative");
    }
    if (description == null || description.isEmpty()) {
      throw new IllegalArgumentException("description must not be null or empty");
    }
  }
}