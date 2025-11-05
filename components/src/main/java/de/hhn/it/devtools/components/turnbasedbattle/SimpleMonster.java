package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Move;

import java.util.HashMap;

public class SimpleMonster {
    //public record Monster(int maxHp, int currentHp, int attack, int defense, double evasionChance, double critChance, Element element, HashMap<Integer, Move> moves) {

    public int maxHp;
    public int currentHp;
    public int attack;
    public int defense;
    public double evasionChance;
    public double critChance;
    public Element element;
    public HashMap<Integer, Move> moves;

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public double getEvasionChance() {
        return evasionChance;
    }

    public double getCritChance() {
        return critChance;
    }

    public Element getElement() {
        return element;
    }

    public HashMap<Integer, Move> getMoves() {
        return moves;
    }

}
