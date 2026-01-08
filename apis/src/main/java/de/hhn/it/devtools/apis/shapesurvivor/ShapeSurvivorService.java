package de.hhn.it.devtools.apis.shapesurvivor;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
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
     * Applies a selected upgrade option when a level up is pending.
     * Can be called when the game is running or paused (e.g., during level-up selection).
     *
     * @param option the upgrade option to apply
     * @throws IllegalStateException if game is not running/paused or no level up is pending
     * @throws IllegalArgumentException if option is null or not available
     */
    void applyUpgrade(UpgradeOption option)
            throws IllegalStateException, IllegalArgumentException;

    /**
     * Gets the available upgrade options for the current level up.
     * Returns 3 random options that can be weapon upgrades, new weapons, or attribute upgrades.
     *
     * @return array of available upgrade options (typically 3 options)
     * @throws IllegalStateException if no level up is pending
     */
    UpgradeOption[] getAvailableUpgrades() throws IllegalStateException;

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
            throws IllegalStateException, IllegalConfigurationException, IllegalParameterException;

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