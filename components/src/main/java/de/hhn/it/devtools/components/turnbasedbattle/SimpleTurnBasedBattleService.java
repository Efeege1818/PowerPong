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
    private Player stertingPlayer;
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
        notifyGameStateChanged(GameState.READY);
    }

    @Override
    public void start() throws IllegalStateException {
        if (gameState != GameState.READY) {
            throw new IllegalStateException("Game must be in READY state to start");
        }
        if (player1 == null || player2 == null) {
            throw new IllegalStateException("Players must be set up before starting");
        }
        gameState = GameState.RUNNING;
        currentPlayer = determineStartingPlayer();
        notifyGameStateChanged(GameState.RUNNING);
        updatePlayersState();
    }

    @Override
    public void pause() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to pause");
        }
        gameState = GameState.PAUSED;
        notifyGameStateChanged(GameState.PAUSED);
    }

    @Override
    public void abort() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to abort");
        }
        gameState = GameState.ABORTED;
        notifyGameStateChanged(GameState.ABORTED);
    }

    @Override
    public void end() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to end");
        }
        gameState = GameState.END;
        notifyGameStateChanged(GameState.END);

        // Determine winner and notify listeners
        if (player1Monster.currentHp() <= 0) {
            notifyGameEnded(2);
        } else if (player2Monster.currentHp() <= 0) {
            notifyGameEnded(1);
        }
    }

    @Override
    public boolean addListener(de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleListener listener)
            throws IllegalArgumentException, IllegalStateException {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (listeners.contains(listener)) {
            throw new IllegalStateException("listener already exists");
        }
        return listeners.add((TurnBasedBattleListener) listener);    }

    @Override
    public boolean removeListener(de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleListener listener) {
        return listeners.remove(listener);
    }


    private void notifyGameStateChanged(GameState newState) {
        for (TurnBasedBattleListener listener : listeners) {
            listener.newGameState(newState);
        }
    }

    private void updatePlayersState() {
        for (TurnBasedBattleListener listener : listeners) {
            listener.updateState(player1, player2);
        }
    }

    private void notifyGameEnded(int winnerNumber) {
        for (TurnBasedBattleListener listener : listeners) {
            listener.gameEnded(winnerNumber);
        }
    }

    @Override
    public void notifyListenersTurnChanged() {
        updatePlayersState();
    }

    @Override
    public void notifyListenersBattleEnded() {
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public void setupPlayers(Player player1, Player player2) throws IllegalStateException {
        if (gameState != GameState.READY) {
            throw new IllegalStateException("Can only setup players when game is in READY state");
        }
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = stertingPlayer;
        updatePlayersState();
    }

    @Override
    public void executeTurn(int move) throws IllegalStateException {

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
        updatePlayersState();
    }

    @Override
    public void executeTurn() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be in RUNNING state to execute turn");
        }
        if (currentMonster == null || opponentMonster == null) {
            throw new IllegalStateException("No active monsters set");
        }

        if (opponentMonster.currentHp() <= 0) {
            battleOver = true;
            end();
        }
        updatePlayersState();
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

        updatePlayersState();
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
        if (player1Monster.currentHp() <= 0) {
            return player2;
        } else if (player2Monster.currentHp() <= 0) {
            return player1;
        }
        return null;
    }

    public Player determineStartingPlayer() {
       if(isElementEffective(player1Monster, player2Monster)) {
           return player1;
       } else {
           return player2;
       }
    }

    public boolean isElementEffective (Monster currentMonster, Monster opponentMonster ) {
        Element current = currentMonster.getElement();
        Element opponent = opponentMonster.getElement();

        //Fire
        if (current==Element.FIRE) {
            if (opponent == Element.GRASS) {
                return true;
            } else {
                return false;
            }
        }

        //Water
        if (current==Element.WATER){
            if (opponent==Element.FIRE){
                return true;
            } else{
                return false;
            }
        }

        //Grass
        if (current==Element.GRASS){
            if (opponent==Element.WATER){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}