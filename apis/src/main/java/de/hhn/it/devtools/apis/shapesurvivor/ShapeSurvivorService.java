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
    void movePlayer(Direction direction) throws IllegalStateException;

    /**
     * Selects a weapon upgrade when the player levels up.
     *
     * @param weaponType the type of weapon to unlock or upgrade
     * @throws IllegalStateException if the GameState is not RUNNING or if no level up is pending
     * @throws IllegalArgumentException if weaponType is null or invalid
     */
    void selectWeapon(WeaponType weaponType) throws IllegalStateException;

    /**
     * Upgrades a specific player attribute when leveling up.
     *
     * @param attribute the attribute to upgrade
     * @throws IllegalStateException if the GameState is not RUNNING or if no level up is pending
     * @throws IllegalArgumentException if attribute is null or invalid
     */
    void upgradeAttribute(PlayerAttribute attribute) throws IllegalStateException;

    /**
     * Adds a listener for game updates and events.
     *
     * @param listener the listener to be added
     * @return true if the listener was successfully added, false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean addListener(ShapeSurvivorListener listener);

    /**
     * Removes a listener from the list of listeners.
     *
     * @param listener the listener to be removed
     * @return true if the listener was successfully removed, false otherwise
     * @throws IllegalArgumentException if listener is null
     */
    boolean removeListener(ShapeSurvivorListener listener);

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
     * Returns the current game statistics.
     *
     * @return the current GameStatistics record
     */
    GameStatistics getStatistics();
}
