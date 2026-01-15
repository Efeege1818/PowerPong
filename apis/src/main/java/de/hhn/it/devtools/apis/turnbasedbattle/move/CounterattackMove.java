package de.hhn.it.devtools.apis.turnbasedbattle.move;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;

/**
 * Record for counterattack move.
 *
 * @param type move type.
 * @param name move name.
 * @param element element of the move.
 * @param attackMove attacking move
 * @param cooldown cooldown of the move.
 * @param isSpecial whether the move is special.
 * @param description description of the move.
 */
public record CounterattackMove(MoveType type, String name, Element element, Move attackMove, int cooldown, boolean isSpecial, String description) implements Move {

  /**
   * Custom constructor without type parameter - type is always COUNTERATTACK.
   */
  public CounterattackMove(String name, Element element, Move attackMove, int cooldown, boolean isSpecial, String description) {
    this(MoveType.COUNTERATTACK, name, element, attackMove, cooldown, isSpecial, description);
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
  public boolean isTrueDamage() {
    return false;
  }
}
