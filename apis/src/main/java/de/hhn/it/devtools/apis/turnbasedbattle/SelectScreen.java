package de.hhn.it.devtools.apis.turnbasedbattle;

import java.util.List;

/**
 * This class is for selecting Monsters and assign them to a player.
 */
public interface SelectScreen {
  /**
     * Player 1 selects a monster.
     * @param monster
     */
    public void MonsterForP1(Monster monster);

    /**
     * Player 2 selects a monster.
     * @param monster
     */
    public void MonsterForP2(Monster monster);

    public List<Monster> getAvailableMonsters(List<Monster> monsters);

    /**
     * Checks if both players have selected a monster.
     * @return true if both players have selected a monster.
     */
    public boolean isSelectionFinished();

    public Monster getP1Monster();

    public Monster getP2Monster();
}
