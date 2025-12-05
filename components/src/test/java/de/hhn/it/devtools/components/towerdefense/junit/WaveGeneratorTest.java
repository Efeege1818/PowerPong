package de.hhn.it.devtools.components.towerdefense.junit;

import de.hhn.it.devtools.apis.towerdefenseapi.Configuration;
import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;
import de.hhn.it.devtools.components.towerdefensecomponents.EnemyToolbox;
import de.hhn.it.devtools.components.towerdefensecomponents.WaveGenerator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MissingJavadocType")
@DisplayName("Tests for the WaveGenerator Class")
public class WaveGeneratorTest {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(WaveGeneratorTest.class);

  @Test
  public void createWaveTest() {

    Configuration configuration = new Configuration(
        Configuration.
    )

    WaveGenerator waveGenerator = new WaveGenerator(new Coordinates(0.0f, 0.0f), 123456789, 10.0f, 1.0f);

    logger.debug(waveGenerator.generateWave(1).toString());

    Assertions.assertEquals(10.0f,
        waveGenerator
            .generateWave(1)
            .values()
            .stream()
            .mapToInt(n -> EnemyToolbox.getMoney(n.type()))
            .sum());

  }

  @Test
  public void createWaveConsistencyTest() {

    WaveGenerator waveGenerator = new WaveGenerator(new Coordinates(0.0f, 0.0f), 123456789, 10.0f, 1.0f);
    List<EnemyType> generation1 = waveGenerator
        .generateWave(10)
        .values()
        .stream()
        .map(Enemy::type)
        .sorted()
        .toList();
    List<EnemyType> generation2 = waveGenerator
        .generateWave(10)
        .values()
        .stream()
        .map(Enemy::type)
        .sorted()
        .toList();

    logger.debug(generation1.toString());

    Assertions.assertEquals(generation1, generation2);

  }
}
