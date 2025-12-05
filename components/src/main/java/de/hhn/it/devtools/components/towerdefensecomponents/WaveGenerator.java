package de.hhn.it.devtools.components.towerdefensecomponents;

import de.hhn.it.devtools.apis.towerdefenseapi.Configuration;
import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Manages the generation of new Waves.
 */
public class WaveGenerator {

  private final Configuration configuration;
  private final long randomSeed;
  private final Coordinates startCoordinates;


  /**
   * Creates a new WaveGenerator.
   *
   * @param startCoordinates the coordinates where enemies start on the map
   * @param randomSeed the Seed that should be used for the random Generation
   * @param configuration the current configuration of the game
   */
  public WaveGenerator(Coordinates startCoordinates, long randomSeed, Configuration configuration) {
    this.configuration = configuration;
    this.randomSeed = randomSeed;
    this.startCoordinates = startCoordinates;
  }

  /**
   * Generates a wave of enemies with the enemyPower.
   *
   * @param wave the wave for which the enemies are generated
   * @return Map filled with enemies and their id
   */
  public Map<UUID, Enemy> generateWave(int wave) {

    Map<UUID, Enemy> enemyMap = new HashMap<>();

    Random random = createRandomGenerator(wave);
    int remainingPower = calculatePower(wave);

    // Create a Map with all accessible EnemyTypes and their weights for easy access later
    Map<EnemyType, Integer> weightMap = new HashMap<>();
    Arrays.stream(EnemyType.values())
        .forEach(n -> weightMap.put(n, EnemyToolbox.getWeight(n)));

    int minPower = weightMap.keySet().stream().mapToInt(EnemyToolbox::getMoney).min().orElse(0);
    assert minPower > 0;

    while (remainingPower >= minPower) {

      // Remove all entries from the Map, that have higher power than what is left to spend.
      Iterator<EnemyType> iterator = weightMap.keySet().iterator();
      while (iterator.hasNext()) {
        EnemyType next = iterator.next();
        if (EnemyToolbox.getMoney(next) > remainingPower) {
          iterator.remove();
        }
      }

      Iterator<EnemyType> types = weightMap.keySet().iterator();
      int combinedWeight = weightMap.values().stream().mapToInt(n -> n).sum();
      int randomValue = random.nextInt(combinedWeight);
      EnemyType nextEnemyType;

      do {
        nextEnemyType = types.next();
        randomValue -= weightMap.get(nextEnemyType);
      } while (randomValue >= 0);

      Enemy enemy = createEnemy(nextEnemyType, wave);
      enemyMap.put(enemy.id(), enemy);
      remainingPower -= EnemyToolbox.getMoney(nextEnemyType);

    }

    return Map.copyOf(enemyMap);
  }

  private Random createRandomGenerator(int wave) {
    Random random =  new Random(randomSeed ^ wave);
    // Let the RNG run through a few times
    // to eliminate any obvious patterns that emerge from similar seeds.
    for (int i = 0; i < 5; i++) {
      random.nextInt();
    }
    return random;
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  private double calculateHMW(int wave) {
    return configuration.enemyHealthMultiplier() * Math.pow(wave, configuration.escalation());
  }

  private int calculatePower(int wave) {
    return (int) configuration.enemyPowerMultiplier() * wave;
  }

  private Enemy createEnemy(EnemyType type, int wave) {
    return new Enemy(
        UUID.randomUUID(),
        startCoordinates,
        type,
        (int) (EnemyToolbox.getMaxHealth(type) * calculateHMW(wave)),
        0
    );
  }
}
