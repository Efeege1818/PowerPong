package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * Difficulty levels the game can have.
 * Determines how many Enemies and Barriers there are, how much hit points they have and how often Aliens shoot.
 */
public enum Difficulty {
  EASY("Einfach & entspannt spielen"),
  NORMAL("Ausgewogen & herausfordernd"),
  HARD("Extrem & gnadenlos schwer");

  final String infoText;

  Difficulty(String infoText) {
    this.infoText = infoText;
  }

  public String getInfoText() {
    return infoText;
  }
}
