package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.helper.EnemyState;
import de.hhn.it.devtools.components.shapesurvivor.helper.PlayerState;

import java.util.*;

public class GameContext {

    private GameState gameState = GameState.PREPARED;
    private GameConfiguration configuration;
    private PlayerState player;
    private final List<EnemyState> enemies = new ArrayList<>();
    private long gameStartTime;
    private boolean levelUpPending;
    private int currentWave;
    private int nextEnemyId;
    private long lastWaveSpawnTime;
    private final Map<WeaponType, WeaponAnimationState> weaponStates = new HashMap<>();
    private final Map<Integer, Long> lastEnemyHitTime = new HashMap<>();
    private long lastWeaponUpdateTime;
    private long lastPlayerHitTime = 0;
    private GameStatistics statistics;

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

    // Getters
    public GameState getGameState() {
        return gameState;
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }

    public PlayerState getPlayer() {
        return player;
    }

    public List<EnemyState> getEnemies() {
        return enemies;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public boolean isLevelUpPending() {
        return levelUpPending;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getNextEnemyId() {
        return nextEnemyId;
    }

    public long getLastWaveSpawnTime() {
        return lastWaveSpawnTime;
    }

    public Map<WeaponType, WeaponAnimationState> getWeaponStates() {
        return weaponStates;
    }

    public Map<Integer, Long> getLastEnemyHitTime() {
        return lastEnemyHitTime;
    }

    public long getLastWeaponUpdateTime() {
        return lastWeaponUpdateTime;
    }

    public long getLastPlayerHitTime() {
        return lastPlayerHitTime;
    }

    public GameStatistics getStatistics() {
        return statistics;
    }

    // Setters
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setConfiguration(GameConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setPlayer(PlayerState player) {
        this.player = player;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public void setLevelUpPending(boolean levelUpPending) {
        this.levelUpPending = levelUpPending;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public void setNextEnemyId(int nextEnemyId) {
        this.nextEnemyId = nextEnemyId;
    }

    public void incrementNextEnemyId() {
        this.nextEnemyId++;
    }

    public void setLastWaveSpawnTime(long lastWaveSpawnTime) {
        this.lastWaveSpawnTime = lastWaveSpawnTime;
    }

    public void setLastWeaponUpdateTime(long lastWeaponUpdateTime) {
        this.lastWeaponUpdateTime = lastWeaponUpdateTime;
    }

    public void setLastPlayerHitTime(long lastPlayerHitTime) {
        this.lastPlayerHitTime = lastPlayerHitTime;
    }

    public void setStatistics(GameStatistics statistics) {
        this.statistics = statistics;
    }

    public List<Enemy> getEnemiesSnapshot() {
        List<Enemy> snapshot = new ArrayList<>(enemies.size());
        for (EnemyState e : enemies) {
            snapshot.add(e.toEnemy());
        }
        return snapshot;
    }
}