package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.GameListener;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Concrete implementation of the {@link ConnectFourService}.
 *
 * <p>This class manages the game lifecycle, chip placement, win/draw detection,
 * and toxic field decay/explosion behavior.
 */
public class ConnectFourServiceImpl implements ConnectFourService {

  private static final Logger LOGGER =
      Logger.getLogger(ConnectFourServiceImpl.class.getName());

  private final GameBoardImpl board;
  private final List<GameListener> listeners;

  private final Player player1;
  private final Player player2;

  private Player currentPlayer;
  private GameConfiguration configuration;
  private boolean gameActive;

  /**
   * Creates a new {@code ConnectFourServiceImpl} with a fresh board and two default players.
   */
  public ConnectFourServiceImpl() {
    this.board = new GameBoardImpl();
    this.listeners = new ArrayList<>();
    this.player1 = new Player("Player Red", PlayerColor.RED);
    this.player2 = new Player("Player Yellow", PlayerColor.YELLOW);
    this.gameActive = false;
  }

  /**
   * Starts a new game with the given configuration.
   *
   * <p>The board is cleared, up to 3 toxic fields are placed, and Player 1 (red) starts.
   *
   * @param configuration the game configuration
   */
  @Override
  public void startGame(GameConfiguration configuration) {
    this.configuration = configuration;

    board.clearBoard();

    int requested = configuration != null ? configuration.getToxicFieldCount() : 3;
    board.placeRandomToxicZones(Math.min(3, requested));

    currentPlayer = player1;
    gameActive = true;

    notifyBoardChanged();
    notifyTurnChanged();
  }

  /**
   * Drops a chip into the given column for the current player.
   *
   * <p>Note: Toxic decay is intentionally NOT executed here. It is triggered separately via
   * {@link #applyToxicDecay()} (this matches the component tests).
   *
   * @param column the column index (0-based)
   * @return the row index where the chip landed
   * @throws IllegalParameterException if the column is invalid
   * @throws OperationNotSupportedException if the game is not active
   */
  @Override
  public int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException {
    if (!gameActive) {
      throw new OperationNotSupportedException("Game not active");
    }

    if (column < 0 || column >= GameBoardImpl.COLUMNS) {
      throw new IllegalParameterException("Column out of bounds");
    }

    int row = board.placeChip(column, currentPlayer);

    notifyBoardChanged();

    if (checkForWin()) {
      gameActive = false;
      notifyGameEnded(currentPlayer, false);
    } else if (checkForDraw()) {
      gameActive = false;
      notifyGameEnded(null, true);
    } else {
      currentPlayer = (currentPlayer == player1) ? player2 : player1;
      notifyTurnChanged();
    }

    return row;
  }

  /**
   * Applies toxic decay to all toxic fields that are currently occupied.
   *
   * <p>Each occupied toxic field decrements its decay counter. If the counter reaches 0,
   * the chip explodes and gravity is applied to the column.
   *
   * @throws OperationNotSupportedException if the game is not active
   */
  @Override
  public void applyToxicDecay() throws OperationNotSupportedException {
    if (!gameActive) {
      throw new OperationNotSupportedException(
          "Toxic decay cannot be applied because the game is not active.");
    }

    for (int r = 0; r < GameBoardImpl.ROWS; r++) {
      for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
        FieldImpl f = (FieldImpl) board.getField(r, c);

        if (f.isToxicZone() && f.isOccupied()) {
          f.decrementDecayTime();

          if (f.getDecayTime() <= 0) {
            explodeColumnAt(r, c);
          }
        }
      }
    }

