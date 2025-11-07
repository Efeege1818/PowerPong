package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the TurnBasedBattleService interface.
 * Manages the battle between two players and their monsters.
 */
public class SimpleTurnBasedBattleService implements TurnBasedBattleService {

    private GameState gameState;
    private Player player1;
    private Player player2;
    private Monster player1Monster;
    private Monster player2Monster;
    private Player currentPlayer;
    private Monster currentMonster;
    private Monster opponentMonster;
    private List<TurnBasedBattleListener> listeners;
    private boolean battleOver;

    /**
     * Constructor initializes the service with default state.
     */
    public SimpleTurnBasedBattleService() {
        this.gameState = GameState.READY;
        this.listeners = new ArrayList<>();
        this.battleOver = false;
    }

    @Override
    public void reset() {
        this.gameState = GameState.READY;
        this.player1 = null;
        this.player2 = null;
        this.player1Monster = null;
        this.player2Monster = null;
        this.currentPlayer = null;
        this.currentMonster = null;
        this.opponentMonster = null;
        this.battleOver = false;
    }

    @Override
    public void start() throws IllegalStateException {
        if (gameState != GameState.READY) {
            throw new IllegalStateException("Game must be in READY state to start");
        }
        if (player1 == null || player2 == null || player1Monster == null || player2Monster == null) {
            throw new IllegalStateException("Players and monsters must be set up before starting");
        }
        gameState = GameState.RUNNING;
        currentPlayer = player1;
        currentMonster = player1Monster;
        opponentMonster = player2Monster;
    }

    @Override
    public void end() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to end");
        }
        gameState = GameState.END;
        notifyListenersBattleEnded();
    }

    @Override
    public boolean addListener(TurnBasedBattleListener listener)
            throws IllegalArgumentException, IllegalStateException {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        if (listeners.contains(listener)) {
            throw new IllegalStateException("Listener already registered");
        }
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(TurnBasedBattleListener listener) {
        return listeners.remove(listener);
    }

    @Override
    public void notifyListenersTurnChanged() {
        /**
        for (TurnBasedBattleListener listener : listeners) {
            listener.onTurnChanged();
        }
         */
    }

    @Override
    public void notifyListenersBattleEnded() {
        /**
        for (TurnBasedBattleListener listener : listeners) {
            listener.onBattleEnded();
        }
         */
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void setupPlayers(Player player1, Monster monster1, Player player2, Monster monster2)
            throws IllegalStateException {
        if (gameState != GameState.READY) {
            throw new IllegalStateException("Can only setup players when game is in READY state");
        }
        this.player1 = player1;
        this.player2 = player2;
        this.player1Monster = monster1;
        this.player2Monster = monster2;
        this.currentPlayer = player1;
        this.currentMonster = monster1;
        this.opponentMonster = monster2;
    }

    @Override
    public void executeTurn() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to execute turn");
        }
        if (currentMonster == null || opponentMonster == null) {
            throw new IllegalStateException("No active monsters set");
        }

        // Execute the turn logic here (e.g., apply moves, calculate damage)
        // Check if the battle is over after execution
        if (opponentMonster.currentHp() <= 0) {
            battleOver = true;
            end();
        }
    }

    @Override
    public void nextTurn() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to change turns");
        }
        if (battleOver) {
            throw new IllegalStateException("Cannot change turns when battle is over");
        }

        // Switch the current player and monsters
        if (currentPlayer == player1) {
            currentPlayer = player2;
            currentMonster = player2Monster;
            opponentMonster = player1Monster;
        } else {
            currentPlayer = player1;
            currentMonster = player1Monster;
            opponentMonster = player2Monster;
        }

        notifyListenersTurnChanged();
    }

    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public Player getPlayer1() {
        return player1;
    }

    @Override
    public Player getPlayer2() {
        return player2;
    }

    @Override
    public boolean isBattleOver() {
        return battleOver;
    }

    @Override
    public Player getWinner() {
        if (!battleOver) {
            return null;
        }
        // Determine winner based on monster HP
        if (player1Monster.currentHp() <= 0) {
            return player2;
        } else if (player2Monster.currentHp() <= 0) {
            return player1;
        }
        return null;
    }
}