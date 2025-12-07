package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Switches between the different screens of the game.
 */
public interface ScreenManager {

  /**
   * Triggers the switch of screens.
   *
   * @param fromScreen Screen that triggers the switch.
   * @param toScreen Screen being switched to.
   * @throws UnknownTransitionException if the transition between the given screens is not specified
   */
  void switchTo(String fromScreen, String toScreen) throws UnknownTransitionException;
}
