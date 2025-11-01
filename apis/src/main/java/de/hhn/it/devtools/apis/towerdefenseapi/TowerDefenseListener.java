package de.hhn.it.devtools.apis.towerdefenseapi;

public interface TowerDefenseListener {

  // TODO: Javadoc adden

  void updateHealth();

  void updateMoney();

  void gameEnded();

  void updateMap();

  void updateScreen();
}
