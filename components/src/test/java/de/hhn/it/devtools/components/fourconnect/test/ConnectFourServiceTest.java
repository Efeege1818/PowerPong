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
 * Test class for the core functionality of the {@link ConnectFourService}.
 * <p>
 * This class focuses on testing the service's initialization logic, proper
 * exception handling for invalid operations or parameters, and the basic
 * chip dropping and player alternation mechanics.
 * </p>
 */
public class ConnectFourServiceTest {

  private ConnectFourService service;
  private GameConfiguration config;
  private Player player1; // Used for comparison
  private Player player2; // Used for comparison

  /**
   * Sets up the test environment before each test method runs.
   * Initializes a new {@link ConnectFourServiceImpl}, a default
   * {@link GameConfiguration}, and reference {@link Player} objects for
   * comparison against the service's internal players.
   */
  @BeforeEach
  void setUp() {
    service = new ConnectFourServiceImpl();
    // The configuration is intentionally simple as the service implementation defines the board size (6x7).
    config = new GameConfiguration(3, 3);
    player1 = new Player("Player Red", PlayerColor.RED);
    player2 = new Player("Player Yellow", PlayerColor.YELLOW);
  }

  /**
   * Tests that {@link ConnectFourService#startGame(GameConfiguration)} correctly
   * initializes the board, sets the game to active, and correctly identifies
   * the starting player (Player Red).
   *
   * @throws OperationNotSupportedException If {@link ConnectFourService#getCurrentPlayer()} fails unexpectedly.
   */
  @Test
  void testStartGame_InitializesBoardAndPlayer() throws OperationNotSupportedException {
    service.startGame(config);
    Player actualPlayer = service.getCurrentPlayer();

    assertDoesNotThrow(() -> service.dropChip(0),
        "Game must be active after startGame() to allow drops.");
    assertEquals(GameBoardImpl.ROWS, service.getBoard().getRows(), "Board must have 6 rows.");
    assertEquals(GameBoardImpl.COLUMNS, service.getBoard().getColumns(), "Board must have 7 columns.");

    // Check the values of the returned starting Player (Player 1 / Red)
    assertEquals(player1.name(), actualPlayer.name(), "Player 1 name must be correct.");
    assertEquals(player1.color(), actualPlayer.color(), "Player 1 color must be correct.");
  }

  /**
   * Tests that {@link ConnectFourService#dropChip(int)} throws an
   * {@link OperationNotSupportedException} if {@link ConnectFourService#startGame(GameConfiguration)}
   * has not been called, indicating the game is not active.
   */
  @Test
  void testDropChip_ThrowsExceptionWhenGameNotStarted() {
    assertThrows(OperationNotSupportedException.class, () -> service.dropChip(0),
        "dropChip must throw OperationNotSupportedException if startGame() was not called.");
  }

  /**
   * Tests that {@link ConnectFourService#dropChip(int)} throws an
   * {@link IllegalParameterException} for column indices that are outside the valid range (0 to 6).
   */
  @Test
  void testDropChip_ThrowsExceptionForInvalidColumn() {
    service.startGame(config);
    assertThrows(IllegalParameterException.class, () -> service.dropChip(7),
        "Column 7 (out of bounds) must throw an IllegalParameterException.");
    assertThrows(IllegalParameterException.class, () -> service.dropChip(-1),
        "Column -1 (out of bounds) must throw an IllegalParameterException.");
  }

  /**
   * Tests the core drop functionality:
   * <ol>
   * <li>The first chip lands at the lowest row (index 5) in the specified column (3).</li>
   * <li>The field owner is correctly set to Player 1 (Red).</li>
   * <li>The current player correctly alternates to Player 2 (Yellow).</li>
   * <li>The second chip lands one row above (index 4) in the same column (3).</li>
   * <li>The field owner is correctly set to Player 2 (Yellow).</li>
   * </ol>
   *
   * @throws Exception if any drop operation throws an unexpected exception.
   */
  @Test
  void testDropChip_AlternatesPlayerAndPlacesChipAtBottom() throws Exception {
    service.startGame(config);

    // Drop 1: Player 1 (Red)
    int row1 = service.dropChip(3);
    Player actualPlayer1 = service.getBoard().getField(row1, 3).getOccupyingPlayer();

    assertEquals(GameBoardImpl.ROWS - 1, row1,
        "The first chip must land on the lowest row (Row 5).");
    assertEquals(player1.name(), actualPlayer1.name(), "Field owner name must be Player 1.");
    assertEquals(player1.color(), actualPlayer1.color(), "Field owner color must be Player 1.");

    // Check if the player switched to Player 2
    Player expectedPlayer2 = service.getCurrentPlayer();
    assertNotEquals(player1.name(), expectedPlayer2.name(),
        "Player must switch after a valid drop.");

    // Drop 2: Player 2 (Yellow)
    int row2 = service.dropChip(3);
    Player actualPlayer2 = service.getBoard().getField(row2, 3).getOccupyingPlayer();

    assertEquals(GameBoardImpl.ROWS - 2, row2,
        "The second chip must land one row above (Row 4).");
    assertEquals(player2.name(), actualPlayer2.name(), "Field owner name must be Player 2.");
    assertEquals(player2.color(), actualPlayer2.color(), "Field owner color must be Player 2.");
  }
}