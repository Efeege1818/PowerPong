package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 *Manages the Battle
 */
public class BattleManager {

    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player opponentPlayer;
    private boolean battleOver;

    //constructor
    public BattleManager(Player player1, Player player2, Player opponentPlayer) {}

    /**
     * starts the battle.
     */
    public void startBattle(){}

    /**
     * switches to the next turn and counts the turns in battle.
     */
    public void nextTurn(){}

    /**
     * lets Monsters attack each other.
     */
    public void doAttack(){}

    /**
     * ends the Battle.
     * @return true if one Monster is dead.
     */
    public boolean isBattleOver(){
        return false;
    }

    /**
     * cecks fi a Player has won the battle.
     * @return Player how has won.
     */
    public Player getWinner(){
        return null;
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    public Player getOpponentPlayer(){
        return opponentPlayer;
    }

    /**
     * switches the current Player and the opponent Player.
     */
    private void switchTurns(){}
}
