package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.entities.Entity;

/**
 * The EnemyGenerator class generates Enemies
 */
public interface EnemyGenerator {
  /**
   * Creates a number of Enemies depending on the game difficulty
   * @return Array of Entities
   */
  Entity[] generateAliens(Difficulty difficulty);
}
