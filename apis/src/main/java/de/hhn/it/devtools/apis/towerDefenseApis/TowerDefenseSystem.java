package de.hhn.it.devtools.apis.towerDefenseApis;

/**
 * Interface for the TowerDefenseSystem
 */
public interface TowerDefenseSystem {
int DEFAULT_SIZE = 10;
int DEFAULT_HEALTH = 50;
double DEFAULT_ENEMY_POWER_MULTIPLIER = 1.5;    //Shouldn't this be of type float?
int DEFAULT_ENERGY_RATE = 1;

    /**
     * Provides the number of the last round that has been started
     * @return the last round number as a positive Integer
     */
    public int getCurrentRound();

    /**
     * Calculated the amount of enemy power for the given wave
     * @param wave the wave to calculate the power for
     * @return the corresponding amount of enemy power
     */
    public int getEnemyPower(int wave);
}
