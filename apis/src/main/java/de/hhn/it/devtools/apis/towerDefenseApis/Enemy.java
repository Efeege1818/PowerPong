package de.hhn.it.devtools.apis.towerDefenseApis;

public interface Enemy {
	int health();

	int speed();

	Path path();

	int id();

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
