package de.hhn.it.devtools.apis.towerDefenseApis;

public interface TowerDefenseListener {

  public void updateHealth();
  public void updateMoney();
  public void gameEnded();
  public void updateMap();
  public void updateScreen();
}
