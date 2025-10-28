package de.hhn.it.devtools.apis.fourconnect;

/**
 * Configuration settings for a specific instance of a game, potentially Four Connect
 * with additional rules like toxic fields.
 */
public class GameConfiguration {
  private final int toxicFieldCount;
  private final int decayAfterTurns;

  /**
   * Creates a new game configuration with specified parameters.
   *
   * @param toxicFieldCount The number of special "toxic" fields to be placed on the board.
   * @param decayAfterTurns The number of turns after which a toxic field's effect might decay or change.
   */
  public GameConfiguration(int toxicFieldCount, int decayAfterTurns) {
    this.toxicFieldCount = toxicFieldCount;
    this.decayAfterTurns = decayAfterTurns;
  }

  /**
   * Returns the number of toxic fields configured for the game.
   *
   * @return The count of toxic fields.
   */
  public int getToxicFieldCount() {
    return toxicFieldCount;
  }

  /**
   * Returns the number of turns after which a toxic field is designed to decay or change its effect.
   *
   * @return The decay duration in turns.
   */
  public int getDecayAfterTurns() {
    return decayAfterTurns;
  }
}