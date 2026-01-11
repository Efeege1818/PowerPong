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
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Service that manages all internal logic of the game.
 */
public class SimpleTowerDefenseService implements TowerDefenseService {

  private MapToolbox mapToolbox;
  private EnemyToolbox enemyToolbox;
  private TowerToolbox towerToolbox;
  private WaveGenerator waveGenerator;

  private final long seed;
  private GameState currentGameState;
  private final List<TowerDefenseListener> listeners = new ArrayList<>();
  private GameLoop gameLoop;
  private Player player;
  private Player savedPlayerData;
  private Configuration configuration;
  private int currentRound;

  private Queue<Enemy> enemyQueue;

  /**
   * Constructor.
   */
  public SimpleTowerDefenseService() {

    gameLoop = new GameLoop(this);

    mapToolbox = new MapToolbox();
    enemyToolbox = new EnemyToolbox(this);
    towerToolbox = new TowerToolbox(this);

    seed = new Random().nextLong();
    configuration = new Configuration();

    mapToolbox.generateMap(configuration.mapSize());
    waveGenerator = new WaveGenerator(mapToolbox.getPath().getFirst(), seed, configuration);

    player = new Player(configuration.startingHealth(), configuration.startingMoney());
    savedPlayerData = player;
    towerToolbox.saveData();
    currentRound = 0;

    updateGameState(GameState.READY);

  }

  @Override
  public GameState getCurrentGameState() {
    return currentGameState;
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  @Override
  public void editConfiguration(Configuration configuration) {
    this.configuration = configuration;
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
    updateGameState(GameState.PAUSED);
  }

  @Override
  public void abortGame() throws IllegalStateException {
    configuration = new Configuration();

    mapToolbox = new MapToolbox();
    mapToolbox.generateMap(configuration.mapSize());

    resetGame();

    updateGameState(GameState.READY);
  }

  @Override
  public void resetGame() {
    if (gameLoop.isRunning()) {
      gameLoop.stopGame();
    }
    enemyToolbox = new EnemyToolbox(this);
    towerToolbox = new TowerToolbox(this);

    waveGenerator = new WaveGenerator(mapToolbox.getPath().getFirst(), seed, configuration);
    player = new Player(configuration.startingHealth(), configuration.startingMoney());

    savedPlayerData = player;
    towerToolbox.saveData();
    currentRound = 0;

    updateGameState(GameState.PAUSED);

  }

  @Override
  public void retry() throws IllegalStateException {
    if (currentGameState != GameState.GAME_OVER) {
      throw new IllegalStateException("GameState has to be GAME_OVER in order to retry");
    }
    currentRound--;
    player = savedPlayerData;
    towerToolbox.loadData();
    updateGameState(GameState.READY);
  }

  @Override
  public void startNextRound() throws IllegalStateException {
    if (currentGameState != GameState.PAUSED) {
      throw new IllegalStateException(
          "Operation startNextRound is only allowed for GameState PAUSED");
    }
    currentRound += 1;
    enemyQueue = waveGenerator.generateWave(currentRound);

    updateGameState(GameState.RUNNING);
    gameLoop.startGame();
  }

  @Override
  public Grid getMap() throws IllegalStateException {
    return mapToolbox.getGrid();
  }

  @Override
  public Map<TowerType, Integer> getTowerTypes() {
    Map<TowerType, Integer> towerMap = new HashMap<>();
    for (TowerType type : TowerType.values()) {
      towerMap.put(type, TowerToolbox.getCost(type));
    }
    return towerMap;
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
    if (!mapToolbox.isAllowed(tower.coordinates())) {
      throw new IllegalArgumentException("Tower position can't be oin the path");
    }
    if (!Objects.isNull(towerToolbox.getTowers().get(tower.coordinates()))) {
      throw new IllegalArgumentException(
              "Tower position can't be at the coordinates of another Tower");
    }
    if (player.money() < TowerToolbox.getCost(tower.type())) {
      throw new IllegalArgumentException("Not enough money");
    }
    updateMoney(-TowerToolbox.getCost(tower.type()));
    towerToolbox.addTower(tower);
    notifyListeners(TowerDefenseListener::updateMap);
  }

  /**
   * Updates the player's health when damaged.
   *
   * @param health the change of the heath value
   * @throws IllegalArgumentException if health is positive
   */
  public void updateHealth(int health) throws IllegalArgumentException {
    if (health > 0) {
      throw new IllegalArgumentException("Health modifier can't be positive");
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

    int moneyMade = towerToolbox.moneyMade() + enemyToolbox.moneyPerEnemy();
    if (moneyMade != 0) {
      updateMoney(moneyMade);
    }
    enemyToolbox.progress();
    if (enemyToolbox.damagePlayer() != 0) {
      updateHealth(-enemyToolbox.damagePlayer());
    }
    if (player.health() <= 0) {
      roundFailed();
    }
    if (enemyQueue.isEmpty() && enemyToolbox.getEnemies().isEmpty()) {
      roundCompleted();
    }
    notifyListeners(TowerDefenseListener::tick);
  }

  private void updateGameState(GameState newGameState) {
    currentGameState = newGameState;
    notifyListeners(TowerDefenseListener::updateGameState);
  }

  private void notifyListeners(Consumer<TowerDefenseListener> consumer) {
    for (TowerDefenseListener listener : listeners) {
      consumer.accept(listener);
    }
  }

  private void roundFailed() {
    updateGameState(GameState.GAME_OVER);
    gameLoop.stopGame();
    notifyListeners(TowerDefenseListener::gameEnded);
  }

  private void roundCompleted() {
    updateGameState(GameState.PAUSED);
    gameLoop.stopGame();
    savedPlayerData = player;
    towerToolbox.saveData();
    notifyListeners(TowerDefenseListener::gameCompleted);
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
