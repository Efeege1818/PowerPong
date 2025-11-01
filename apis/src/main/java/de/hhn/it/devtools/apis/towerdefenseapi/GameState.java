package de.hhn.it.devtools.apis.towerdefenseapi;

/**
 * This Enum represents the State of the game and
 * is used by the Service to evaluate, which actions are allowed at the current time.
 */
public enum GameState {
  RUNNING,
  PAUSED, // This State is used between waves, while the player can build new Towers
  GAME_OVER
}
