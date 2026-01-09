package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.GameState;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;

public class TowerDefenseUsageDemo {
  public static void main(String[] args){
    SimpleTowerDefenseService service = new SimpleTowerDefenseService();
    service.startGame();
    service.placeTower(new Tower(1,new Coordinates(4,5), TowerType.RANGED));
    service.startNextRound();
  }
}
