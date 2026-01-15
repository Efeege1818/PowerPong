package de.hhn.it.devtools.components.towerdefense;

import de.hhn.it.devtools.apis.towerdefense.Configuration;
import de.hhn.it.devtools.apis.towerdefense.Coordinates;
import de.hhn.it.devtools.apis.towerdefense.Enemy;
import de.hhn.it.devtools.apis.towerdefense.GameState;
import de.hhn.it.devtools.apis.towerdefense.Grid;
import de.hhn.it.devtools.apis.towerdefense.Player;
import de.hhn.it.devtools.apis.towerdefense.Tower;
import de.hhn.it.devtools.apis.towerdefense.TowerDefenseListener;
import de.hhn.it.devtools.apis.towerdefense.TowerDefenseService;
import de.hhn.it.devtools.apis.towerdefense.TowerType;

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

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleTowerDefenseService.class);

  private MapToolbox mapToolbox;
  private EnemyToolbox enemyToolbox;
  private TowerToolbox towerToolbox;
  private WaveGenerator waveGenerator;

  private long seed;
  private GameState currentGameState;
  private final List<TowerDefenseListener> listeners = new ArrayList<>();
  private final GameLoop gameLoop;
  private Player player;
  private Player savedPlayerData;
  private Configuration configuration;
  private int currentRound;

  private Queue<Enemy> enemyQueue;

  /**
   * Constructor.
   */
  public SimpleTowerDefenseService() {

    seed = new Random().nextLong();

    gameLoop = new GameLoop(this);
    mapToolbox = new MapToolbox(seed);
    enemyToolbox = new EnemyToolbox(this);
    towerToolbox = new TowerToolbox(this);

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
  public void editConfiguration(Configuration configuration) throws IllegalStateException {
    if (currentGameState != GameState.READY) {
      throw new IllegalStateException();
    }
    this.configuration = configuration;
    mapToolbox = new MapToolbox(seed);
    mapToolbox.generateMap(configuration.mapSize());
    waveGenerator = new WaveGenerator(mapToolbox.getPath().getFirst(), seed, configuration);
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
    if (currentGameState != GameState.READY) {
      throw new IllegalStateException();
    }
    updateGameState(GameState.PAUSED);
    player = new Player(configuration.startingHealth(), configuration.startingMoney());
    notifyListeners(TowerDefenseListener::updateHealth);
    notifyListeners(TowerDefenseListener::updateMoney);
  }

  @Override
  public void abortGame() throws IllegalStateException {
    seed = new Random().nextLong();

    mapToolbox = new MapToolbox(seed);
    mapToolbox.generateMap(configuration.mapSize());
    notifyListeners(TowerDefenseListener::updateMap);

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
    notifyListeners(TowerDefenseListener::updateTowerMap);
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

  @Override
  public void terminate() {
    gameLoop.interrupt();
    updateGameState(GameState.TERMINATED);
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
  }

  private void roundCompleted() {
    logger.info("Round {} completed", currentRound);
    updateGameState(GameState.PAUSED);
    gameLoop.stopGame();
    savedPlayerData = player;
    towerToolbox.saveData();
  }

  public EnemyToolbox getEnemyToolbox() {
    return enemyToolbox;
  }

  public TowerToolbox getTowerToolbox() {
    return towerToolbox;
  }

  public MapToolbox getMapToolbox() {
    return mapToolbox;
  }

}
