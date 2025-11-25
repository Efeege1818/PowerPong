package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.Player;

/**
 * Concrete implementation of the {@link Field} interface for a single cell on the Four-Connect board.
 * <p>
 * This class manages the internal state of a board cell, including the {@link Player}
 * whose chip occupies the field and the decay timer state, particularly relevant
 * for toxic zones.
 * </p>
 * <p>
 * It provides public read-only access via the {@link Field} interface methods
 * and package-private setters for internal manipulation by related provider classes
 * (like {@link GameBoardImpl}).
 * </p>
 */
public class FieldImpl implements Field {

  private Player owner = null;
  private boolean isToxicZone;
  private int decayTime = 0;
  private int toxicTimer = 3;

  /**
   * Constructs a new field instance.
   *
   * @param isToxicZone {@code true} if this field is a permanent toxic zone that triggers a
   * decay timer upon chip placement, {@code false} otherwise.
   */
  public FieldImpl(boolean isToxicZone) {
    this.isToxicZone = isToxicZone;
  }

  /**
   * Returns the player whose chip currently occupies this field.
   *
   * @return The {@link Player} occupying the field, or {@code null} if the field is empty.
   */
  @Override
  public Player getOccupyingPlayer() {
    return owner;
  }

  /**
   * Checks if this field is permanently marked as a toxic zone.
   *
   * @return {@code true} if the field is a toxic zone, {@code false} otherwise.
   */
  @Override
  public boolean isToxicZone() {
    return isToxicZone;
  }

  /**
   * Returns the current value of the decay timer.
   * <p>
   * This value is only relevant if {@link #isToxicZone()} is {@code true} and a chip
   * has been recently placed here. It counts down to zero, at which point the chip is removed.
   * </p>
   *
   * @return The remaining decay time (typically starting at 3).
   */
  @Override
  public int getDecayTime() {
    return decayTime;
  }

  /**
   * Sets the owner of this field.
   * <p>
   * This method is package-private and used internally by {@link GameBoardImpl}
   * to place or clear a chip.
   * </p>
   *
   * @param owner The {@link Player} whose chip is placed on this field, or {@code null} to clear the field.
   */
  void setOccupyingPlayer(Player owner) {
    this.owner = owner;
  }

  /**
   * Sets the initial value of the decay timer.
   * <p>
   * This method is package-private and typically used by {@link GameBoardImpl}
   * when a chip lands on a toxic zone.
   * </p>
   *
   * @param time The initial countdown time (e.g., 3).
   */
  public void setDecayTime(int time) {
    this.decayTime = time;
  }

  public boolean isOccupied() {
    return this.getOccupyingPlayer() != null;
  }

  public boolean isToxicTimerExpired() {
    return this.toxicTimer <= 0;
  }


  public void resetToxicTimer() {
    this.toxicTimer = 3;
  }



  /**
   * Decrements the decay timer by one, provided the time is greater than zero.
   * <p>
   * This method is package-private and used by the game service layer (e.g.,
   * {@code ConnectFourServiceImpl}) in its decay logic.
   * </p>
   */
  void decrementDecayTime() {
    if (this.decayTime > 0) {
      this.decayTime--;
    }
  }

  public void setToxicZone(boolean isToxic) {
    this.isToxicZone = isToxic;
  }
}