package de.hhn.it.devtools.apis.examples.coffeemakerservice;

/**
 * Callback to notify observers about a state change of a coffee maker.
 */
public interface CoffeeMakerListener {

  /**
   *  Informs the listener that the CoffeeMaker has changed its state.
   *
   * @param state actual state of the CoffeeMaker
   */
  void newState(State state);

  /**
   * Informs the listener that the cup counter has changed.
   *
   * @param value actual value of the cupsSinceLastMaintenance counter
   */
  void newCupsCounter(int value);
}
