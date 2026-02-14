package de.hhn.it.devtools.apis.spaceinvaders;

/**
 * States the Game can be in.
 */
public enum GameState {
    /**
     * The Game is in the Preparation Phase, where the Player can
     * set up the Game and start it when ready.
     */
    PREPARED,
    /**
     * The Game is currently running and the Player can control the Ship.
     */
    RUNNING,
    /**
     * The Game is currently paused and the Player cannot control the Ship.
     */
    PAUSED,
    /**
     * The Game is aborted by the Player. The Game cannot be continued and must be restarted.
     */
    ABORTED,
    /**
     * The Game is Error, which means that something went wrong and the Game cannot be continued.
     * The Game must be restarted.
     */
    ERROR
}
