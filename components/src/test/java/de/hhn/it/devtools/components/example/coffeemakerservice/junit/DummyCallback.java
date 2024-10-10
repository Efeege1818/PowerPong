package de.hhn.it.devtools.components.example.coffeemakerservice.junit;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerListener;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.State;

public class DummyCallback implements CoffeeMakerListener {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(DummyCallback.class);

  @Override
  public void newState(final State state) {
    logger.info("newState: {}", state);
  }

  @Override
  public void newCupsCounter(final int value) {
    logger.info("cupsCounter: {}", value);
  }
}
