package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;

/**
 *Manages the Battle
 */
public class BattleManager {

    private Monster player1Monster;
    private Monster player2Monster;
    private Monster currentMonster;
    private Monster opponentMonster;
    private boolean battleOver;

    //constructor
    public BattleManager(Monster player1Monster, Monster player2Monster) {}

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
     * checks if a Player has won the battle.
     * @return Player how has won.
     */
    public Player getWinner(){
        return null;
    }

    public Monster getcurrentMonster(){
        return currentMonster;
    }
    public Monster getopponentMonster(){
        return opponentMonster;
    }

    /**
     * switches the current Player/Monster and the opponent Player.
     */
    private void switchTurns(){}
}
