package de.hhn.it.devtools.javafx.spaceinvaders.viewmodel;

import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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

  /**
   * Constructor for SpaceInvadersViewModel.
   */
  public SpaceInvadersViewModel() {
    this.barriers = FXCollections.observableHashMap();
    this.currentRound = new SimpleIntegerProperty(1);
    this.aliens = FXCollections.observableHashMap();
    this.projectiles = FXCollections.observableHashMap();
    this.shipObjectProperty = new SimpleObjectProperty<>();
    this.gameStateObjectProperty = new SimpleObjectProperty<>();
    this.score = new SimpleIntegerProperty();
  }

  @Override
  public void updateBarrier(Barrier barrier) {
    Platform.runLater(() -> this.barriers.put(barrier.barrierId(), barrier));
  }

  @Override
  public void updateAliens(Alien[] aliens) {
    Platform.runLater(() -> {
      for (Alien alien : aliens) {
        this.aliens.put(alien.alienId(), alien);
      }
    });
  }

  @Override
  public void updateShip(Ship ship) {
    Platform.runLater((() -> this.shipObjectProperty.setValue(ship)));
  }

  @Override
  public void updateProjectile(Projectile projectile) {
    Platform.runLater(() -> this.projectiles.put(projectile.projectileId(), projectile));
  }

  @Override
  public void damageAlien(Alien alien) {
    Platform.runLater(() -> {
      if (alien.hitPoints() == 0) {
        this.aliens.remove(alien.alienId());
      } else {
        this.aliens.put(alien.alienId(), alien);
      }
    });
  }

  @Override
  public void updateSound(Sound sound) {
    // TODO: implement sound update.
  }

  @Override
  public void changedGameState(GameState gameState) {
    Platform.runLater(() -> this.gameStateObjectProperty.setValue(gameState));
  }

  @Override
  public void updateRound(int round) {
    Platform.runLater(() -> this.currentRound.set(round));
  }

  @Override
  public void updateScore(int score) {
    Platform.runLater(() -> this.score.set(score));
  }

  @Override
  public void updateGameConfiguration(GameConfiguration configuration) {
  }

  @Override
  public void gameEnded() {
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

  public IntegerProperty getScoreProperty() {
    return score;
  }

  public ObjectProperty<GameState> getGameStateObjectProperty() {
    return gameStateObjectProperty;
  }

}
