package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.SelectScreen;

import java.util.List;

public class SimpleSelectScreen implements SelectScreen {
    private boolean selected1 = false;
    private boolean selected2 = false;

    @Override
    public boolean MonsterForP1(Monster monster) {
        return selected1 = true;
    }

    @Override
    public boolean MonsterForP2(Monster monster) {
        return selected2 = true;
    }

    @Override
    public List<Monster> getAvailableMonsters(List<Monster> monsters) {
        return monsters;
    }

    @Override
    public boolean isSelectionFinished() {
        return selected1 && selected2;
    }
}
