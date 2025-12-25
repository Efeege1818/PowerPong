package de.hhn.it.devtools.javafx.spaceinvaders.viewmodel;

import de.hhn.it.devtools.apis.spaceinvaders.*;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * ViewModel for SpaceInvadersScreen.
 */
public class SpaceInvadersViewModel implements SpaceInvadersListener {
  private final IntegerProperty currentRound;
  private final ObservableMap<Integer, Barrier> barriers;
  private final ObservableMap<Integer, Alien> aliens;
  private final ObservableMap<Integer, Projectile> projectiles;
  private final ObjectProperty<Ship> shipObjectProperty;
  private final ObjectProperty<GameState> gameStateObjectProperty;
  private final IntegerProperty score;
  private final BooleanProperty running;

  public SpaceInvadersViewModel() {
    this.barriers = FXCollections.observableHashMap();
    this.currentRound = new SimpleIntegerProperty(1);
    this.aliens = FXCollections.observableHashMap();
    this.projectiles = FXCollections.observableHashMap();
    this.shipObjectProperty = new SimpleObjectProperty<>();
    this.gameStateObjectProperty = new SimpleObjectProperty<>();
    this.score = new SimpleIntegerProperty();
    this.running = new SimpleBooleanProperty();
  }

  @Override
  public void updateBarrier(Barrier barrier) {
    this.barriers.put(barrier.barrierId(), barrier);
    sync();
  }

  @Override
  public void updateAliens(Alien[] aliens) {
    for (Alien alien : aliens) {
      this.aliens.put(alien.alienId(), alien);
    }
    sync();
  }

  @Override
  public void updateShip(Ship ship) {
    this.shipObjectProperty.setValue(ship);
    sync();
  }

  @Override
  public void updateProjectile(Projectile projectile) {
    this.projectiles.put(projectile.projectileId(), projectile);
    sync();
  }

  @Override
  public void damageAlien(Alien alien) {
    this.aliens.put(alien.alienId(), alien);
    sync();
  }

  @Override
  public void updateSound(Sound sound) {
    //TODO
  }

  @Override
  public void changedGameState(GameState gameState) {
    this.gameStateObjectProperty.setValue(gameState);
  }

  @Override
  public void updateRound(int round) {
    currentRound.set(round);
  }

  @Override
  public void updateScore(int score) {
    this.score.set(score);
  }

  @Override
  public void updateGameConfiguration(GameConfiguration configuration) {

  }

  @Override
  public void gameEnded() {
    this.running.set(false);
  }

  public IntegerProperty getCurrentRoundProperty() {
    return currentRound;
  }

  public ObservableMap<Integer, Barrier> getBarriers() {
    return barriers;
  }

  public ObservableMap<Integer, Alien> getAliens() {
    return aliens;
  }

  public ObservableMap<Integer, Projectile> getProjectiles() {
    return projectiles;
  }

  public ObjectProperty<Ship> getShipObjectPropertyProperty() {
    return shipObjectProperty;
  }

  public ObjectProperty<GameState> getGameStateObjectPropertyProperty() {
    return gameStateObjectProperty;
  }

  public IntegerProperty getScoreProperty() {
    return score;
  }

  public Ship getShipObjectProperty() {
    return shipObjectProperty.get();
  }

  public BooleanProperty getSyncProperty() {
    return running;
  }

  private void sync() {
    this.running.set(true);
  }

}
