package de.hhn.it.devtools.components.example.coffeemakerservice.junit;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Quantity;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.State;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMaker;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.makerstates.HeatingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("While in Heating State, ...")
public class TestHeatingState {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(TestHeatingState.class);
  private WnckCoffeeMaker maker;
  private Recipe espresso;

  @BeforeEach
  void setup() {
    CoffeeMakerConfiguration configuration = new CoffeeMakerConfiguration("A106", "Senseo muddy brown");
    maker = new WnckCoffeeMaker(configuration);
    maker.setMakerState(new HeatingState(maker));
    espresso = new Recipe(Quantity.LARGE, Quantity.NONE, Quantity.NONE, Quantity.NONE);
  }

  @Test
  @DisplayName("onCleaning results in an IllegalStateException")
  void checkExceptionThrownWhenOnCleaningCalled() {
    IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> maker.cleanIt());
  }

  @Test
  @DisplayName("onBrewing results in an IllegalStateException")
  void checkExceptionThrownWhenOnBrewingCalled() {
    IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> maker.brew(espresso));
  }

  @Test
  @DisplayName("onSwitchOn results in an IllegalStateException")
  void checkExceptionThrownWhenOnSwitchOnCalled() {
    IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> maker.switchOn());
  }

  @Test
  @DisplayName("switchOff results in a new state OFF")
  void checkStateWhenOnSwitchOffCalled() {
    maker.switchOff();
    assertEquals(State.OFF, maker.getMakerState().getState());
  }
}
