package de.hhn.it.devtools.components.towerDefenseComponents;

//TODO Kommentare adden

import de.hhn.it.devtools.apis.towerDefenseApis.Coordinates;
import de.hhn.it.devtools.components.towerDefenseComponents.Path;

public interface Enemy {
	int getHealth();

	int getSpeed();

	Path getPath();

	int getId();

	/**
	 * Reduces the health of the enemy by the given amount and markes the enemy as dead,
	 * if the health has reached zero.
	 *
	 * @param amount the amount of health the enemy's health should be reduced by
	 * @return true if the enemy dies due to the health reduction
	 * @throws IllegalArgumentException if the amount is negative
	 */
	public boolean getDamage(int amount);

	public Coordinates move();

	public void endReached();
}
