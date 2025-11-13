package de.hhn.it.devtools.components.fourconnect.provider;

<<<<<<< HEAD
=======
import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.Player;

/**
 * Concrete implementation of the Field interface.
 * This class holds the actual internal state of a board cell.
 * It is publicly read-only (implements the Field interface) but provides
 * package-private setters for internal manipulation by the GameBoardImpl.
 */
public class FieldImpl implements Field {

  private Player owner = null; // null represents an empty field
  private final boolean isToxicZone;
  private int decayTime = 0; // The countdown timer (e.g., 3, 2, 1)

  /**
   * Constructor: Initializes the field.
   * @param isToxicZone True if this field is a permanent toxic zone.
   */
  public FieldImpl(boolean isToxicZone) {
    this.isToxicZone = isToxicZone;
  }

  // --- PUBLIC READ-ONLY METHODS (from Field Interface) ---

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

  // Anmerkung: Die Methode boolean isToxic() wurde zu isToxicZone() korrigiert
  // und isOccupied() / isEmpty() ist durch getOccupyingPlayer() == null abgedeckt.


  // --- PACKAGE-PRIVATE METHODS (for Internal Service Control) ---
  // Diese Methoden sind in der Fassade (Field.java) nicht sichtbar,
  // aber für die Logik (GameBoardImpl) notwendig.

  /**
   * Sets the owner of this field. Used internally by GameBoardImpl.
   * @param owner The Player whose chip is placed on this field, or null to clear the field.
   */
  void setOccupyingPlayer(Player owner) {
    this.owner = owner;
  }

  /**
   * Sets the decay timer. Used internally by GameBoardImpl when a chip lands here.
   * @param time The initial countdown time (e.g., 3).
   */
  void setDecayTime(int time) {
    this.decayTime = time;
  }

  /**
   * Decrements the decay timer by one. Used internally by the ConnectFourServiceImpl
   * in the applyToxicDecay logic.
   */
  void decrementDecayTime() {
    if (this.decayTime > 0) {
      this.decayTime--;
    }
  }

  @Override
  public Player getOccupyingPlayer(Player player) {
    return null;
  }

  @Override
  public boolean isToxic() {
    return false;
  }
}
>>>>>>> 7810f2cfbea3cb8d72df1b8faa2e5c96662379ae
