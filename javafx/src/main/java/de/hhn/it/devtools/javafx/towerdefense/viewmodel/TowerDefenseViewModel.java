package de.hhn.it.devtools.javafx.towerdefense.viewmodel;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Player;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseListener;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;


public class TowerDefenseViewModel implements TowerDefenseListener {
  TowerDefenseService service;
  private final ObjectProperty<Grid> map = new SimpleObjectProperty<>();
  private final ObjectProperty<Player> player = new SimpleObjectProperty<>();
  private final ListProperty<Enemy> enemies = new SimpleListProperty<>(FXCollections.observableArrayList());
  private final ListProperty<Tower> towers = new SimpleListProperty<>(FXCollections.observableArrayList());
  private final IntegerProperty round = new SimpleIntegerProperty();
  private final IntegerProperty health = new SimpleIntegerProperty();
  private final IntegerProperty money = new SimpleIntegerProperty();

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

  public void startRound() {
    service.startGame();
    startNextRound();
    sync();
  }

  public void abortGame() {
    service.abortGame();
    sync();
  }

  public void resetGame() {
    service.resetGame();
    sync();
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

  @Override
  public void updateHealth() {
    sync();
  }

  @Override
  public void updateMoney() {
    sync();
  }

  @Override
  public void gameEnded() {
    sync();
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

  }

  public void sync() {
    towers.setAll(service.getTowerBoard().values());
    enemies.setAll(service.getCurrentEnemies());
    this.map.set(service.getMap());
    health.set(service.getPlayer().health());
    money.set(service.getPlayer().money());
  }
}
