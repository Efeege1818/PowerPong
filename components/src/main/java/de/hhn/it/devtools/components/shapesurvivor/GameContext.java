package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.helper.EnemyState;

import java.util.*;

public class GameContext {

    GameState gameState = GameState.PREPARED;
    GameConfiguration configuration;
    Player player;
    final List<EnemyState> enemies = new ArrayList<>();
    long gameStartTime;
    boolean levelUpPending;
    int currentWave;
    int nextEnemyId;
    long lastWaveSpawnTime;
    final Map<WeaponType, WeaponAnimationState> weaponStates = new HashMap<>();
    final Map<Integer, Long> lastEnemyHitTime = new HashMap<>();
    long lastWeaponUpdateTime;
    long lastPlayerHitTime = 0;
    GameStatistics statistics;

    GameContext(GameConfiguration initialConfig) {
        this.configuration = initialConfig;
    }

    void reset() {
        this.gameState = GameState.PREPARED;
        enemies.clear();
        weaponStates.clear();
        lastEnemyHitTime.clear();
        currentWave = 0;
        nextEnemyId = 0;
        levelUpPending = false;
        lastPlayerHitTime = 0;
    }

    public  GameState getGameState() {
        return gameState;
    }
    public Player getPlayer() {
        return player;
    }
    public List<Enemy> getEnemiesSnapshot() {
        List<Enemy> snapshot = new ArrayList<>(enemies.size());
        for (EnemyState e : enemies) {
            snapshot.add(e.toEnemy());
        }
        return snapshot;
    }
}
