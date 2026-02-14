package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Difficulty levels the game can have.
 * Determines how many Enemies and Barriers there are, how much hit points they have and how often Aliens shoot.
 */
public enum Difficulty {
  /** Easy difficulty. */
  EASY("Einfach & entspannt spielen"),
  /** Normal difficulty. */
  NORMAL("Ausgewogen & herausfordernd"),
  /** Hard difficulty. */
  HARD("Extrem & gnadenlos schwer");

  final String infoText;

  Difficulty(String infoText) {
    this.infoText = infoText;
  }

  /**
   * Getter for InfoText.
   *
   * @return a short description of the difficulty level.
   */
  public String getInfoText() {
    return infoText;
  }
}
