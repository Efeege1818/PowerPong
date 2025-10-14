package de.hhn.it.devtools.apis.towerDefenseApis;

public interface TowerDefenseListener {

  void updateHealth();
  void updateMoney();
  void gameEnded();
  void updateMap();
  void updateScreen();
}
