package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;

import java.util.Map;

public class SimpleWaveGenerator implements WaveGenerator {
    @Override
    public int getEnemyPower(int wave, int multiplier) {
        return 0;
    }

    @Override
    public Map<Integer, Enemy> generateWave(int enemyPower) {
        return Map.of();
    }
}
