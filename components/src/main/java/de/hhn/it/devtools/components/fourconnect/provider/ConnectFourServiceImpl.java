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
 * of Four-Connect. It holds the current game state, manages the {@link GameBoardImpl},
 * tracks the current player, and implements core game logic like starting a game
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
        // Log at method entry (Informational)
        logger.info("startGame called. Configuration received.");

        this.configuration = configuration;
        this.board.clearBoard();
        this.currentPlayer = player1;
        this.gameActive = true;

        notifyBoardChanged();
        notifyTurnChanged();

        logger.info("Game started successfully. Current player: " + currentPlayer.name());
    }


    /**
     * Attempts to drop a chip for the {@link #currentPlayer} into the specified column.
     * <p>
     * If successful, the chip is placed at the lowest available row in that column,
     * and the turn switches to the next player.
     * </p>
     *
     * @param column The 0-based index of the column to drop the chip into.
     * @return The row index (0-based) where the chip landed.
     * @throws IllegalParameterException If the column index is out of bounds or the column is full.
     * @throws OperationNotSupportedException If the game is not currently active.
     */
    @Override
    public int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException {
        // Log which column the move was made in
        logger.info("dropChip called for column: " + column + " by player: " + (currentPlayer != null ? currentPlayer.name() : "null"));

        if (!gameActive) {
            // Log before throwing an exception (Warning or Severe level)
            logger.warning("Attempt to drop chip while game is not active.");
            throw new OperationNotSupportedException("Game is not active. Call startGame() first.");
        }
        if (column < 0 || column >= GameBoardImpl.COLUMNS) {
            logger.warning("Invalid column index: " + column);
            throw new IllegalParameterException("Column index " + column + " out of bounds.");
        }

        int row = board.placeChip(column, currentPlayer);
        logger.info("Chip placed at Row: " + row + ", Column: " + column);

        notifyBoardChanged();

        if (checkForWin()) {
            logger.info("Winning condition detected! Winner: " + currentPlayer.name());
            gameActive = false;
            notifyGameEnded(currentPlayer, false);
        } else if (checkForDraw()) {
            logger.info("Draw condition detected!");
            gameActive = false;
            notifyGameEnded(null, true);
        } else {
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
            notifyTurnChanged();
            logger.info("Turn switched to: " + currentPlayer.name());
        }

        return row;
    }



    /**
     * Registers a game listener to receive updates about the game state.
     *
     * @param listener The listener to register.
     * @throws OperationNotSupportedException This method is currently not implemented.
     * @deprecated Use {@link #addGameListener(GameListener)} instead.
     */
    @Override
    @Deprecated
    public void registerListener(GameListener listener) {
        // Delegiert auf die neue Methode
        addGameListener(listener);
    }


    /**
     * Returns the current state of the game board.
     *
     * @return The active {@link GameBoard}.
     */
    @Override
    public GameBoard getBoard() {
        return this.board;
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return The current {@link Player}.
     * @throws OperationNotSupportedException If the game has not been initialized ({@code startGame} has not been called).
     */
    @Override
    public Player getCurrentPlayer() throws OperationNotSupportedException {
        if (currentPlayer == null) {
            throw new OperationNotSupportedException("Game not initialized.");
        }
        return currentPlayer;
    }

    /**
     * Checks if the last move resulted in a win condition.
     * Refactored to use helper methods.
     */
    public boolean checkForWin() {
        return checkHorizontal() || checkVertical() || checkDiagonal();
    }

    public boolean checkHorizontal() {
        // Horizontal check: Limit columns to -3 (to avoid index overflow)
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
        // Vertical check: Limit rows to -3
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
        // Direction 1: Top-Left to Bottom-Right (Down-Right)
        for (int r = 0; r < GameBoardImpl.ROWS - 3; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS - 3; c++) {
                Player p = board.getField(r, c).getOccupyingPlayer();
                if (p != null &&
                        p == board.getField(r + 1, c + 1).getOccupyingPlayer() &&
                        p == board.getField(r + 2, c + 2).getOccupyingPlayer() &&
                        p == board.getField(r + 3, c + 3).getOccupyingPlayer()) {
                    return true;
                }
            }
        }

        // Direction 2: Top-Right to Bottom-Left (Down-Left)
        // Start from c = 3 because we check towards the left (c-3)
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
     *
     * @return {@code true} if the game is a draw, {@code false} otherwise.
     * @throws OperationNotSupportedException This method is currently not implemented.
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
     * Adds a {@link GameListener} to the list of objects receiving game state updates.
     *
     * @param listener The listener to be added.
     */
    @Override
    public void addGameListener(GameListener listener) {
        if (listener == null) {
            return;
        }
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a {@link GameListener} from the list of objects receiving game state updates.
     *
     * @param listener The listener to be removed.
     */
    @Override
    public void removeGameListener(GameListener listener) {
        listeners.remove(listener);
    }





    /**
     * Applies the toxic decay effect across the board, reducing the decay timer
     * on relevant fields and potentially removing chips.
     *
     * @throws OperationNotSupportedException This method is currently not implemented.
     */
    public void applyToxicDecay() throws OperationNotSupportedException {
        logger.info("applyToxicDecay triggered.");

        if (!gameActive) {
            logger.warning("applyToxicDecay failed: Game not active.");
            throw new OperationNotSupportedException("Toxic decay cannot be applied because the game is not active.");
        }

        boolean decayHappened = false; // Tracking variable for logging

        for (int r = 0; r < GameBoardImpl.ROWS; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
                FieldImpl currentField = (FieldImpl) board.getField(r, c);
                if (currentField.isToxicZone() && currentField.isOccupied()) {
                    currentField.decrementDecayTime();

                    if (currentField.getDecayTime() <= 0) {
                        logger.info("Toxic Meltdown at Col: " + c + ", Row: " + r);
                        decayHappened = true;

                        // ... Melting logic ...
                        System.out.println("Poison activated! Column: " + c + ", Row: " + r);
                        for (int i = r; i > 0; i--) {
                            FieldImpl fieldCurrent = (FieldImpl) board.getField(i, c);
                            FieldImpl fieldAbove = (FieldImpl) board.getField(i - 1, c);
                            fieldCurrent.setOccupyingPlayer(fieldAbove.getOccupyingPlayer());
                        }

                        FieldImpl topField = (FieldImpl) board.getField(0, c);
                        topField.setOccupyingPlayer(null);
                        if (currentField.isOccupied()) {
                            currentField.setDecayTime(3);
                        } else {
                            currentField.setDecayTime(0);
                        }
                    }
                }
            }
        }

        if (!decayHappened) {
            logger.info("Toxic decay applied but no chips melted.");
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