package de.hhn.it.devtools.javafx.towerDefense.viewModel;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.Grid;
import de.hhn.it.devtools.apis.towerdefenseapi.Player;
import de.hhn.it.devtools.apis.towerdefenseapi.Tower;
import de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseListener;
import de.hhn.it.devtools.components.towerdefensecomponents.MapToolbox;
import de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
import de.hhn.it.devtools.components.towerdefensecomponents.TowerToolbox;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

import java.util.List;

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
    towers.add(tower);
    service.placeTower(tower);
  }

  public void startRound() {
    service.startGame();
    startNextRound();
  }

  public void abortGame() {
    service.abortGame();
  }

  public void resetGame() {
    service.resetGame();
  }

  public void startNextRound() {
    round.set(round.get() + 1);
    service.startNextRound();
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
    return towers;
  }

  @Override
  public void updateHealth() {

  }

  @Override
  public void updateMoney() {

  }

  @Override
  public void gameEnded() {

  }

  @Override
  public void gameCompleted() {

  }

  @Override
  public void updateMap() {

  }
}
