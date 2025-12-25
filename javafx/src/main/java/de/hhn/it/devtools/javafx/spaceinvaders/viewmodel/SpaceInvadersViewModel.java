package de.hhn.it.devtools.javafx.spaceinvaders.viewmodel;

import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.Sound;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Ship;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * ViewModel for SpaceInvadersScreen.
 */
public class SpaceInvadersViewModel implements SpaceInvadersListener {
  private final IntegerProperty currentRound = new SimpleIntegerProperty(1);

  @Override
  public void updateBarrier(Barrier barrier) {

  }

  @Override
  public void updateAliens(Alien[] aliens) {

  }

  @Override
  public void updateShip(Ship ship) {

  }

  @Override
  public void updateProjectile(Projectile projectile) {

  }

  @Override
  public void damageAlien(Alien alien) {

  }

  @Override
  public void updateSound(Sound sound) {

  }

  @Override
  public void changedGameState(GameState gameState) {

  }

  @Override
  public void updateRound(int round) {
    currentRound.set(round);
  }

  @Override
  public void gameEnded() {

  }

  @Override
  public void updateScore(int score) {

  }

  @Override
  public void updateGameConfiguration(GameConfiguration configuration) {

  }

  public IntegerProperty getCurrentRoundProperty() {
    return currentRound;
  }
}
