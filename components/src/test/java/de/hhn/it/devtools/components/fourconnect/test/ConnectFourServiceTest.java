package de.hhn.it.devtools.components.fourconnect.test;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;
import de.hhn.it.devtools.components.fourconnect.provider.GameBoardImpl;
import de.hhn.it.devtools.components.fourconnect.provider.FieldImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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


  @Test
  void testDiagonalWinLogicUpRight() throws IllegalParameterException, OperationNotSupportedException {
    service.startGame(null);
    service.dropChip(0);


    service.dropChip(1);
    service.dropChip(1);


    service.dropChip(2);
    service.dropChip(6);
    service.dropChip(2);
    service.dropChip(2);


    service.dropChip(3);
    service.dropChip(6);
    service.dropChip(3);
    service.dropChip(6);
    service.dropChip(3);


    assertFalse(((ConnectFourServiceImpl) service).checkForWin());

    service.dropChip(3); // R (3,3) - TARGET


    assertTrue(((ConnectFourServiceImpl) service).checkForWin(), "Diagonal (/) win condition should return true");
  }

  @Test
  void testDiagonalWinLogicUpLeft() throws IllegalParameterException, OperationNotSupportedException {
    service.startGame(null);
    service.dropChip(3);


    service.dropChip(2);
    service.dropChip(2);


    service.dropChip(1);
    service.dropChip(6);
    service.dropChip(1);
    service.dropChip(1);


    service.dropChip(0);
    service.dropChip(6);
    service.dropChip(0);
    service.dropChip(6);
    service.dropChip(0);


    assertFalse(((ConnectFourServiceImpl) service).checkForWin());

    service.dropChip(0); // R (3,0) - TARGET


    assertTrue(((ConnectFourServiceImpl) service).checkForWin(), "Reverse Diagonal (\\) win condition should return true");
  }

  @Test
  void testHorizontalWinLogic() throws IllegalParameterException, OperationNotSupportedException {
    service.startGame(null);


    service.dropChip(0);
    service.dropChip(0);

    service.dropChip(1);
    service.dropChip(1);

    service.dropChip(2);
    service.dropChip(2);

    assertFalse(((ConnectFourServiceImpl) service).checkForWin());

    service.dropChip(3);

    assertTrue(((ConnectFourServiceImpl) service).checkForWin(), "Horizontal win condition should return true");
  }

  @Test
  void testVerticalWinLogic() throws IllegalParameterException, OperationNotSupportedException {
    service.startGame(null);


    service.dropChip(0);
    service.dropChip(1);
    service.dropChip(0);
    service.dropChip(1);
    service.dropChip(0);
    service.dropChip(1);

    assertFalse(((ConnectFourServiceImpl) service).checkForWin());

    service.dropChip(0);

    assertTrue(((ConnectFourServiceImpl) service).checkForWin(), "Vertical win condition should return true");
  }

  @Test
  void testCheckForDraw_FalseWhenNotFull() throws OperationNotSupportedException, IllegalParameterException {
    service.startGame(null);

    // Drop just a few chips
    service.dropChip(0);
    service.dropChip(1);

    // There is still empty space on the board, should return false
    assertFalse(((ConnectFourServiceImpl) service).checkForDraw(), "Draw should not return true before the board is full.");
  }

  void testCheckForDraw_TrueWhenFull() throws OperationNotSupportedException, IllegalParameterException {
    service.startGame(null);

    // We need to fill the board (6x7 = 42 cells) completely.
    // It might be hard to fill without creating a 4-in-a-row (win),
    // but since checkForDraw only checks for "null",
    // even if the win check activates (as long as it doesn't throw an exception),
    // we can fill the board and test the result of this method.

    int rows = 6;
    int cols = 7;

    service.dropChip(0);
    service.dropChip(1);
    service.dropChip(0);
    service.dropChip(2);
    service.dropChip(3);
    service.dropChip(0);
    service.dropChip(1);
    service.dropChip(1);
    service.dropChip(4);
    service.dropChip(5);
    service.dropChip(6);
    service.dropChip(2);
    service.dropChip(0);
    service.dropChip(0);
    service.dropChip(0);
    service.dropChip(1);
    service.dropChip(3);
    service.dropChip(2);
    service.dropChip(3);
    service.dropChip(3);
    service.dropChip(2);
    service.dropChip(1);
    service.dropChip(2);
    service.dropChip(2);
    service.dropChip(1);
    service.dropChip(3);
    service.dropChip(3);
    service.dropChip(6);
    service.dropChip(6);
    service.dropChip(4);
    service.dropChip(5);
    service.dropChip(4);
    service.dropChip(5);
    service.dropChip(4);
    service.dropChip(4);
    service.dropChip(4);
    service.dropChip(6);
    service.dropChip(5);
    service.dropChip(5);
    service.dropChip(6);
    service.dropChip(5);
    service.dropChip(6);

    // Board is completely full, should return true
    assertTrue(((ConnectFourServiceImpl) service).checkForDraw(), "Should return true when the board is completely full.");
  }




  /**
   * Tests that applying toxic decay simply reduces the counter
   * when the counter is greater than 1.
   */
  @Test
  void testToxicDecay_DecrementsCounterOnly() throws OperationNotSupportedException, IllegalParameterException {
    service.startGame(config);

    // 1. Setup: Drop a chip at col 0 (Row 5)
    int row = service.dropChip(0);

    // 2. Access internal structures to simulate a Toxic Zone
    GameBoardImpl board = (GameBoardImpl) service.getBoard();
    FieldImpl targetField = (FieldImpl) board.getField(row, 0);

    // Manually flag as toxic and set time to 3
    targetField.setToxicZone(true);
    targetField.setDecayTime(3);

    // 3. Act: Apply decay
    ((ConnectFourServiceImpl) service).applyToxicDecay();

    // 4. Assert
    assertEquals(2, targetField.getDecayTime(), "Decay time should decrease by 1.");
    assertNotNull(targetField.getOccupyingPlayer(), "Player should still be on the field.");
  }

  /**
   * Tests the "Meltdown" scenario:
   * 1. Chip A is at the bottom (Toxic, Decay=1).
   * 2. Chip B is directly above it.
   * 3. applyToxicDecay is called.
   * * Expectation: Chip A disappears, Chip B falls into Chip A's spot,
   * and the toxic timer resets to 3.
   */
  @Test
  void testToxicDecay_ExplosionAndGravity() throws Exception {
    service.startGame(config);

    // 1. Setup Board State: Stack 2 chips in Column 0
    // Drop 1 (Red) - Will be at Row 5 (Bottom)
    service.dropChip(0);
    // Drop 2 (Yellow) - Will be at Row 4 (Above)
    service.dropChip(0);

    // 2. Access internal structures
    GameBoardImpl board = (GameBoardImpl) service.getBoard();
    FieldImpl bottomField = (FieldImpl) board.getField(5, 0); // The toxic trap
    FieldImpl aboveField  = (FieldImpl) board.getField(4, 0); // The victim falling down

    Player playerYellow = aboveField.getOccupyingPlayer();

    // Set bottom field to Toxic with 1 turn left
    bottomField.setToxicZone(true);
    bottomField.setDecayTime(1);

    // 3. Act: Trigger the decay (1 -> 0 -> Explosion)
    ((ConnectFourServiceImpl) service).applyToxicDecay();

    // 4. Assertions

    // Check Gravity: The bottom field should now hold the Yellow player (who fell down)
    assertEquals(playerYellow, bottomField.getOccupyingPlayer(),
            "The chip from above should have fallen into the toxic zone.");

    // Check Clearance: The field above should now be empty (or null)
    assertNull(aboveField.getOccupyingPlayer(),
            "The field above should be empty after its chip fell down.");

    // Check Reset: Since a new player occupies the toxic zone, timer should reset to 3
    assertEquals(3, bottomField.getDecayTime(),
            "Toxic timer should reset to 3 when a new chip falls in.");
  }

  /**
   * Tests a complex gravity chain.
   * If the bottom chip dissolves, the ENTIRE stack above it must shift down.
   */
  @Test
  void testToxicDecay_ChainGravity() throws Exception {
    service.startGame(config);

    // Stack 3 chips in Column 1
    service.dropChip(1); // Row 5 (Red) - TOXIC
    service.dropChip(1); // Row 4 (Yellow)
    service.dropChip(1); // Row 3 (Red)

    GameBoardImpl board = (GameBoardImpl) service.getBoard();
    FieldImpl bottomField = (FieldImpl) board.getField(5, 1);

    // Prepare the trap
    bottomField.setToxicZone(true);
    bottomField.setDecayTime(1);

    // Act
    ((ConnectFourServiceImpl) service).applyToxicDecay();

    // Assert positions
    // Row 5 should now be Yellow
    assertEquals(PlayerColor.YELLOW, board.getField(5, 1).getOccupyingPlayer().color());
    // Row 4 should now be Red
    assertEquals(PlayerColor.RED, board.getField(4, 1).getOccupyingPlayer().color());
    // Row 3 should be empty
    assertNull(board.getField(3, 1).getOccupyingPlayer());
  }

  /**
   * Tests that applyToxicDecay ignores non-toxic fields even if occupied.
   */
  @Test
  void testToxicDecay_IgnoredOnSafeFields() throws Exception {
    service.startGame(config);
    int row = service.dropChip(2);

    GameBoardImpl board = (GameBoardImpl) service.getBoard();
    FieldImpl field = (FieldImpl) board.getField(row, 2);

    // Ensure it's NOT toxic
    field.setToxicZone(false);
    // Set decay time to something just in case (shouldn't matter)
    field.setDecayTime(1);

    ((ConnectFourServiceImpl) service).applyToxicDecay();

    // Should remain unchanged
    assertEquals(1, field.getDecayTime(), "Decay time should not change on non-toxic fields.");
    assertNotNull(field.getOccupyingPlayer(), "Chip should not be removed.");
  }

  @Test
  void testDropChipPlacesPlayerInCorrectField() throws OperationNotSupportedException, IllegalParameterException {
    service.startGame(null);

    ConnectFourServiceImpl svc = (ConnectFourServiceImpl) service;
    GameBoardImpl board = (GameBoardImpl) svc.getBoard();

    Player current = svc.getCurrentPlayer(); // player1

    int column = 3;
    int row = svc.dropChip(column);


    assertEquals(current, board.getField(row, column).getOccupyingPlayer(),
            "Dropped chip should belong to currentPlayer");
  }


  @Test
  void testDropChipColumnOutOfBoundsThrowsException() {
    ConnectFourServiceImpl svc = (ConnectFourServiceImpl) service;
    svc.startGame(null);

    assertThrows(IllegalParameterException.class, () -> svc.dropChip(-1));
    assertThrows(IllegalParameterException.class, () -> svc.dropChip(GameBoardImpl.COLUMNS));
  }

  /**
   * Test Case: Verifies that calling applyToxicDecay before the game has started
   * (gameActive = false) throws an OperationNotSupportedException.
   */
  @Test
  void testToxicDecay_ThrowsExceptionWhenGameNotActive() {
    // We do NOT call service.startGame(config) explicitly here.

    assertThrows(OperationNotSupportedException.class, () ->
                    ((ConnectFourServiceImpl) service).applyToxicDecay(),
            "applyToxicDecay must throw an exception when the game is not active.");
  }

  /**
   * Test Case: When a chip in a toxic zone dissolves, if there is no other chip above it
   * (meaning the column becomes empty):
   * 1. The field must become completely empty (null).
   * 2. DecayTime must be set to 0.
   * (This tests the 'else { currentField.setDecayTime(0); }' block at the end of the code).
   */
  @Test
  void testToxicDecay_ResetsTimerToZeroWhenNoChipFallsIn() throws Exception {
    service.startGame(config);

    // 1. Scenario: Drop only 1 chip into Column 0.
    service.dropChip(0); // Row 5 (Bottom) is occupied, Row 4 (Above) is empty.

    GameBoardImpl board = (GameBoardImpl) service.getBoard();
    FieldImpl bottomField = (FieldImpl) board.getField(5, 0);

    // 2. Manually flag as toxic and set the timer to 1.
    bottomField.setToxicZone(true);
    bottomField.setDecayTime(1);

    // 3. Act: Trigger the decay.
    ((ConnectFourServiceImpl) service).applyToxicDecay();

    // 4. Assert
    assertNull(bottomField.getOccupyingPlayer(),
            "The field must be empty (null) after decay since no chip fell from above.");

    assertEquals(0, bottomField.getDecayTime(),
            "The decay timer must reset to 0 because the field is now empty.");
  }

  // -------------------------------------------------------------------------
  // DIRECT HELPER METHOD TESTS (WHITE BOX TESTING)
  // -------------------------------------------------------------------------

  @Test
  void testCheckHorizontal_OnlyDetectsHorizontalWin() throws Exception {
    service.startGame(null);

    // We line up 4 chips side-by-side (turn alternation doesn't matter here, the board state does).
    // Instead of manipulating the board manually, we use dropChip but
    // we strictly create a horizontal condition.

    // To create R(0,0), R(0,1), R(0,2), R(0,3), we could sprinkle Yellows in between
    // so they don't stack. However, the easiest way is to line them up on the bottom row.

    service.dropChip(0); // R
    service.dropChip(0); // Y (Goes above)
    service.dropChip(1); // R
    service.dropChip(1); // Y
    service.dropChip(2); // R
    service.dropChip(2); // Y
    service.dropChip(3); // R -> HORIZONTAL WIN (Row 5: R, R, R, R)

    // Access the method directly via casting
    ConnectFourServiceImpl impl = (ConnectFourServiceImpl) service;

    assertTrue(impl.checkHorizontal(), "checkHorizontal() must detect the horizontal win.");

    // Ensure diagonal or vertical methods return FALSE (Isolation test)
    assertFalse(impl.checkVertical(), "checkVertical() must not return true when there is a horizontal win.");
    assertFalse(impl.checkDiagonal(), "checkDiagonal() must not return true when there is a horizontal win.");
  }

  @Test
  void testCheckVertical_OnlyDetectsVerticalWin() throws Exception {
    service.startGame(null);

    // Vertical setup: We need to stack 4 Red chips in Column 0.
    // Yellow must not interrupt. That is why creating 4 same-colored chips
    // vertically using dropChip (with alternating turns) is tricky.
    // HOWEVER: checkVertical() only checks "are there 4 same-colored chips in a row".

    // Simulation:
    service.dropChip(0); // R
    service.dropChip(1); // Y (Throw into another column)
    service.dropChip(0); // R
    service.dropChip(1); // Y
    service.dropChip(0); // R
    service.dropChip(1); // Y
    service.dropChip(0); // R -> VERTICAL WIN (Col 0: R, R, R, R)

    ConnectFourServiceImpl impl = (ConnectFourServiceImpl) service;

    assertTrue(impl.checkVertical(), "checkVertical() must detect the vertical win.");

    // Verify that others do not work
    assertFalse(impl.checkHorizontal(), "checkHorizontal() must not work when there is a vertical win.");
  }

  @Test
  void testCheckDiagonal_UpRight_OnlyDropChip() throws Exception {
    service.startGame(null);
    ConnectFourServiceImpl impl = (ConnectFourServiceImpl) service;

    // TARGET: Place RED chips at positions (5,0), (4,1), (3,2), (2,3).
    // To do this, we need to fill underneath them and align the turn order.

    // --- COLUMN 0 (Target is at the bottom) ---
    service.dropChip(0); // RED (5,0) -> TARGET 1

    // --- COLUMN 1 (One below is full, top is target) ---
    service.dropChip(1); // YELLOW (Filler)
    service.dropChip(1); // RED (4,1) -> TARGET 2

    // --- COLUMN 2 (Two below are full, top is target) ---
    service.dropChip(2); // YELLOW (Filler)
    service.dropChip(2); // RED (Filler)
    service.dropChip(6); // YELLOW (WASTE MOVE - Distant column to switch turn back to Red)
    service.dropChip(2); // RED (3,2) -> TARGET 3

    // --- COLUMN 3 (Three below are full, top is target) ---
    service.dropChip(3); // YELLOW (Filler)
    service.dropChip(3); // RED (Filler)
    service.dropChip(3); // YELLOW (Filler)
    service.dropChip(3); // RED (2,3) -> TARGET 4

    // NOW CHECK
    assertTrue(impl.checkDiagonal(), "Up-right (/) diagonal win must be detected.");

    // Ensure others are false (Isolation test)
    assertFalse(impl.checkHorizontal(), "Horizontal win should not be detected during diagonal win.");
    assertFalse(impl.checkVertical(), "Vertical win should not be detected during diagonal win.");
  }

  @Test
  void testCheckDiagonal_UpLeft_OnlyDropChip() throws Exception {
    service.startGame(null);
    ConnectFourServiceImpl impl = (ConnectFourServiceImpl) service;

    // TARGET: Place RED chips at positions (2,0), (3,1), (4,2), (5,3).
    // Since it is a reverse diagonal, the left side needs to be high, right side low.

    // --- COLUMN 3 (Target is at the bottom) ---
    service.dropChip(3); // RED (5,3) -> TARGET 1

    // --- COLUMN 2 (One below is full, top is target) ---
    service.dropChip(2); // YELLOW (Filler)
    service.dropChip(2); // RED (4,2) -> TARGET 2

    // --- COLUMN 1 (Two below are full, top is target) ---
    service.dropChip(1); // YELLOW (Filler)
    service.dropChip(1); // RED (Filler)
    service.dropChip(6); // YELLOW (WASTE MOVE - Adjust turn)
    service.dropChip(1); // RED (3,1) -> TARGET 3

    // --- COLUMN 0 (Three below are full, top is target) ---
    service.dropChip(0); // YELLOW (Filler)
    service.dropChip(0); // RED (Filler)
    service.dropChip(0); // YELLOW (Filler)
    service.dropChip(0); // RED (2,0) -> TARGET 4

    // NOW CHECK
    assertTrue(impl.checkDiagonal(), "Up-left (\\) diagonal win must be detected.");

    // Ensure others are false
    assertFalse(impl.checkHorizontal());
    assertFalse(impl.checkVertical());
  }
}