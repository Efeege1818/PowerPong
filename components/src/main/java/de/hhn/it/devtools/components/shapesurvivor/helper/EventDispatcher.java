package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.Enemy;
import de.hhn.it.devtools.apis.shapesurvivor.GameConfiguration;
import de.hhn.it.devtools.apis.shapesurvivor.GameState;
import de.hhn.it.devtools.apis.shapesurvivor.ShapeSurvivorListener;
import de.hhn.it.devtools.apis.shapesurvivor.ShapeSurvivorService;
import de.hhn.it.devtools.apis.shapesurvivor.Weapon;
import de.hhn.it.devtools.components.shapesurvivor.GameContext;

import java.util.List;

/**
 * Dispatches game events to registered listeners.
 */
public class EventDispatcher {

  private final List<ShapeSurvivorListener> listeners;
  private final GameContext gameContext;
  private final ShapeSurvivorService service;

  /**
   * Creates a new EventDispatcher.
   * @param listeners list of listeners to notify
   * @param context game context
   * @param service the game service
   */
  public EventDispatcher(List<ShapeSurvivorListener> listeners,
                         GameContext context,
                         ShapeSurvivorService service) {
    this.listeners = listeners;
    this.gameContext = context;
    this.service = service;
  }

  /**
   * Notifies all listeners of player updates.
   */
  public void notifyPlayerUpdated() {
    for (ShapeSurvivorListener listener : listeners) {
      listener.updatePlayer(gameContext.getPlayer().toPlayer());
    }
  }

  /**
   * Notifies all listeners of enemy updates.
   */
  public void notifyEnemiesUpdated() {
    Enemy[] enemyArray =  gameContext.getEnemiesSnapshot().toArray(new Enemy[0]);
    for (ShapeSurvivorListener listener : listeners) {
      listener.updateEnemies(enemyArray);
    }
  }

  /**
   * Notifies all listeners of weapon updates.
   *  @param weapon the updated weapon
   */
  public void notifyWeaponUpdated(Weapon weapon) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.updateWeapon(weapon);
    }
  }

  /**
   * Notifies all listeners that the player was damaged.
   *  @param damage amount of damage taken
   */
  public void notifyPlayerDamaged(int damage) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.playerDamaged(damage);
    }
  }

  /**
   * Notifies all listeners that an enemy was damaged.
   * @param enemy the damaged enemy
   * @param damage amount of damage dealt
   */
  public void notifyEnemyDamaged(Enemy enemy, int damage) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.enemyDamaged(enemy, damage);
    }
  }

  /**
   * Notifies all listeners that an enemy was killed.
   *  @param enemy the killed enemy
   *  @param exp experience gained
   */
  public void notifyEnemyKilled(Enemy enemy, int exp) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.enemyKilled(enemy, exp);
    }
  }

  /**
   * Notifies all listeners that the player leveled up.
   */
  public void notifyPlayerLeveledUp() {
    for (ShapeSurvivorListener listener : listeners) {
      listener.playerLeveledUp();
    }
  }

  /**
   * Notifies all listeners of game state changes.
   *  @param state the new game state
   */
  public void notifyGameStateChanged(GameState state) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.changedGameState(state);
    }
  }

  /**
   * Notifies all listeners of time updates.
   */
  public void notifyTimeUpdate() {
    if (gameContext.getGameState() != GameState.RUNNING) {
      return;
    }

    int remaining = service.getRemainingTime();
    for (ShapeSurvivorListener listener : listeners) {
      listener.updateRemainingTime(remaining);
    }
  }

  /**
   * Notifies all listeners that a new enemy wave spawned.
   *  @param wave wave number
   *  @param count number of enemies
   */
  public void notifyEnemyWaveSpawned(int wave, int count) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.enemyWaveSpawned(wave, count);
    }
  }

  /**
   * Notifies all listeners that the game ended.
   *  @param victory whether the game was won
   */
  public void notifyGameEnded(boolean victory) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.gameEnded(victory);
    }
  }

  /**
   * Notifies all listeners of experience updates.
   */
  public void notifyExperienceUpdated() {
    for (ShapeSurvivorListener listener : listeners) {
      listener.updateExperience(gameContext.getPlayer().getExperience(),
              gameContext.getPlayer().getExperienceToNextLevel());
    }
  }

  /**
   * Notifies all listeners of configuration updates.
   *  @param config the new configuration
   */
  public void notifyConfigurationUpdated(GameConfiguration config) {
    for (ShapeSurvivorListener listener : listeners) {
      listener.updateGameConfiguration(config);
    }
  }
}