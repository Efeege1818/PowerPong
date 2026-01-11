package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.GameListener;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Concrete implementation of the {@link ConnectFourService} facade.
 * <p>
 * This class serves as the main entry point for controlling and managing a game
 * of Four-Connect. It holds the current game state, manages the
 * {@link GameBoardImpl},
 * tracks the current player, and implements core game logic like starting a
 * game
 * and dropping chips.
 * </p>
 */
public class ConnectFourServiceImpl implements ConnectFourService {

    private static final Logger logger = Logger.getLogger(ConnectFourServiceImpl.class.getName());

    private final GameBoardImpl board;
    private final List<GameListener> listeners;

    private final Player player1;
    private final Player player2;

    private Player currentPlayer;
    private GameConfiguration configuration;
    private boolean gameActive = false;

    public ConnectFourServiceImpl() {
        this.board = new GameBoardImpl();
        this.listeners = new ArrayList<>();
        this.player1 = new Player("Player Red", PlayerColor.RED);
        this.player2 = new Player("Player Yellow", PlayerColor.YELLOW);

        // Log in the constructor
        logger.info("ConnectFourServiceImpl created via Constructor.");
    }

    /**
     * Initializes and starts a new game of Four-Connect.
     * <p>
     * This method saves the provided configuration, clears the board, sets the
     * current player to Player 1 (Red), and sets the game state to active.
     * </p>
     *
     * @param configuration The configuration to be used for the new game.
     */
    @Override
    public void startGame(GameConfiguration configuration) {
        logger.info("startGame called. Configuration received.");

        this.configuration = configuration;

        // Board reset
        this.board.clearBoard();

        // ✅ NEW: place toxic fields according to configuration
        this.board.placeRandomToxicZones(configuration.getToxicFieldCount());

        this.currentPlayer = player1;
        this.gameActive = true;

        notifyBoardChanged();
        notifyTurnChanged();

        logger.info("Game started successfully. Current player: " + currentPlayer.name());
    }

    /**
     * Attempts to drop a chip for the {@link #currentPlayer} into the specified
     * column.
     */
    @Override
    public int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException {

        logger.info("dropChip called for column: " + column);

        if (!gameActive) {
            throw new OperationNotSupportedException("Game is not active. Call startGame() first.");
        }

        if (column < 0 || column >= GameBoardImpl.COLUMNS) {
            throw new IllegalParameterException("Column index " + column + " out of bounds.");
        }

        int row = board.placeChip(column, currentPlayer);
        logger.info("Chip placed at row " + row + ", column " + column);

        notifyBoardChanged();

        if (checkForWin()) {
            logger.info("Winning condition detected!");
            gameActive = false;
            notifyGameEnded(currentPlayer, false);
        } else if (checkForDraw()) {
            logger.info("Draw condition detected!");
            gameActive = false;
            notifyGameEnded(null, true);
        } else {
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
            notifyTurnChanged();
        }

        return row;
    }

    @Override
    public GameBoard getBoard() {
        return this.board;
    }

    @Override
    public Player getCurrentPlayer() throws OperationNotSupportedException {
        if (currentPlayer == null) {
            throw new OperationNotSupportedException("Game not initialized.");
        }
        return currentPlayer;
    }

    /**
     * Checks if the last move resulted in a win condition.
     */
    public boolean checkForWin() {
        return checkHorizontal() || checkVertical() || checkDiagonal();
    }

    public boolean checkHorizontal() {
        for (int r = 0; r < GameBoardImpl.ROWS; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS - 3; c++) {
                Player p = board.getField(r, c).getOccupyingPlayer();
                if (p != null &&
                        p == board.getField(r, c + 1).getOccupyingPlayer() &&
                        p == board.getField(r, c + 2).getOccupyingPlayer() &&
                        p == board.getField(r, c + 3).getOccupyingPlayer()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkVertical() {
        for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
                Player p = board.getField(r, c).getOccupyingPlayer();
                if (p != null &&
                        p == board.getField(r + 1, c).getOccupyingPlayer() &&
                        p == board.getField(r + 2, c).getOccupyingPlayer() &&
                        p == board.getField(r + 3, c).getOccupyingPlayer()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkDiagonal() {
        for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS - 3; c++) {
                Player p = board.getField(r, c).getOccupyingPlayer();
                if (p != null &&
                        p == board.getField(r + 1, c + 1).getOccupyingPlayer() &&
                        p == board.getField(r, c + 2).getOccupyingPlayer() &&
                        p == board.getField(r + 3, c + 3).getOccupyingPlayer()) {
                    return true;
                }
            }
        }

        for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
            for (int c = 3; c < GameBoardImpl.COLUMNS; c++) {
                Player p = board.getField(r, c).getOccupyingPlayer();
                if (p != null &&
                        p == board.getField(r + 1, c - 1).getOccupyingPlayer() &&
                        p == board.getField(r + 2, c - 2).getOccupyingPlayer() &&
                        p == board.getField(r + 3, c - 3).getOccupyingPlayer()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the game has ended in a draw.
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
     * Registers a game listener to receive updates about the game state.
     *
     * @param listener The listener to register.
     * @throws OperationNotSupportedException This method is currently not
     *                                        implemented.
     * @deprecated Use {@link #addGameListener(GameListener)} instead.
     */
    @Override
    @Deprecated
    public void registerListener(GameListener listener) throws OperationNotSupportedException {
        // Delegate to the new method
        addGameListener(listener);
    }

    @Override
    public void addGameListener(GameListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeGameListener(GameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Applies the toxic decay effect across the board.
     */
    public void applyToxicDecay() throws OperationNotSupportedException {

        if (!gameActive) {
            throw new OperationNotSupportedException("Toxic decay cannot be applied because the game is not active.");
        }

        for (int r = 0; r < GameBoardImpl.ROWS; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
                FieldImpl f = (FieldImpl) board.getField(r, c);

                if (f.isToxicZone() && f.isOccupied()) {
                    f.decrementDecayTime();

                    if (f.getDecayTime() <= 0) {
                        logger.info("Toxic Meltdown at column " + c);

                        for (int i = r; i > 0; i--) {
                            FieldImpl current = (FieldImpl) board.getField(i, c);
                            FieldImpl above = (FieldImpl) board.getField(i - 1, c);
                            current.setOccupyingPlayer(above.getOccupyingPlayer());
                        }

                        ((FieldImpl) board.getField(0, c)).setOccupyingPlayer(null);
                    }
                }
            }
        }
    }

    private void notifyTurnChanged() {
        for (GameListener listener : listeners) {
            listener.onTurnChanged(currentPlayer);
        }
    }

    private void notifyBoardChanged() {
        for (GameListener listener : listeners) {
            listener.onBoardChanged(board);
        }
    }

    private void notifyGameEnded(Player winner, boolean isDraw) {
        for (GameListener listener : listeners) {
            listener.onGameEnded(winner, isDraw);
        }
    }
}
