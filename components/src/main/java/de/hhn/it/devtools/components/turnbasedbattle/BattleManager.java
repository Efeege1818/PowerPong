package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;

/**
 * Manages the battle between two players and their monsters.
 */
public class BattleManager {

    private Monster player1Monster;
    private Monster player2Monster;
    private Monster currentMonster;
    private Monster opponentMonster;
    private boolean battleOver;

    /**
     * Constructor that initializes the two monsters for the battle.
     *
     * @param player1Monster the monster of player 1.
     * @param player2Monster the monster of player 2.
     */
    public BattleManager(Monster player1Monster, Monster player2Monster) {
        this.player1Monster = player1Monster;
        this.player2Monster = player2Monster;
        this.currentMonster = player1Monster;
        this.opponentMonster = player2Monster;
        this.battleOver = false;
    }

    /**
     * Starts the battle.
     */
    public void startBattle() {
        // Initialize battle logic here (e.g., set HP to max, log start)
    }

    /**
     * Ends the battle.
     *
     * @throws IllegalStateException if the battle is not currently running.
     */
    public void endBattle() {
        // Cleanup, declare winner, etc.
    }

    /**
     * Executes the currently selected action for the active monster.
     * Lets monsters attack each other.
     *
     * @throws IllegalStateException if the battle is not running.
     */
    public void executeTurn() {
        // Example: perform a basic attack
        if (currentMonster == null || opponentMonster == null) {
            throw new IllegalStateException("No active monsters set!");
        }

        // Attack logic here (damage calculation etc.)
    }

    /**
     * Switches to the next player's turn.
     * If a turn has ended successfully, this method sets the next monster as the current one.
     */
    public void nextTurn() {
        switchTurns();
    }

    /**
     * Returns the monster that is currently active.
     *
     * @return the current Monster.
     */
    public Monster getCurrentMonster() {
        return currentMonster;
    }

    /**
     * Returns the monster that is currently the opponent.
     *
     * @return the opponent Monster.
     */
    public Monster getOpponentMonster() {
        return opponentMonster;
    }

    /**
     * Checks if the battle is over.
     *
     * @return true if one monster is fainted.
     */
    public boolean isBattleOver() {
        return battleOver;
    }

    /**
     * Checks which player has won the battle.
     *
     * @return the Player who has won, or null if still ongoing.
     */
    public Player getWinner() {
        // Determine winner logic here
        return null;
    }

    /**
     * Switches the current monster and opponent monster.
     */
    private void switchTurns() {
        Monster temp = currentMonster;
        currentMonster = opponentMonster;
        opponentMonster = temp;
    }
}
