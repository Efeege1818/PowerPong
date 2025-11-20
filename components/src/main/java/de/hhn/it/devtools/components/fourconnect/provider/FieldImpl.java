package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.Player;

/**
 * Concrete implementation of the Field interface.
 * This class holds the actual internal state of a board cell, including its owner
 * and the decay timer state for toxic fields.
 *
 * It provides package-private setters for internal manipulation by the GameBoardImpl
 * while remaining publicly read-only (implements the Field interface).
 */
public class FieldImpl implements Field {

  private Player owner = null;
  private final boolean isToxicZone;
  private int decayTime = 0;

  /**
   * Constructor: Initializes the field.
   * @param isToxicZone True if this field is a permanent toxic zone.
   */
  public FieldImpl(boolean isToxicZone) {
    this.isToxicZone = isToxicZone;
  }

  @Override
  public Player getOccupyingPlayer() {
    return owner;
  }

  @Override
  public boolean isToxicZone() {
    return isToxicZone;
  }

  @Override
  public int getDecayTime() {
    return decayTime;
  }

  /**
   * Sets the owner of this field. Used internally by GameBoardImpl to place or clear a chip.
   * This method is package-private.
   * @param owner The Player whose chip is placed on this field, or null to clear the field.
   */
  void setOccupyingPlayer(Player owner) {
    this.owner = owner;
  }

  /**
   * Sets the decay timer. Used internally by GameBoardImpl when a chip lands here.
   * This method is package-private.
   * @param time The initial countdown time (e.g., 3).
   */
  void setDecayTime(int time) {
    this.decayTime = time;
  }

  /**
   * Decrements the decay timer by one. Used internally by the ConnectFourServiceImpl
   * in the applyToxicDecay logic. This method is package-private.
   */
  void decrementDecayTime() {
    if (this.decayTime > 0) {
      this.decayTime--;
    }
  }
}