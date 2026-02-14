package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefense.*;
import de.hhn.it.devtools.components.towerdefense.WaveGenerator;
import de.hhn.it.devtools.components.towerdefense.EnemyToolbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import org.junit.jupiter.api.Assertions;

@DisplayName("GoodCases for WaveGenerator")
public class WaveGeneratorGoodCasesTest {

  private final Configuration configuration = new Configuration(10, 50, 100, 10, 1, 1);

  @Test
  public void createWaveTest() {
    WaveGenerator waveGenerator = new WaveGenerator(
            new Coordinates(0.0f, 0.0f), 123456789, configuration);
    Assertions.assertEquals(10.0f,
            waveGenerator.generateWave(1)
                    .stream()
                    .mapToInt(n -> EnemyToolbox.getMoney(n.type()))
                    .sum());
  }

  @Test
  public void createWaveConsistencyTest() {
    WaveGenerator waveGenerator = new WaveGenerator(
            new Coordinates(0.0f, 0.0f), 123456789, configuration);
    List<EnemyType> generation1 = waveGenerator
            .generateWave(10)
            .stream()
            .map(Enemy::type)
            .sorted()
            .toList();
    List<EnemyType> generation2 = waveGenerator
            .generateWave(10)
            .stream()
            .map(Enemy::type)
            .sorted()
            .toList();
    Assertions.assertEquals(generation1, generation2);
  }
}
