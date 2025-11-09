package de.hhn.it.devtools.apis.turnbasedbattle;

import java.util.List;

/**
 * This class is for selecting Monsters and assign them to a player
 */
public class SelectScreen {

    //Fields
    private Player player1;
    private Player player2;
    private List<Monster> availableMonsters;

    //constructor
    public SelectScreen(Player player1, Player player2, List<Monster> availableMonsters) {

    }

    /**
     * Player 1 selects a monster.
     * @param monster
     */
    public void MonsterForP1(Monster monster) {

    }

    /**
     * Player 2 selects a monster.
     * @param monster
     */
    public void MonsterForP2(Monster monster) {

    }

    public List<Monster> getAvailableMonsters(){
        return null; //TODO: replace Placeholder value
    }

    /**
     * Checks if both players have selected a monster.
     * @return true if both players have selected a monster
     */
    public boolean isSelectionFinished(){
        return false;
    }

}
