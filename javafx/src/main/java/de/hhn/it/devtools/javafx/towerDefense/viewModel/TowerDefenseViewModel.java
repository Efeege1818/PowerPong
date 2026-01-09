package de.hhn.it.devtools.javafx.towerDefense.viewModel;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Player;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseListener;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;


public class TowerDefenseViewModel implements TowerDefenseListener {
  SimpleTowerDefenseService service;
  private final ObjectProperty<Grid> map = new SimpleObjectProperty<>();
  private final ObjectProperty<Player> player = new SimpleObjectProperty<>();
  private final ListProperty<Enemy> enemies = new SimpleListProperty<>(FXCollections.observableArrayList());
  private final ListProperty<Tower> towers = new SimpleListProperty<>(FXCollections.observableArrayList());
  private final IntegerProperty round = new SimpleIntegerProperty();

  public TowerDefenseViewModel(SimpleTowerDefenseService service) {
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

  public ObjectProperty<Player> getPlayerStats() {
    return player;
  }

  public IntegerProperty getRound() {
    return round;
  }

  public ListProperty<Enemy> getEnemies() {
    return enemies;
  }

  public ListProperty<Tower> getTowers() {
    // TODO: set this to param {@Code towers}
//    return new SimpleListProperty<>(FXCollections.observableArrayList(
//            service.getTowerBoard().values()));
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

  public void sync() {
    this.map.set(service.getMap());
  }
}
