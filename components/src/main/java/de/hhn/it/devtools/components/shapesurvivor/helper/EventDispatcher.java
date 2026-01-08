package de.hhn.it.devtools.components.shapesurvivor.helper;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.GameContext;

import java.util.List;

public class EventDispatcher {

    private final List<ShapeSurvivorListener> listeners;
    private final GameContext gameContext;
    private final ShapeSurvivorService service;

    public EventDispatcher(List<ShapeSurvivorListener> listeners,GameContext context, ShapeSurvivorService service) {
        this.listeners = listeners;
        this.gameContext = context;
        this.service = service;
    }

    public void notifyPlayerUpdated() {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updatePlayer(gameContext.getPlayer().toPlayer());
        }
    }

    public void notifyEnemiesUpdated() {
        Enemy[] enemyArray =  gameContext.getEnemiesSnapshot().toArray(new Enemy[0]);
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateEnemies(enemyArray);
        }
    }

    public void notifyWeaponUpdated(Weapon weapon) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateWeapon(weapon);
        }
    }

    public void notifyPlayerDamaged(int damage) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.playerDamaged(damage);
        }
    }

    public void notifyEnemyDamaged(Enemy enemy, int damage) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.enemyDamaged(enemy, damage);
        }
    }

    public void notifyEnemyKilled(Enemy enemy, int exp) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.enemyKilled(enemy, exp);
        }
    }

    public void notifyPlayerLeveledUp() {
        for (ShapeSurvivorListener listener : listeners) {
            listener.playerLeveledUp();
        }
    }

    public void notifyGameStateChanged(GameState state) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.changedGameState(state);
        }
    }

    public void notifyTimeUpdate() {
        if (gameContext.getGameState() != GameState.RUNNING) {
            return;
        }

        int remaining = service.getRemainingTime();
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateRemainingTime(remaining);
        }
    }

    public void notifyEnemyWaveSpawned(int wave, int count) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.enemyWaveSpawned(wave, count);
        }
    }

    public void notifyGameEnded(boolean victory) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.gameEnded(victory);
        }
    }

    public void notifyExperienceUpdated() {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateExperience(gameContext.getPlayer().getExperience(), gameContext.getPlayer().getExperienceToNextLevel());
        }
    }

    public void notifyConfigurationUpdated(GameConfiguration config) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateGameConfiguration(config);
        }
    }
}
