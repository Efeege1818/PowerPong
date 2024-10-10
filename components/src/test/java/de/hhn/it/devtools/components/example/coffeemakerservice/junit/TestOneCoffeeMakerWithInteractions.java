package de.hhn.it.devtools.components.example.coffeemakerservice.junit;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Quantity;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.State;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.makerstates.CleaningState;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.makerstates.HeatingState;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("When a maker ...")
public class TestOneCoffeeMakerWithInteractions {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(TestOneCoffeeMakerWithInteractions.class);
  public static final int DELTA = 500;

  int makerId;
  private CoffeeMakerService coffeeMakerService;
  private Recipe espresso;

  @BeforeEach
  void setup() throws IllegalParameterException {
    WnckCoffeeMakerService wnckCoffeeMakerService = new WnckCoffeeMakerService();
    AdminCoffeeMakerService adminCoffeeMakerService = wnckCoffeeMakerService;
    coffeeMakerService = wnckCoffeeMakerService;
    CoffeeMakerConfiguration configuration = new CoffeeMakerConfiguration("A106", "Senseo muddy brown");
    adminCoffeeMakerService.addCoffeeMaker(configuration);
    List<CoffeeMakerConfiguration> makers = coffeeMakerService.getCoffeeMakers();
    makerId = makers.get(0).id();
    espresso = new Recipe(Quantity.LARGE, Quantity.NONE, Quantity.NONE, Quantity.NONE);
  }

  @Test
  @DisplayName("is created, it is in state OFF")
  void checkStateOfCreatedMaker() throws IllegalParameterException {
    assertEquals(State.OFF, coffeeMakerService.getState(makerId));
  }

  @Test
  @DisplayName("is switched on, it changes in state heating and then in state ready")
  void checkStatesHeadingAndReadyAfterSwitchedOn() throws IllegalParameterException,
          InterruptedException {
    coffeeMakerService.switchOn(makerId);
    State assumeHeating = coffeeMakerService.getState(makerId);
    Thread.sleep(HeatingState.HEATING_TIME_MILLIS + DELTA);
    State assumeReady = coffeeMakerService.getState(makerId);
    assertAll(
            () -> assertEquals(State.HEATING, assumeHeating),
            () -> assertEquals(State.READY, assumeReady)
    );
  }

  @Test@DisplayName("is in state READY and we cleanIt, it changes in state cleaning unad then in " +
          "state ready")
  void checkStatesCleaningAndReadyAfterCleanIt() throws IllegalParameterException,
          InterruptedException {
    coffeeMakerService.switchOn(makerId);
    Thread.sleep(HeatingState.HEATING_TIME_MILLIS + DELTA);
    coffeeMakerService.cleanIt(makerId);
    State assumeCleaning = coffeeMakerService.getState(makerId);
    Thread.sleep(CleaningState.CLEANING_TIME_MILLIS + DELTA);
    State assumeReady = coffeeMakerService.getState(makerId);
    assertAll(
            () -> assertEquals(State.CLEANING, assumeCleaning),
            () -> assertEquals(State.READY, assumeReady)
    );
  }

  @Test
  @DisplayName("is in state READY and we call brewing and immediately cleaning, we get an " +
                      "IllegalStateException")
    void checkRaiseOfExceptionWhenCleaningWhileBrewing() throws IllegalParameterException,
          InterruptedException {
    coffeeMakerService.switchOn(makerId);
    Thread.sleep(HeatingState.HEATING_TIME_MILLIS + DELTA);
    coffeeMakerService.brewCoffee(makerId, espresso);
    IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> coffeeMakerService.cleanIt(makerId));
    assertEquals(State.BREWING, coffeeMakerService.getState(makerId));
    }
}
