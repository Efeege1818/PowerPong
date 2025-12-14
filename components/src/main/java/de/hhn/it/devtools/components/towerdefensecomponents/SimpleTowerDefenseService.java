package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Configuration;
import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.GameState;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Player;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseListener;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// LOCKED : L.Missbach
public class SimpleTowerDefenseService implements TowerDefenseService {

  Grid grid;
  GameState currentGameState;
  List<TowerDefenseListener> listeners = new ArrayList<TowerDefenseListener>();
  GameLoop gameLoop = new GameLoop();
  Player player;
  Configuration configuration;
  int currentRound;

  // TODO: Es gibt keine Methode, um eine neue Runde zu starten

  public SimpleTowerDefenseService() {
    configuration = new Configuration();
    player = new Player(configuration.startingHealth(), configuration.startingMoney());
    currentRound = 0;

    currentGameState = GameState.READY;

  }

  @Override
  public GameState getCurrentGameState() {
    return null;
  }

  @Override
  public boolean addListener(TowerDefenseListener listener) throws IllegalArgumentException {
    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(TowerDefenseListener listener) throws IllegalArgumentException {
    return listeners.remove(listener);
  }

  // TODO: Wozu ist diese Methode da?
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

  // TODO: Diese Methode sollte private sein
  @Override
  public void roundFailed() {

  }

  @Override
  public Grid getMap() throws IllegalStateException {
    return grid;
  }

  @Override
  public Map<Coordinates, Tower> getTowerBoard() throws IllegalStateException {
    return Map.of();
  }

  @Override
  public List<Enemy> getCurrentEnemies() throws IllegalStateException {
    return List.of();
  }

  // TODO: Es wird keine Position an die Methode übergeben
  @Override
  public void placeTower(Tower tower) throws IllegalArgumentException {

  }


  // TODO: Diese Methode muss nicht Teil der API sein
  @Override
  public void updateHealth(int health) throws IllegalArgumentException {
    if (health < 0) {
      throw new IllegalArgumentException("Health can't be negative");
    }
    player = new Player(health, player.money());
    notifyListeners(TowerDefenseListener::updateHealth);
  }


  // TODO: Diese Methode muss nicht Teil der API sein
  @Override
  public void updateMoney(int money) {
    if (money < 0) {
      throw new IllegalArgumentException("Money can't be negative");
    }
    player = new Player(player.health(), money);
    notifyListeners(TowerDefenseListener::updateMoney);
  }

  @Override
  public int getCurrentRound() {
    return currentRound;
  }

  private void notifyListeners(Consumer<TowerDefenseListener> consumer) {
    for (TowerDefenseListener listener : listeners) {
      consumer.accept(listener);
    }
  }
}
