package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Enum to represent the different states that the game can be in.
 */
public enum GameState {
    /**
     * The game is currently running.
     */
    RUNNING,

    /**
     * The game is ready to start.
     */
    READY,

    /**
     * The game is finished.
     */
    END,

    /**
     * The game is aborted.
     */
    ABORTED,

    /**
     * The game has an error.
     */
    ERROR
}
