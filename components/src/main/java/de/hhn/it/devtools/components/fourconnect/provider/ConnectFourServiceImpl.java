package de.hhn.it.devtools.components.fourconnect.provider;

import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.GameListener;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.apis.fourconnect.GameRules;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of the ConnectFourService facade.
 */
public class ConnectFourServiceImpl implements ConnectFourService {
    private final GameBoardImpl board;
    private final GameRules gameRules;
    private final List<GameListener> listeners;

    private final Player player1;
    private final Player player2;

    private Player currentPlayer;
    private GameConfiguration configuration;
    private boolean gameActive = false;

    /** Constructor */
    public ConnectFourServiceImpl() {
        this.board = new GameBoardImpl();
        this.gameRules = new GameRules(GameBoardImpl.ROWS, GameBoardImpl.COLUMNS);
        this.listeners = new ArrayList<>();
        this.player1 = new Player("Player Red", PlayerColor.RED);
        this.player2 = new Player("Player Yellow", PlayerColor.YELLOW);
    }

    @Override
    public void startGame(GameConfiguration configuration) {
        this.configuration = configuration;
        this.board.clearBoard(); // Setzt das Board zurück (Annahme: clearBoard existiert und initialisiert)

        // TODO: Logik zur Platzierung der toxischen Felder (kommt später)

        this.currentPlayer = player1;
        this.gameActive = true; // Setzt das Spiel aktiv
    }

    @Override
    public int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException {
        // Prüfe, ob das Spiel gestartet ist
        if (!gameActive) {
            throw new OperationNotSupportedException("Game is not active. Call startGame() first.");
        }

        // Prüfe auf ungültige Spalten
        if (column < 0 || column >= GameBoardImpl.COLUMNS) {
            throw new IllegalParameterException("Column index " + column + " is out of bounds (0-6).");
        }

        return 0;
    }

    @Override
    public void registerListener(GameListener listener) throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Method not implemented.");
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

    @Override
    public boolean checkForWin() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Method not implemented.");
    }

    @Override
    public boolean checkForDraw() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Method not implemented.");
    }

    @Override
    public void addGameListener(GameListener listener) {

    }

    @Override
    public void removeGameListener(GameListener listener) {

    }

    @Override
    public void applyToxicDecay() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Method not implemented.");
    }
}