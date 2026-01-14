package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.Direction;
import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.apis.spaceinvaders.GameConfiguration;
import de.hhn.it.devtools.apis.spaceinvaders.Difficulty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiBadCasesTest {

  @Test
  void testCoordinateEqualsWithNullAndDifferentType() {
    Coordinate a = new Coordinate(0, 0);
    assertFalse(a.equals(null), "equals(null) muss false liefern");
    assertFalse(a.equals("not a coordinate"), "equals mit anderem Typ muss false liefern");
  }

  @Test
  void testEnumValueOfNullThrowsNpe() {
    assertThrows(NullPointerException.class, () -> Direction.valueOf(null));
  }

  @Test
  void testEnumValueOfInvalidNameThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> GameState.valueOf("INVALID_STATE_NAME"));
  }

  @Test
  void testGameConfigurationAllowsNegativeAndNullDifficulty() {
    // API records currently haben keine Validierung -> diese "bad" Inputs werden akzeptiert
    GameConfiguration cfgNeg = new GameConfiguration(-1, Difficulty.EASY);
    assertEquals(-1, cfgNeg.numberOfBarriers());

    GameConfiguration cfgNull = new GameConfiguration(0, null);
    assertNull(cfgNull.difficulty(), "Wenn null übergeben wird, sollte difficulty() null zurückgeben (keine Validierung im Record)");
  }
}
