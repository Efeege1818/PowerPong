package de.hhn.it.devtools.components.example.coffeemakerservice.provider;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerListener;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.State;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.makerstates.SwitchOffState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple implementation of the CoffeeMaker interface.
 */
public class WnckCoffeeMaker implements CoffeeMaker {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(WnckCoffeeMaker.class);
  private MakerState makerState;
  private final List<CoffeeMakerListener> listeners;
  private final String location;
  private final String model;
  private final int id;
  private int cupsSinceLastService;

  /**
   * Constructor to create a WnckCoffeeMaker based on the information in the given
   * CoffeeMakerDescriptor.
   *
   * @param configuration Descriptor with basic facts about the coffee maker to be created
   */
  public WnckCoffeeMaker(CoffeeMakerConfiguration configuration) {
    logger.debug("Constructor - {}", configuration);
    listeners = new ArrayList<>();
    this.location = configuration.location();
    this.model = configuration.model();
    this.id = configuration.id();
    this.cupsSinceLastService = configuration.cups();
    makerState = new SwitchOffState(this);
  }

  @Override
  public void switchOn() throws IllegalStateException {
    makerState.onSwitchOn();
  }

  @Override
  public void switchOff() throws IllegalStateException {
    makerState.onSwitchOff();
  }

  @Override
  public void brew(final Recipe recipe) throws IllegalParameterException, IllegalStateException {
    // simulate brewing ...
    makerState.onBrew();
    cupsSinceLastService++;
    notifyListeners((l) -> l.newCupsCounter(cupsSinceLastService));
  }

  @Override
  public void cleanIt() throws IllegalStateException {
    makerState.onCleanIt();
  }

  @Override
  public void addCallback(final CoffeeMakerListener listener) throws IllegalParameterException {
    if (listener == null) {
      throw new IllegalParameterException("Listener was null reference.");
    }

    if (listeners.contains(listener)) {
      throw new IllegalParameterException("Listener already registered.");
    }

    listeners.add(listener);
  }

  @Override
  public void removeCallback(final CoffeeMakerListener listener) throws IllegalParameterException {
    if (listener == null) {
      throw new IllegalParameterException("Listener was null reference.");
    }

    if (!listeners.contains(listener)) {
      throw new IllegalParameterException("Listener is not registered:" + listener);
    }

    listeners.remove(listener);
  }

  @Override
  public CoffeeMakerConfiguration getConfiguration() {
    return new CoffeeMakerConfiguration(location, model, id, cupsSinceLastService);
  }

  public MakerState getMakerState() {
    return makerState;
  }

  /**
   * sets the state of the CoffeeMaker and notifies all listeners.
   *
   * @param makerState new maker state
   */
  public void setMakerState(final MakerState makerState) {
    logger.debug("setMakerState - {}", makerState);
    this.makerState = makerState;
    notifyListeners((CoffeeMakerListener l) -> l.newState(makerState.getState()));
  }

  private void notifyListeners(final Consumer<CoffeeMakerListener> consumer) {
    listeners.forEach(consumer);
  }

  public String getLocation() {
    return location;
  }

  public String getModel() {
    return model;
  }

  public int getId() {
    return id;
  }

  @Override
  public State getState() {
    return getMakerState().getState();
  }

  public int getCupsSinceLastService() {
    return cupsSinceLastService;
  }
}