    notifyBoardChanged();
  }

  /**
   * Handles an explosion at a toxic field and applies gravity in that column.
   *
   * @param row the row index of the toxic field
   * @param col the column index of the toxic field
   */
  private void explodeColumnAt(int row, int col) {
    LOGGER.info("Toxic Meltdown at row " + row + ", column " + col);

    FieldImpl target = (FieldImpl) board.getField(row, col);
    target.setOccupyingPlayer(null);
    target.setDecayTime(0);

    for (int r = row; r > 0; r--) {
      FieldImpl cur = (FieldImpl) board.getField(r, col);
      FieldImpl above = (FieldImpl) board.getField(r - 1, col);
      cur.setOccupyingPlayer(above.getOccupyingPlayer());
    }

    ((FieldImpl) board.getField(0, col)).setOccupyingPlayer(null);

    // After gravity: if a stone lands on a toxic field, decay restarts at 3.
    for (int r = 0; r < GameBoardImpl.ROWS; r++) {
      FieldImpl f = (FieldImpl) board.getField(r, col);
      if (f.isToxicZone() && f.isOccupied()) {
        f.setDecayTime(3);
      } else if (f.isToxicZone() && !f.isOccupied()) {
        f.setDecayTime(0);
      }
    }
  }

  /**
   * Checks if any win condition is met.
   *
   * @return true if a player has four in a row
   */
  public boolean checkForWin() {
    return checkHorizontal() || checkVertical() || checkDiagonal();
  }

  /**
   * Checks horizontal win condition.
   *
   * @return true if found
   */
  public boolean checkHorizontal() {
    for (int r = 0; r < GameBoardImpl.ROWS; r++) {
      for (int c = 0; c < GameBoardImpl.COLUMNS - 3; c++) {
        Player p = board.getField(r, c).getOccupyingPlayer();
        if (p != null
            && p == board.getField(r, c + 1).getOccupyingPlayer()
            && p == board.getField(r, c + 2).getOccupyingPlayer()
            && p == board.getField(r, c + 3).getOccupyingPlayer()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks vertical win condition.
   *
   * @return true if found
   */
  public boolean checkVertical() {
    for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
      for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
        Player p = board.getField(r, c).getOccupyingPlayer();
        if (p != null
            && p == board.getField(r + 1, c).getOccupyingPlayer()
            && p == board.getField(r + 2, c).getOccupyingPlayer()
            && p == board.getField(r + 3, c).getOccupyingPlayer()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks diagonal win condition.
   *
   * @return true if found
   */
  public boolean checkDiagonal() {
    for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
      for (int c = 0; c < GameBoardImpl.COLUMNS - 3; c++) {
        Player p = board.getField(r, c).getOccupyingPlayer();
        if (p != null
            && p == board.getField(r + 1, c + 1).getOccupyingPlayer()
            && p == board.getField(r + 2, c + 2).getOccupyingPlayer()
            && p == board.getField(r + 3, c + 3).getOccupyingPlayer()) {
          return true;
        }
      }
    }

    for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
      for (int c = 3; c < GameBoardImpl.COLUMNS; c++) {
        Player p = board.getField(r, c).getOccupyingPlayer();
        if (p != null
            && p == board.getField(r + 1, c - 1).getOccupyingPlayer()
            && p == board.getField(r + 2, c - 2).getOccupyingPlayer()
            && p == board.getField(r + 3, c - 3).getOccupyingPlayer()) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Checks if the game is a draw (board full and no winner).
   *
   * @return true if draw
   */
  public boolean checkForDraw() {
    for (int r = 0; r < GameBoardImpl.ROWS; r++) {
      for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
        if (board.getField(r, c).getOccupyingPlayer() == null) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Legacy listener registration method (kept for compatibility).
   *
   * @param listener the listener to register
   * @throws OperationNotSupportedException never thrown here, kept for interface compatibility
   * @deprecated use {@link #addGameListener(GameListener)} instead
   */
  @Override
  @Deprecated
  public void registerListener(GameListener listener) throws OperationNotSupportedException {
    addGameListener(listener);
  }

  /**
   * Adds a game listener.
   *
   * @param l the listener
   */
  @Override
  public void addGameListener(GameListener l) {
    if (l != null && !listeners.contains(l)) {
      listeners.add(l);
    }
  }

  /**
   * Removes a game listener.
   *
   * @param l the listener
   */
  @Override
  public void removeGameListener(GameListener l) {
    listeners.remove(l);
  }

  /**
   * Notifies listeners that the current turn has changed.
   */
  private void notifyTurnChanged() {
    for (GameListener l : listeners) {
      l.onTurnChanged(currentPlayer);
    }
  }

  /**
   * Notifies listeners that the board has changed.
   */
  private void notifyBoardChanged() {
    for (GameListener l : listeners) {
      l.onBoardChanged(board);
    }
  }

  /**
   * Notifies listeners that the game ended.
   *
   * @param winner the winner or null
   * @param draw true if draw
   */
  private void notifyGameEnded(Player winner, boolean draw) {
    for (GameListener l : listeners) {
      l.onGameEnded(winner, draw);
    }
  }

  /**
   * Returns the current game board.
   *
   * @return the board
   */
  @Override
  public GameBoard getBoard() {
    return board;
  }

  /**
   * Returns the current player.
   *git branch
   *  @return the current player
   * @throws OperationNotSupportedException if the game has not been started
   */
  @Override
  public Player getCurrentPlayer() throws OperationNotSupportedException {
    if (currentPlayer == null) {
      throw new OperationNotSupportedException("Game not initialized");
    }
    return currentPlayer;
  }
}