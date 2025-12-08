package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.GameState;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseListener;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService;

import java.util.List;
import java.util.Map;

//LOCKED : L.Missbach
public class SimpleTowerDefenseService implements TowerDefenseService {
  @Override
  public GameState getCurrentGameState() {
    return null;
  }

  @Override
  public boolean addListener(TowerDefenseListener listener) throws IllegalArgumentException {
    return false;
  }

  @Override
  public boolean removeListener(TowerDefenseListener listener) throws IllegalArgumentException {
    return false;
  }

  @Override
  public void startGame() throws IllegalStateException {

  }

  @Override
  public void abortGame() throws IllegalStateException {

  }

  @Override
  public void resetGame() {

  }

  @Override
  public void retry() throws IllegalStateException {

  }

  @Override
  public void roundFailed() {

  }

  @Override
  public Grid getMap() throws IllegalStateException {
    return null;
  }

  @Override
  public Map<Coordinates, Tower> getTowerBoard() throws IllegalStateException {
    return Map.of();
  }

  @Override
  public List<Enemy> getCurrentEnemies() throws IllegalStateException {
    return List.of();
  }

  @Override
  public void placeTower(Tower tower) throws IllegalArgumentException {

  }

  @Override
  public void updateHealth(int health) throws IllegalArgumentException {

  }

  @Override
  public void updateMoney(int money) {

  }

  @Override
  public int getCurrentRound() {
    return 0;
  }
}
