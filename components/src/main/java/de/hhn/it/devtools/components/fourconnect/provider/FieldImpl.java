package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.Player;

/**
 * Concrete implementation of the {@link Field} interface for a single cell on the board.
 *
 * <p>This class stores the occupying player, toxic state and decay timer.
 */
public class FieldImpl implements Field {

  private Player owner;
  private boolean isToxicZone;
  private int decayTime;

  /**
   * Constructs a new field instance.
   *
   * @param isToxicZone true if this field is a permanent toxic zone
   */
  public FieldImpl(boolean isToxicZone) {
    this.isToxicZone = isToxicZone;
    this.decayTime = 0;
  }

  @Override
  public Player getOccupyingPlayer() {
    return owner;
  }

  void setOccupyingPlayer(Player owner) {
    this.owner = owner;
  }

  @Override
  public boolean isToxicZone() {
    return isToxicZone;
  }

  public void setToxicZone(boolean isToxic) {
    this.isToxicZone = isToxic;
  }

  @Override
  public int getDecayTime() {
    return decayTime;
  }

  /**
   * Sets the decay timer value.
   *
   * @param time decay time
   */
  public void setDecayTime(int time) {
    this.decayTime = time;
  }

  public boolean isOccupied() {
    return owner != null;
  }

  void decrementDecayTime() {
    if (decayTime > 0) {
      decayTime--;
    }
  }
}
