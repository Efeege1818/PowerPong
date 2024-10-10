package de.hhn.it.devtools.javafx.coffeemaker.viewmodel;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerListener;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Quantity;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.State;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View model for a single CoffeeMaker. Can be registered as
 * CoffeeMakerListener at the CoffeeMakerService.
 */
public class CoffeeMakerViewModel implements CoffeeMakerListener {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(CoffeeMakerViewModel.class);

  private final CoffeeMakerConfiguration configuration;
  private final CoffeeMakerService service;

  private final StringProperty location;
  private final StringProperty model;
  private final IntegerProperty cups;
  private final ObjectProperty<State> state;
  private final ObjectProperty<Recipe> recipe;
  private final BooleanProperty onButtonDisabled;
  private final BooleanProperty offButtonDisabled;
  private final BooleanProperty cleanButtonDisabled;
  private final BooleanProperty brewButtonDisabled;
  private final BooleanProperty recipeConfiguratorDisabled;

  /**
   * Constructor.
   *
   * @param configuration Configuration of the CoffeeMaker to handle
   * @param service CoffeeMakerService object to register with as a listener
   */
  public CoffeeMakerViewModel(final CoffeeMakerConfiguration configuration,
                              final CoffeeMakerService service) {
    this.configuration = configuration;
    this.service = service;

    location = new SimpleStringProperty(configuration.location());
    model = new SimpleStringProperty(configuration.model());
    // TODO: get the real number of cups
    cups = new SimpleIntegerProperty(configuration.cups());
    state = new SimpleObjectProperty<>(State.OFF);
    recipe = new SimpleObjectProperty<>(
        new Recipe(Quantity.NONE, Quantity.NONE, Quantity.NONE, Quantity.NONE));
    // Initialize buttons for a coffee machine which is in State OFF.
    onButtonDisabled = new SimpleBooleanProperty(false);
    offButtonDisabled = new SimpleBooleanProperty(true);
    cleanButtonDisabled = new SimpleBooleanProperty(true);
    brewButtonDisabled = new SimpleBooleanProperty(true);
    recipeConfiguratorDisabled = new SimpleBooleanProperty(true);

    // register as a callback for the coffee maker infos
    try {
      service.addCallback(configuration.id(), this);
    } catch (IllegalParameterException e) {
      throw new RuntimeException(e);
    }
  }

  public StringProperty locationProperty() {
    return location;
  }

  public StringProperty modelProperty() {
    return model;
  }

  public IntegerProperty cupsProperty() {
    return cups;
  }

  public ObjectProperty<State> stateProperty() {
    return state;
  }

  public ObjectProperty<Recipe> recipeProperty() {
    return recipe;
  }

  public BooleanProperty onButtonDisabledProperty() {
    return onButtonDisabled;
  }

  public BooleanProperty offButtonDisabledProperty() {
    return offButtonDisabled;
  }

  public BooleanProperty cleanButtonDisabledProperty() {
    return cleanButtonDisabled;
  }

  public BooleanProperty brewButtonDisabledProperty() {
    return brewButtonDisabled;
  }

  public BooleanProperty recipeConfigurationDisabledProperty() {
    return recipeConfiguratorDisabled;
  }

  @Override
  public void newState(final State newState) {
    Platform.runLater(() -> setState(newState));
  }

  @Override
  public void newCupsCounter(final int value) {
    Platform.runLater(() -> cups.set(value));
  }

  private void setState(State newState) {
    state.set(newState);

    switch (newState) {
      case OFF:
        onButtonDisabled.set(false);
        offButtonDisabled.set(true);
        cleanButtonDisabled.set(true);
        brewButtonDisabled.set(true);
        recipeConfiguratorDisabled.set(true);
        break;
      case READY:
        onButtonDisabled.set(true);
        offButtonDisabled.set(false);
        cleanButtonDisabled.set(false);
        brewButtonDisabled.set(false);
        recipeConfiguratorDisabled.set(false);
        break;
      case BREWING, ERROR, CLEANING, HEATING:
        onButtonDisabled.set(true);
        offButtonDisabled.set(true);
        cleanButtonDisabled.set(true);
        brewButtonDisabled.set(true);
        recipeConfiguratorDisabled.set(true);
        break;
      default:
        logger.error("Illegal state: " + newState);
    }
  }

  public void switchOn() throws IllegalParameterException {
    service.switchOn(configuration.id());
  }


  public void switchOff() throws IllegalParameterException {
    service.switchOff(configuration.id());
  }

  public void cleanIt() throws IllegalParameterException {
    service.cleanIt(configuration.id());
  }

  public void brewCoffee() throws IllegalParameterException {
    service.brewCoffee(configuration.id(), recipe.get());
  }
}
