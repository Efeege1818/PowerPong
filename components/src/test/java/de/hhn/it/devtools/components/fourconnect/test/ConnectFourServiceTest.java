package de.hhn.it.devtools.components.fourconnect.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    Player actualPlayer = service.getCurrentPlayer();

    assertDoesNotThrow(() -> service.dropChip(0),
        "Game must be active after startGame() to allow drops.");
    assertEquals(GameBoardImpl.ROWS, service.getBoard().getRows(), "Board must have 6 rows.");
    assertEquals(GameBoardImpl.COLUMNS, service.getBoard().getColumns(), "Board must have 7 columns.");

    // Prüfe die Werte des zurückgegebenen Player-Records
    assertEquals(player1.name(), actualPlayer.name(), "Player 1 name must be correct.");
    assertEquals(player1.color(), actualPlayer.color(), "Player 1 color must be correct.");
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
  @Test
  void testDropChip_AlternatesPlayerAndPlacesChipAtBottom() throws Exception {

    service.startGame(config);

    int row1 = service.dropChip(3);

    Player actualPlayer1 = service.getBoard().getField(row1, 3).getOccupyingPlayer();

    assertEquals(GameBoardImpl.ROWS - 1, row1,
        "The first chip must land on the lowest row (Row 5).");
    assertEquals(player1.name(), actualPlayer1.name(), "Field owner name must be Player 1.");
    assertEquals(player1.color(), actualPlayer1.color(), "Field owner color must be Player 1.");

    // Prüfen ob der Spieler gewechselt hat
    Player expectedPlayer2 = service.getCurrentPlayer();
    assertNotEquals(player1.name(), expectedPlayer2.name(),
        "Player must switch after a valid drop.");
    int row2 = service.dropChip(3);
    Player actualPlayer2 = service.getBoard().getField(row2, 3).getOccupyingPlayer();
    assertEquals(GameBoardImpl.ROWS - 2, row2,
        "The second chip must land one row above (Row 4).");
    assertEquals(player2.name(), actualPlayer2.name(), "Field owner name must be Player 2.");
    assertEquals(player2.color(), actualPlayer2.color(), "Field owner color must be Player 2.");
  }
}
