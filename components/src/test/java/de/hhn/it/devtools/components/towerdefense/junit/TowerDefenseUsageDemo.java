package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefenseapi.*;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;

public class TowerDefenseUsageDemo {
  public static void main(String[] args){
    SimpleTowerDefenseService service = new SimpleTowerDefenseService();
    service.startGame();
    TowerDefenseListener listener = new TowerDefenseListener() {
      boolean tower2Placed = false;
      boolean triedAgain = false;

      @Override
      public void updateHealth() {
      }

      @Override
      public void updateMoney() {
      }

      @Override
      public void gameEnded() {
        if (!triedAgain) {
          service.retry();
          if (tower2Placed) {
            service.placeTower(new Tower(1, new Coordinates(2, 5), TowerType.RANGED));
            service.placeTower(new Tower(1, new Coordinates(1, 2), TowerType.RANGED));
            service.placeTower(new Tower(1, new Coordinates(4, 8), TowerType.RANGED));
            service.placeTower(new Tower(1, new Coordinates(7, 7), TowerType.RANGED));
            service.placeTower(new Tower(1, new Coordinates(4, 9), TowerType.RANGED));
            triedAgain = true;
          }
          if (!tower2Placed) {
            service.placeTower(new Tower(1, new Coordinates(6, 7), TowerType.RANGED));
            tower2Placed = true;
          }

          service.startGame();
          service.startNextRound();
        }
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
    service.placeTower(new Tower(1,new Coordinates(4,5), TowerType.RANGED));
  }
}
