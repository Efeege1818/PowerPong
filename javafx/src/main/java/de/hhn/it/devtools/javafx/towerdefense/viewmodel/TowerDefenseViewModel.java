package de.hhn.it.devtools.javafx.towerdefense.viewmodel;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.GameState;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseListener;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerType;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import java.util.Map;

/**
 * Collective View Model for all TowerDefense Screens.
 */
public class TowerDefenseViewModel implements TowerDefenseListener {
  TowerDefenseService service;
  private final ObjectProperty<Grid> map = new SimpleObjectProperty<>();
  private final ListProperty<Enemy> enemies = new SimpleListProperty<>(
          FXCollections.observableArrayList());
  private final ListProperty<Tower> towers = new SimpleListProperty<>(
          FXCollections.observableArrayList());
  private final IntegerProperty round = new SimpleIntegerProperty();
  private final IntegerProperty health = new SimpleIntegerProperty();
  private final IntegerProperty money = new SimpleIntegerProperty();
  private final BooleanProperty gameOver = new SimpleBooleanProperty();

  public TowerDefenseViewModel(TowerDefenseService service) {
    this.service = service;
    this.service.addListener(this);
    map.set(service.getMap());
    round.set(service.getCurrentRound());
  }

  public void addTower(Tower tower) {
    service.placeTower(tower);
    sync();
  }

  public void startGame() {
    service.startGame();
  }

  public void abortGame() {
    service.abortGame();
    sync();
  }

  public void resetGame() {
    service.resetGame();
    sync();
  }

  public void resetRound() {
    service.retry();

  }

  public void startNextRound() {
    service.startNextRound();
    sync();
  }

  public ObjectProperty<Grid> getMap() {
    return map;
  }

  public IntegerProperty getHealth() {
    sync();
    return health;
  }

  public IntegerProperty getMoney() {
    sync();
    return money;
  }

  public IntegerProperty getRound() {
    return round;
  }

  public ListProperty<Enemy> getEnemies() {
    return enemies;
  }

  public ListProperty<Tower> getTowers() {
    sync();
    return towers;
  }

  public BooleanProperty getGameOver() {
    return gameOver;
  }

  public Map<TowerType, Integer> getTowerTypes() {
    return service.getTowerTypes();
  }

  public GameState getCurrentGameState() {
    return service.getCurrentGameState();
  }

  @Override
  public void updateHealth() {
    health.set(service.getPlayer().health());
  }

  @Override
  public void updateMoney() {
    money.set(service.getPlayer().money());
  }

  @Override
  public void gameEnded() {
    gameOver.setValue(true);
  }

  @Override
  public void gameCompleted() {
    sync();
  }

  @Override
  public void updateMap() {
    sync();
  }

  @Override
  public void tick() {
    enemies.setAll(service.getCurrentEnemies());
  }

  public void sync() {
    towers.setAll(service.getTowerBoard().values());
    enemies.setAll(service.getCurrentEnemies());
    this.map.set(service.getMap());
    health.set(service.getPlayer().health());
    money.set(service.getPlayer().money());
  }
}
