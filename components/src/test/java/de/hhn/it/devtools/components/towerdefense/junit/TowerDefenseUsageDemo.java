package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefenseapi.*;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;

public class TowerDefenseUsageDemo {
  public static void main(String[] args){
    SimpleTowerDefenseService service = new SimpleTowerDefenseService();
    service.startGame();
    service.placeTower(new Tower(1,new Coordinates(4,5), TowerType.RANGED));
    TowerDefenseListener listener = new TowerDefenseListener() {
      @Override
      public void updateHealth() {

      }

      @Override
      public void updateMoney() {

      }

      @Override
      public void gameEnded() {

      }

      @Override
      public void gameCompleted() {
        service.startNextRound();
      }

      @Override
      public void updateMap() {

      }
    };
    service.addListener(listener);
    service.startNextRound();
  }
}
