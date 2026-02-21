package de.hhn.it.devtools.apis.spaceinvaders.exceptions;

/**
 * Signals that the GameConfiguration contains values not appropriate for the game.
 */
public class IllegalConfigurationException extends RuntimeException {

  /**
   * Constructs an IllegalConfigurationException with the specified detail message.
   *
   * @param message the detail message
   */
  public IllegalConfigurationException(String message) {
    super(message);
  }
}
