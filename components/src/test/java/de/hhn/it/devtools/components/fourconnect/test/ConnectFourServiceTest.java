package de.hhn.it.devtools.components.fourconnect.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;

import de.hhn.it.devtools.components.fourconnect.provider.GameBoardImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the ConnectFourService core functionality.
 * This class tests Initialization, Exceptions, and basic drop logic.
 */
public class ConnectFourServiceTest {

  private ConnectFourService service;
  private GameConfiguration config;
  private Player player1;
  private Player player2;

  @BeforeEach
  void setUp() {
    service = new ConnectFourServiceImpl();


    config = new GameConfiguration(3, 3);


    player1 = new Player("Player Red", PlayerColor.RED);
    player2 = new Player("Player Yellow", PlayerColor.YELLOW);
  }

  @Test
  void testStartGame_InitializesBoardAndPlayer() throws OperationNotSupportedException {
    service.startGame(config);
    assertDoesNotThrow(() -> service.dropChip(0),
        "Game must be active after startGame() to allow drops.");
    assertEquals(GameBoardImpl.ROWS, service.getBoard().getRows(), "Board must have 6 rows.");
    assertEquals(GameBoardImpl.COLUMNS, service.getBoard().getColumns(), "Board must have 7 columns.");
    assertEquals(player1, service.getCurrentPlayer(), "Player 1 must be the starting player.");
  }
  @Test
  void testDropChip_ThrowsExceptionWhenGameNotStarted() {
    assertThrows(OperationNotSupportedException.class, () -> service.dropChip(0),
        "dropChip must throw OperationNotSupportedException if startGame() was not called.");
  }

  @Test
  void testDropChip_ThrowsExceptionForInvalidColumn() {
    service.startGame(config);
    assertThrows(IllegalParameterException.class, () -> service.dropChip(7),
        "Column 7 must throw an IllegalParameterException.");
    assertThrows(IllegalParameterException.class, () -> service.dropChip(-1),
        "Column -1 must throw an IllegalParameterException.");
  }
}
