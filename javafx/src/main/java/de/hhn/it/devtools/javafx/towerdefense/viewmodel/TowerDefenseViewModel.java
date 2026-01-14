package de.hhn.it.devtools.javafx.towerdefense.viewmodel;

import de.hhn.it.devtools.apis.towerdefense.Configuration;
import de.hhn.it.devtools.apis.towerdefense.Enemy;
import de.hhn.it.devtools.apis.towerdefense.GameState;
import de.hhn.it.devtools.apis.towerdefense.Grid;
import de.hhn.it.devtools.apis.towerdefense.Tower;
import de.hhn.it.devtools.apis.towerdefense.TowerDefenseListener;
import de.hhn.it.devtools.apis.towerdefense.TowerDefenseService;
import de.hhn.it.devtools.apis.towerdefense.TowerType;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

/**
 * Collective View Model for all TowerDefense Screens.
 */
public class TowerDefenseViewModel implements TowerDefenseListener {

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(TowerDefenseViewModel.class);

  private final TowerDefenseService service;
  private final ObjectProperty<Grid> map = new SimpleObjectProperty<>();
  private final ListProperty<Enemy> enemies = new SimpleListProperty<>(
          FXCollections.observableArrayList());
  private final ListProperty<Tower> towers = new SimpleListProperty<>(
          FXCollections.observableArrayList());
  private final IntegerProperty round = new SimpleIntegerProperty();
  private final IntegerProperty health = new SimpleIntegerProperty();
  private final IntegerProperty money = new SimpleIntegerProperty();
  private final BooleanProperty gameOver = new SimpleBooleanProperty();
  private final ObjectProperty<GameState> gameState = new SimpleObjectProperty<>();

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

  public void retryRound() {
    service.retry();
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

  public BooleanProperty getGameOver() {
    return gameOver;
  }

  public Map<TowerType, Integer> getTowerTypes() {
    return service.getTowerTypes();
  }

  public ObjectProperty<GameState> getCurrentGameState() {
    return gameState;
  }

  public void editConfiguration(Configuration configuration) {
    service.editConfiguration(configuration);
  }

  @Override
  public void updateGameState() {
    Platform.runLater(() -> gameState.set(service.getCurrentGameState()));
    logger.debug("GameState: " + service.getCurrentGameState());
  }

  @Override
  public void updateHealth() {
    Platform.runLater(() -> health.set(service.getPlayer().health()));
    logger.debug("Health: " + service.getPlayer().health());
  }

  @Override
  public void updateMoney() {
    Platform.runLater(() -> money.set(service.getPlayer().money()));
    logger.debug("Money: " + service.getPlayer().money());
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
    Platform.runLater(() -> enemies.setAll(service.getCurrentEnemies()));
  }

  /**
   * Returns the color of the tower of the given TowerType.
   *
   * @param type the requested TowerType
   * @return the Color of that Type
   */
  public Color getTowerColors(TowerType type) {
    return switch (type) {
      case MELEE -> Color.BLUE;
      case RANGED -> Color.CYAN;
      case MONEYMAKER -> Color.DARKBLUE;
      default -> Color.HOTPINK; // Use Hotpink as default value to spot errors
    };
  }

  /**
   * Synchronizes all Properties in this ViewModel with the corresponding values in the Service.
   */
  public void sync() {
    Platform.runLater(() -> {
      towers.setAll(service.getTowerBoard().values());
      enemies.setAll(service.getCurrentEnemies());
      this.map.set(service.getMap());
      health.set(service.getPlayer().health());
      money.set(service.getPlayer().money());
      round.set(service.getCurrentRound());
    });
  }
}
