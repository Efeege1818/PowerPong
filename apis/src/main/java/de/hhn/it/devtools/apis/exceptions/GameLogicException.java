package de.hhn.it.devtools.apis.exceptions;

/**
 * A custom exception for errors that occur within the game logic
 * (e.g., an invalid game state).
 */
public class GameLogicException extends Exception {
    public GameLogicException(String message) {
        super(message);
    }

    public GameLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}
