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
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.function.Consumer;

// LOCKED : L.Missbach
public class SimpleTowerDefenseService implements TowerDefenseService {

  private final MapToolbox mapToolbox = new MapToolbox();
  private final EnemyToolbox enemyToolbox = new EnemyToolbox(this);
  private final TowerToolbox towerToolbox = new TowerToolbox(this);
  private final WaveGenerator waveGenerator;

  private final long seed;
  private GameState currentGameState;
  private final List<TowerDefenseListener> listeners = new ArrayList<TowerDefenseListener>();
  private final GameLoop gameLoop = new GameLoop();
  private Player player;
  private Configuration configuration;
  private int currentRound;

  private Queue<Enemy> enemyQueue;

  public SimpleTowerDefenseService() {

    seed = new Random().nextLong();
    configuration = new Configuration();

    mapToolbox.generateMap(configuration.mapSize());
    waveGenerator = new WaveGenerator(mapToolbox.getPath().getFirst(), seed, configuration);

    player = new Player(configuration.startingHealth(), configuration.startingMoney());
    currentRound = 0;

    currentGameState = GameState.READY;

  }

  @Override
  public GameState getCurrentGameState() {
    return currentGameState;
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
    currentGameState = GameState.PAUSED;
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
    enemyQueue = waveGenerator.generateWave(currentRound);

    currentGameState = GameState.RUNNING;
    gameLoop.startGame();
  }

  @Override
  public Grid getMap() throws IllegalStateException {
    return mapToolbox.getGrid();
  }

  @Override
  public Map<Coordinates, Tower> getTowerBoard() throws IllegalStateException {
    return towerToolbox.getTowers();
  }

  @Override
  public List<Enemy> getCurrentEnemies() throws IllegalStateException {
    return enemyToolbox.getEnemies();
  }

  @Override
  public void placeTower(Tower tower) throws IllegalArgumentException {
    if (Objects.isNull(tower)) {
      throw new IllegalArgumentException("Tower can't be null");
    }
    if (mapToolbox.isAllowed(tower.coordinates())) {
      throw new IllegalArgumentException("Tower position can't be oin the path");
    }
    if (!Objects.isNull(towerToolbox.getTowers().get(tower.coordinates()))) {
      throw new IllegalArgumentException("Tower position can't be at the coordinates of another Tower");
    }
    if (player.money() < TowerToolbox.getCost(tower.type())) {
      throw new IllegalArgumentException("Not enough money");
    }
    updateMoney(-TowerToolbox.getCost(tower.type()));
    towerToolbox.addTower(tower);
  }

  /**
   * Updates the player's health when damaged.
   *
   * @param health the change of the heath value
   * @throws IllegalArgumentException if health is positive
   */
  public void updateHealth(int health) throws IllegalArgumentException {
    if (health < 0) {
      throw new IllegalArgumentException("Health can't be negative");
    }
    player = new Player(player.health() + health, player.money());
    notifyListeners(TowerDefenseListener::updateHealth);
  }

  /**
   * Updates the player's money when killing enemies or spending on towers.
   *
   * @param money the change of the health value
   */
  public void updateMoney(int money) throws IllegalArgumentException {
    if (money < -player.money()) {
      throw new IllegalArgumentException("Money can't be negative");
    }
    player = new Player(player.health(), player.money() + money);
    notifyListeners(TowerDefenseListener::updateMoney);
  }

  @Override
  public int getCurrentRound() {
    return currentRound;
  }

  /**
   * Logic that gets called by the GameLoop one per Game-Tick.
   */
  public void tick() {
    if (!enemyQueue.isEmpty()) {
      enemyToolbox.addEnemy(enemyQueue.poll());
    }
    towerToolbox.attack();
    updateMoney(towerToolbox.moneyMade() + enemyToolbox.moneyPerEnemy());
    enemyToolbox.progress();
    updateHealth(-enemyToolbox.damagePlayer());
    if (player.health() <= 0) {
      roundFailed();
    }
    if (enemyQueue.isEmpty() && enemyToolbox.getEnemies().isEmpty()) {
      roundCompleted();
    }
  }

  private void notifyListeners(Consumer<TowerDefenseListener> consumer) {
    for (TowerDefenseListener listener : listeners) {
      consumer.accept(listener);
    }
  }

  private void roundFailed() {
    currentGameState = GameState.GAME_OVER;
    gameLoop.stopGame();
    notifyListeners(TowerDefenseListener::gameEnded);
  }

  private void roundCompleted() {
    currentGameState = GameState.PAUSED;
    gameLoop.stopGame();
  }


  public EnemyToolbox getEnemyToolbox() {
    return enemyToolbox;
  }

  public TowerToolbox getTowerToolbox() {
    return towerToolbox;
  }

  public MapToolbox getMapToolbox() {
    return  mapToolbox;
  }

}
