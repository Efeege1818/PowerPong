package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Configuration for a game instance.
 */
public class GameConfiguration {
  private final int toxicFieldCount;
  private final int decayAfterTurns;

  public GameConfiguration(int toxicFieldCount, int decayAfterTurns) {
    this.toxicFieldCount = toxicFieldCount;
    this.decayAfterTurns = decayAfterTurns;
  }

  public int getToxicFieldCount() {
    return toxicFieldCount;
  }

  public int getDecayAfterTurns() {
    return decayAfterTurns;
  }
}

