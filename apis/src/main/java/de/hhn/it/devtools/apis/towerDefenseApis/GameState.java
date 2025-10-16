package de.hhn.it.devtools.apis.towerDefenseApis;

public enum GameState {
  RUNNING,
  PAUSED, //This State is used between waves, while the player can build new Towers
  GAME_OVER
}
