package de.hhn.it.devtools.apis.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.exceptions.IllegalConfigurationException;

/**
 * Interface for the ShaperSurvivor game service
 */
public interface ShapeSurvivorService {

    /**
     * Resets the game to its initial state. Sets GameState to PREPARED.
     */
    void reset();

    /**
     * Starts the game if the GameState is PREPARED. Sets GameState to RUNNING.
     *
     * @throws IllegalStateException if the GameState is not PREPARED
     */
    void start() throws IllegalStateException;

    /**
     * Aborts the current game if GameState is RUNNING or PAUSED. Sets GameState to ABORTED.
     *
     * @throws IllegalStateException if the GameState is PREPARED or already ABORTED
     */
    void abort() throws IllegalStateException;

    /**
     * Pauses the game if GameState is RUNNING. Sets GameState to PAUSED.
     *
     * @throws IllegalStateException if the GameState is not RUNNING
     */
    void pause() throws IllegalStateException;

    /**
     * Resumes the game if GameState is PAUSED. Sets GameState to RUNNING.
     *
     * @throws IllegalStateException if the GameState is not PAUSED
     */
    void resume() throws IllegalStateException;

    /**
     * Moves the player in the specified direction.
     *
     * @param direction the direction to move
     * @throws IllegalStateException if the GameState is not RUNNING
     * @throws IllegalArgumentException if direction is null
     */
    void movePlayer(Direction direction) throws IllegalStateException, IllegalArgumentException;

    /**
     * Selects a weapon upgrade when the player levels up.
     *
     * @param weaponType the type of weapon to unlock or upgrade
     * @throws IllegalStateException if the GameState is not RUNNING or if no level up is pending
     * @throws IllegalArgumentException if weaponType is null or invalid
     */
    void selectWeapon(WeaponType weaponType) throws IllegalStateException, IllegalArgumentException;

    /**
     * Upgrades a specific player attribute when leveling up.
     *
     * @param attribute the attribute to upgrade
     * @param value the value to apply (either multiplier or fixed increase)
     * @param isMultiplier if true, applies value as multiplier; if false, applies as fixed increase
     * @throws IllegalStateException if the GameState is not RUNNING or if no level up is pending
     * @throws IllegalArgumentException if attribute is null or invalid, or if value is invalid
     */
    void upgradeAttribute(PlayerAttribute attribute, double value, boolean isMultiplier) 
            throws IllegalStateException, IllegalArgumentException;

    /**
     * Adds a listener for game updates and events.
     *
     * @param listener the listener to be added
     * @return true if the listener was successfully added, false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean addListener(ShapeSurvivorListener listener) throws IllegalArgumentException;

    /**
     * Removes a listener from the list of listeners.
     *
     * @param listener the listener to be removed
     * @return true if the listener was successfully removed, false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean removeListener(ShapeSurvivorListener listener) throws IllegalArgumentException;

    /**
     * Configures the game with specific parameters if GameState is PREPARED or ABORTED.
     *
     * @param configuration the game configuration parameters
     * @throws IllegalStateException if the GameState is not PREPARED or ABORTED
     * @throws IllegalConfigurationException if the configuration values are invalid
     * @throws IllegalArgumentException if configuration is null
     */
    void configure(GameConfiguration configuration)
            throws IllegalStateException, IllegalConfigurationException;

    /**
     * Returns the current game configuration.
     *
     * @return the current GameConfiguration record
     */
    GameConfiguration getConfiguration();

    /**
     * Returns the current game state.
     *
     * @return the current GameState
     */
    GameState getGameState();

    /**
     * Returns the current player state.
     *
     * @return the current Player record
     */
    Player getPlayer();

    /**
     * Returns the current enemies in the game.
     *
     * @return array of current Enemy records
     */
    Enemy[] getEnemies();

    /**
     * Checks if a level-up is pending and requires player action.
     *
     * @return true if the player has leveled up and needs to select an upgrade, false otherwise
     */
    boolean isLevelUpPending();

    /**
     * Returns the available weapon types that can be selected or upgraded.
     *
     * @return array of WeaponType that are available for selection
     * @throws IllegalStateException if no level up is pending
     */
    WeaponType[] getAvailableWeaponUpgrades() throws IllegalStateException;

    /**
     * Returns the available player attributes that can be upgraded.
     *
     * @return array of PlayerAttribute that are available for upgrade
     * @throws IllegalStateException if no level up is pending
     */
    PlayerAttribute[] getAvailableAttributeUpgrades() throws IllegalStateException;

    /**
     * Returns the current game statistics.
     *
     * @return the current GameStatistics record
     */
    GameStatistics getStatistics();

    /**
     * Returns the elapsed game time in seconds.
     *
     * @return the elapsed time since the game started in seconds
     * @throws IllegalStateException if the GameState is PREPARED or ABORTED
     */
    int getElapsedTime() throws IllegalStateException;

    /**
     * Returns the remaining game time in seconds.
     *
     * @return the remaining time until the game ends in seconds
     * @throws IllegalStateException if the GameState is PREPARED or ABORTED
     */
    int getRemainingTime() throws IllegalStateException;
}
