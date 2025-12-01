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
        this.configuration = configuration;
        this.board.clearBoard();
        this.currentPlayer = player1;
        this.gameActive = true;

        // Listener benachrichtigen
        notifyBoardChanged();
        notifyTurnChanged();
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
        if (!gameActive) {
            throw new OperationNotSupportedException("Game is not active. Call startGame() first.");
        }
        if (column < 0 || column >= GameBoardImpl.COLUMNS) {
            throw new IllegalParameterException("Column index " + column + " out of bounds.");
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
     * Checks if the last move resulted in a win condition (four chips in a row).
     *
     * @return {@code true} if a player has won, {@code false} otherwise.
     * @throws OperationNotSupportedException This method is currently not implemented.
     */
    public boolean checkForWin() {


        for (int r = 0; r < GameBoardImpl.ROWS; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
                Player p = board.getField(r, c).getOccupyingPlayer();
                if (p == null) continue;


                if (r + 3 < GameBoardImpl.ROWS) {
                    if (p == board.getField(r + 1, c).getOccupyingPlayer() &&
                            p == board.getField(r + 2, c).getOccupyingPlayer() &&
                            p == board.getField(r + 3, c).getOccupyingPlayer()) {
                        return true;
                    }
                }


                if (c + 3 < GameBoardImpl.COLUMNS) {
                    if (p == board.getField(r, c + 1).getOccupyingPlayer() &&
                            p == board.getField(r, c + 2).getOccupyingPlayer() &&
                            p == board.getField(r, c + 3).getOccupyingPlayer()) {
                        return true;
                    }
                }


                if (r + 3 < GameBoardImpl.ROWS && c + 3 < GameBoardImpl.COLUMNS) {
                    if (p == board.getField(r + 1, c + 1).getOccupyingPlayer() &&
                            p == board.getField(r + 2, c + 2).getOccupyingPlayer() &&
                            p == board.getField(r + 3, c + 3).getOccupyingPlayer()) {
                        return true;
                    }
                }


                if (r + 3 < GameBoardImpl.ROWS && c - 3 >= 0) {
                    if (p == board.getField(r + 1, c - 1).getOccupyingPlayer() &&
                            p == board.getField(r + 2, c - 2).getOccupyingPlayer() &&
                            p == board.getField(r + 3, c - 3).getOccupyingPlayer()) {
                        return true;
                    }
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

        if (!gameActive) {
            throw new OperationNotSupportedException("Toxic decay cannot be applied because the game is not active.");
        }

        for (int r = 0; r < GameBoardImpl.ROWS; r++) {
            for (int c = 0; c < GameBoardImpl.COLUMNS; c++) {
                FieldImpl currentField = (FieldImpl) board.getField(r, c);
                if (currentField.isToxicZone() && currentField.isOccupied()) {
                    currentField.decrementDecayTime();
                    if (currentField.getDecayTime() <= 0) {
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

    /**
     * Test helper: player parametresi ile drop yapar, currentPlayer kontrolü yoktur.
     */
    public int dropChipForTest(int column, Player player) throws IllegalParameterException, OperationNotSupportedException {
        if (!gameActive) {
            throw new OperationNotSupportedException("Game is not active. Call startGame() first.");
        }
        if (column < 0 || column >= GameBoardImpl.COLUMNS) {
            throw new IllegalParameterException("Column index " + column + " is out of bounds (0-6).");
        }

        return board.placeChip(column, player);
    }





}