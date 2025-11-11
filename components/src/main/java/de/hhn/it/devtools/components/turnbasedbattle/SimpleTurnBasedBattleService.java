//SimTBBService
package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;
import java.util.ArrayList;
import java.util.List;

import static de.hhn.it.devtools.apis.turnbasedbattle.MoveType.*;

/**
 * A simple implementation of the TurnBasedBattleService interface.
 * Manages the battle between two players and their monsters.
 */
public class SimpleTurnBasedBattleService implements TurnBasedBattleService {

    private GameState gameState;
    private Player player1;
    private Player player2;
    //for UI
    private Monster player1Monster;
    private Monster player2Monster;
    //for logic
    private SimpleMonster p1SimpleMonster;
    private SimpleMonster p2SimpleMonster;

    private Player currentPlayer;
    private SimpleMonster currentMonster;
    private SimpleMonster opponentMonster;
    private List<TurnBasedBattleListener> listeners;
    private boolean battleOver;
    private int turnCount;
    private SimpleSelectScreen selectScreen = new SimpleSelectScreen();

    /**
     * Constructor initializes the service with default state.
     */
    public SimpleTurnBasedBattleService() {
        this.gameState = GameState.READY;
        this.listeners = new ArrayList<>();
        this.battleOver = false;
        this.turnCount = 0;
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
        this.turnCount = 0;
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

        p1SimpleMonster = new SimpleMonster(player1Monster);
        p2SimpleMonster = new SimpleMonster(player2Monster);

        gameState = GameState.RUNNING;
        currentPlayer = determineStartingPlayer();

        if (currentPlayer == player1) {
            currentMonster = p1SimpleMonster;
            opponentMonster = p2SimpleMonster;
        } else {
            currentMonster = p2SimpleMonster;
            opponentMonster = p1SimpleMonster;
        }

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
        if (p1SimpleMonster.getCurrentHp() <= 0) {
            notifyGameEnded(2);
        } else if (p2SimpleMonster.getCurrentHp() <= 0) {
            notifyGameEnded(1);
        }
    }

    @Override
    public boolean addListener(TurnBasedBattleListener listener)
            throws IllegalArgumentException, IllegalStateException {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (listeners.contains(listener)) {
            throw new IllegalStateException("listener already exists");
        }
        return listeners.add((SimpleTurnBasedBattleListener) listener);    }

    @Override
    public boolean removeListener(TurnBasedBattleListener listener) {
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
            throw new IllegalStateException("Game must be READY to setup Players");
        }
        this.player1 = player1;
        this.player2 = player2;
        this.player1Monster = selectScreen.getP1Monster();
        this.player2Monster = selectScreen.getP2Monster();
        updatePlayersState();
    }

    @Override
    public void executeTurn(int move) throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Game must be RUNNING to execute Turn");
        }
        if (currentMonster == null || opponentMonster == null) {
            throw new IllegalStateException("No monster set");
        }
        if (!currentMonster.hasMove(move)) {
            throw new IllegalArgumentException("Invalid move index");
        }

        Move selectedMove = currentMonster.getMove(move);

        switch (selectedMove.type()) {
            case ATTACK -> {
                // do damage
                opponentMonster.takeDamage(selectedMove, currentMonster);
                // Check for death
                if (!opponentMonster.isAlive()) {
                    battleOver = true;
                    gameState = GameState.END;
                    notifyGameStateChanged(GameState.END);

                    if (currentPlayer == player1) {
                        notifyGameEnded(1);
                    } else {
                        notifyGameEnded(2);
                    }
                    return;
                }
            }

            case BUFF -> {
                currentMonster.buffMonster(selectedMove);
            }

            case DEBUFF -> {
                opponentMonster.debuffMonster(selectedMove);
            }

            default -> throw new IllegalStateException("Unknown move type: " + selectedMove.type());
        }

        // Notify listeners about state update (e.g., UI refresh)
        updatePlayersState();

        // Move to next player's turn if battle continues
        if (!battleOver) {
            nextTurn();
            notifyListenersTurnChanged();

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
            currentMonster = p2SimpleMonster;
            opponentMonster = p1SimpleMonster;
        } else {
            currentPlayer = player1;
            currentMonster = p1SimpleMonster;
            opponentMonster = p2SimpleMonster;
        }
        turnCount++;
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
    public int getTurnCount() {
        return turnCount;
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

    @Override
    public Player determineStartingPlayer() {
        //if monster elements ate the same
        if (currentMonster.getElement() == opponentMonster.getElement()) {
            if (Math.random() < 0.50) {
                return player1;
            } else {
                return player2;
            }
        }
        if (isElementEffective(player1Monster, player2Monster)) {
            return player1;
        } else {
            return player2;
        }
    }

    @Override
    public boolean isElementEffective(Monster currentMonster, Monster opponentMonster) {
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
