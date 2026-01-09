package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefenseapi.*;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;

import java.security.Provider;

public class TowerDefenseUsageDemo {
  public static void main(String[] args){
    SimpleTowerDefenseService service = new SimpleTowerDefenseService();
    service.startGame();
    TowerDefenseListener listener = new TowerDefenseListener() {
      boolean tower2Placed = false;
      boolean triedAgain = false;

      @Override
      public void updateHealth() {
        System.out.println(service.getPlayer().health() + "HP");
      }

      @Override
      public void updateMoney() {
        System.out.println(service.getPlayer().money() + "$");
      }

      @Override
      public void gameEnded() {
        System.out.println("GAME OVER");
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
        System.out.println("Round " + service.getCurrentRound() + " Completed");
        service.startNextRound();
      }

      @Override
      public void updateMap() {
      }

      @Override
      public void tick() {
        System.out.println(service.getCurrentEnemies().stream().map(enemy -> enemy.currentHealth() + "HP ").toList());
      }
    };
    service.addListener(listener);
    service.startNextRound();
    service.placeTower(new Tower(1,new Coordinates(4,5), TowerType.RANGED));
  }
}
