package de.hhn.it.devtools.apis.turnbasedbattle.move;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;

/**
 * Interface for moves in a turn-based battle.
 */
public interface Move {
  MoveType type();
  String name();
  String description();
  int cooldown();
  boolean isSpecial();
  Element element();

  // Optional methods with default implementations
  default double amount() {
    return 0.0;
  }

  default String stat() {
    return "";
  }

  default int duration() {
    return 0;
  }
}
