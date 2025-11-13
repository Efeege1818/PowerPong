package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.GameListener;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.GameRules;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the ConnectFourService facade.
 * This class contains all the game logic and state management.
 */
public class ConnectFourServiceImpl implements ConnectFourService {

    // --- Internal States (Schritt 3 von Phase 1) ---
    private final GameBoardImpl board;
    private final GameRules gameRules;
    private final List<GameListener> listeners;

    private Player currentPlayer;
    private GameConfiguration configuration;
    private boolean gameActive = false;

    /**
     * Constructor
     */
    public ConnectFourServiceImpl() {
        this.board = new GameBoardImpl();
        this.gameRules = new GameRules(GameBoardImpl.ROWS, GameBoardImpl.COLUMNS);
        this.listeners = new ArrayList<>();
    }

    // --- Interface Method Implementations (Platzhalter fÃ¼r TDD) ---

    @Override
    public void startGame(GameConfiguration configuration) {
        this.configuration = configuration;
        this.board.clearBoard();
        // TODO: Implement logic to place random toxic fields based on config

        // TODO: Implement Player creation (P1, P2)
        // this.currentPlayer = ...

        this.gameActive = true;

        // TODO: Notify listeners
        // listeners.forEach(l -> l.onPlayerTurnChanged(currentPlayer));
    }

    @Override
    public void registerListener(GameListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException {
        if (!gameActive) {
            throw new OperationNotSupportedException("Game is not active. Call startGame() first.");
        }

        // TODO: Implement logic (Phase 2)
        throw new OperationNotSupportedException("dropChip is not yet implemented.");
    }

    @Override
    public GameBoard getBoard() {
        return this.board;
    }

    @Override
    public Player getCurrentPlayer() throws OperationNotSupportedException {
        // TODO: Implement logic (Phase 2)
        throw new OperationNotSupportedException("getCurrentPlayer is not yet implemented.");
    }

    @Override
    public boolean checkForWin() throws OperationNotSupportedException {
        // TODO: Implement logic (Phase 2)
        throw new OperationNotSupportedException("checkForWin is not yet implemented.");
    }

    @Override
    public boolean checkForDraw() throws OperationNotSupportedException {
        // TODO: Implement logic (Phase 2)
        throw new OperationNotSupportedException("checkForDraw is not yet implemented.");
    }

    @Override
    public void addGameListener(GameListener listener) {

    }

    @Override
    public void removeGameListener(GameListener listener) {

    }

    @Override
    public void applyToxicDecay() {
        // TODO: Implement logic (Phase 2)
        // (This logic will likely be called internally by dropChip)
    }
}