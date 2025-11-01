package de.hhn.it.devtools.components.towerDefenseComponents;

import java.util.Map;

public interface WaveGenerator {
	// TODO Methoden adden


	/**
	 * Calculates the total enemy power for a given wave.
	 *
	 * @param wave the wave number to calculate the power for
	 * @return the corresponding amount of enemy power
	 * @throws IllegalArgumentException if wave number is less than or equal to zero
	 */
	int getEnemyPower(int wave, int multiplier);

	/**
	 * Generates a wave of enemies with the enemyPower
	 *
	 * @param enemyPower will determine how strong the wave will be
	 * @return Map filled with enemies and their id
	 * @throws IllegalArgumentException if enemyPower number is less than or equal to zero
	 */
	Map<Integer, Enemy> generateWave(int enemyPower);
}
