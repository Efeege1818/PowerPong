package de.hhn.it.devtools.components.towerdefense;

import de.hhn.it.devtools.apis.towerdefense.*;

import java.security.Provider;

public class TowerDefenseUsageDemo {
  public static void main(String[] args){
    SimpleTowerDefenseService service = new SimpleTowerDefenseService();
    service.startGame();
    TowerDefenseListener listener = new TowerDefenseListener() {
      boolean tower2Placed = false;
      boolean triedAgain = false;
      GameState lastGameState;
      GameState currentGameState;

      @Override
      public void updateGameState() {

        lastGameState = currentGameState;
        currentGameState = service.getCurrentGameState();
        System.out.println(service.getCurrentGameState());
        if (service.getCurrentGameState() == GameState.PAUSED && lastGameState == GameState.RUNNING) {
          waveCompleted();
        }
        if (service.getCurrentGameState() == GameState.GAME_OVER) {
         gameEnded();
        }
      }

      @Override
      public void updateHealth() {
        System.out.println(service.getPlayer().health() + "HP");
      }

      @Override
      public void updateMoney() {
        System.out.println(service.getPlayer().money() + "$");
      }

      //@Override
      public void gameEnded() {
        System.out.println("GAME OVER");
        if (!triedAgain) {
          service.retry();
          if (tower2Placed) {
            service.placeTower(new Tower(new Coordinates(2, 5), TowerType.RANGED));
            service.placeTower(new Tower(new Coordinates(1, 2), TowerType.RANGED));
            service.placeTower(new Tower(new Coordinates(4, 8), TowerType.RANGED));
            triedAgain = true;
          }
          if (!tower2Placed) {
            service.placeTower(new Tower(new Coordinates(6, 7), TowerType.RANGED));
            tower2Placed = true;
          }
          service.abortGame();
          service.startGame();
          service.startNextRound();
        } else {
          service.abortGame();
        }
      }

      //@Override
      public void waveCompleted() {
        System.out.println("Round " + service.getCurrentRound() + " Completed");
        service.startNextRound();
      }

      @Override
      public void updateTowerMap() {
      }

      @Override
      public void updateMap() {

      }

      @Override
      public void tick() {
        System.out.println(service.getCurrentEnemies().stream().map(enemy ->
            enemy.currentHealth() + "HP ").toList());
      }
    };
    service.addListener(listener);
    service.startNextRound();
    service.placeTower(new Tower(new Coordinates(4,5), TowerType.RANGED));
  }
}
