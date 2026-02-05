package de.hhn.it.devtools.components.spaceinvaders.junit;

import de.hhn.it.devtools.components.spaceinvaders.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

  @Test
  void testConstantsArePositiveWhereRequired() {
    assertTrue(Constants.THREAD_WAIT > 0);
    assertTrue(Constants.NUMBER_OF_ALIENS > 0);
    assertTrue(Constants.GRID_CELL_SIZE > 0);
    assertTrue(Constants.ALIEN_SHOOTING_CHANCE >= 0);
  }

  @Test
  void testNumberOfAliensReasonableValue() {
    assertTrue(Constants.NUMBER_OF_ALIENS <= 1000);
  }
}

