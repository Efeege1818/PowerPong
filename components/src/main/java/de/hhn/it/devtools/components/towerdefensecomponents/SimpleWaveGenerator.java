package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import java.util.HashMap;
import java.util.Map;

// LOCKED: L.Missbach
public class SimpleWaveGenerator implements WaveGenerator {
  // saved data
  Map<Integer, Map<Integer, Enemy>> waveMap = new HashMap<>();

  @Override
  public int getEnemyPower(int wave, int multiplier) {
    return 0;
  }

  @Override
  public Map<Integer, Enemy> generateWave(int enemyPower) {
    // TODO: add to saved data
    return Map.of();
  }

  @Override
  public Map<Integer, Map<Integer, Enemy>> wavePerRound(int round) {
    return Map.of();
  }
}
