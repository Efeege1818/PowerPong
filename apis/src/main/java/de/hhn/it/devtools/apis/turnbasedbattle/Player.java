package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * handles inputs and creates monster objects.
 */
public class Player {

    // Fields
    private int playerID; // Player1 oder Player 2
    private Monster monster;

    // Constructor
    public Player(int playerID, Monster monster) {

    }
    /**
     * Assigns a monster to this player.
     * @param monster
     */
    public void assignMonster(Monster monster) {

    }

    /**
     * Orders the Monster what to do.
     */
    public void commandMonster(){
        //call Move Methods in monster class based on the input of the player
    }

    /**
     * Returns this player's ID ("Player 1" or "Player 2").
     *
     * @return player ID.
     */
    public int getPlayerId() {
        return 0;  //TODO: replace Placeholder value
    }

    /**
     * Checks if this player's monster is still able to fight.
     *
     * @return true if monster has remaining HP.
     */
    public boolean isMonsterAlive(Monster monster){
        return false;
    }


    public Monster getMonster() {
        return monster;
    }

    /**
     * Returns current health value of the monster that the player has.
     *
     * @return current hp of monster.
     */
    public int getMonsterCurrentHp() {
        return monster.currentHp();
    }
}
