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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

// LOCKED : L.Missbach
public class SimpleTowerDefenseService implements TowerDefenseService {

  private final MapToolbox mapToolbox = new MapToolbox();
  private final EnemyToolbox enemyToolbox = new EnemyToolbox();
  private final TowerToolbox towerToolbox = new TowerToolbox();
  private final WaveGenerator waveGenerator;

  private final long seed;
  private Grid grid;
  private GameState currentGameState;
  private final List<TowerDefenseListener> listeners = new ArrayList<TowerDefenseListener>();
  private final GameLoop gameLoop = new GameLoop();
  private Player player;
  private Configuration configuration;
  private int currentRound;

  private List<Enemy> enemies;

  public SimpleTowerDefenseService() {

    seed = new Random().nextLong();
    configuration = new Configuration();
    grid = mapToolbox.generateMap(configuration.mapSize());
    waveGenerator = new WaveGenerator(mapToolbox.getPath(grid).getFirst(), seed, configuration);

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
  public void startNextRound() throws IllegalStateException {
    if (currentGameState != GameState.PAUSED) {
      throw new IllegalStateException(
          "Operation startNextRound is only allowed for GameState PAUSED");
    }
    currentRound += 1;
    waveGenerator.generateWave(currentRound);

    currentGameState = GameState.RUNNING;
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

  /**
   * Updates the player's health when damaged.
   *
   * @param health the new health value
   * @throws IllegalArgumentException if health is negative
   */
  public void updateHealth(int health) throws IllegalArgumentException {
    if (health < 0) {
      throw new IllegalArgumentException("Health can't be negative");
    }
    player = new Player(health, player.money());
    notifyListeners(TowerDefenseListener::updateHealth);
  }

  /**
   * Updates the player's money when killing enemies or spending on towers.
   *
   * @param money the new money value
   */
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

  private void roundFailed() {

  }
}
