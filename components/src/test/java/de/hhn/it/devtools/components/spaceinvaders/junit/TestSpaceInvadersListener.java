package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
import de.hhn.it.devtools.apis.spaceinvaders.entities.Alien;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test helper that records which callbacks were invoked and with what last value.
 */
public class TestSpaceInvadersListener implements SpaceInvadersListener {
  public final AtomicBoolean barrierUpdated = new AtomicBoolean(false);
  public final AtomicBoolean aliensUpdated = new AtomicBoolean(false);
  public final AtomicBoolean shipUpdated = new AtomicBoolean(false);
  public final AtomicBoolean projectilesUpdated = new AtomicBoolean(false);
  public final AtomicBoolean alienDamaged = new AtomicBoolean(false);
  public final AtomicBoolean soundUpdated = new AtomicBoolean(false);
  public final AtomicReference<GameState> lastState = new AtomicReference<>();
  public final AtomicInteger lastRound = new AtomicInteger(-1);
  public final AtomicBoolean gameEnded = new AtomicBoolean(false);
  public final AtomicInteger lastScore = new AtomicInteger(-1);
  public final AtomicReference<Object> lastConfiguration = new AtomicReference<>();
  public volatile Alien[] lastAliens = null;

  @Override
  public void updateBarrier(de.hhn.it.devtools.apis.spaceinvaders.entities.Barrier barrier) {
    barrierUpdated.set(true);
  }

  @Override
  public void updateAliens(Alien[] aliens) {
    aliensUpdated.set(true);
    this.lastAliens = aliens;
  }

  @Override
  public void updateShip(de.hhn.it.devtools.apis.spaceinvaders.entities.Ship ship) {
    shipUpdated.set(true);
  }

  @Override
  public void updateProjectiles(de.hhn.it.devtools.apis.spaceinvaders.entities.Projectile[] projectile) {
    projectilesUpdated.set(true);
  }

  @Override
  public void damageAlien(de.hhn.it.devtools.apis.spaceinvaders.entities.Alien alien) {
    alienDamaged.set(true);
  }

  @Override
  public void updateSound(de.hhn.it.devtools.apis.spaceinvaders.Sound sound) {
    soundUpdated.set(true);
  }

  @Override
  public void changedGameState(GameState gameState) {
    lastState.set(gameState);
  }

  @Override
  public void updateRound(int round) {
    lastRound.set(round);
  }

  @Override
  public void gameEnded() {
    gameEnded.set(true);
  }

  @Override
  public void updateScore(int score) {
    lastScore.set(score);
  }

  @Override
  public void updateGameConfiguration(de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration configuration) {
    lastConfiguration.set(configuration);
  }
}

