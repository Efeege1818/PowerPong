package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Move;

import java.util.HashMap;

/**
 * A simple implementation of the Monster interface.
 */
public class SimpleMonster {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SimpleMonster.class);

    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private double evasionChance;
    private double critChance;
    private Element element;
    private HashMap<Integer, Move> moves;

    /**
     * Constructor for creating a SimpleMonster from a Monster.
     *
     * @param monster the Monster to create the SimpleMonster from.
     */
    public SimpleMonster(Monster monster) {
        this.maxHp = monster.maxHp();
        this.currentHp = monster.currentHp();
        this.attack = monster.attack();
        this.defense = monster.defense();
        this.evasionChance = monster.evasionChance();
        this.critChance = monster.critChance();
        this.element = monster.element();
        this.moves = monster.moves();

        logger.debug("Monster created: {}", toString());
    }

    /**
     * Takes damage from a move.
     *
     * @param move the move that caused the damage.
     * @param attackingMonster the monster that executed the move.
     */
    public void takeDamage(Move move, SimpleMonster attackingMonster) {
        int actualDamage = 0;

        // Check if the attack is evaded
        if (Math.random() < evasionChance) {
            logger.debug("Monster evaded the attack.");
            return;
        }

        boolean isCritical = Math.random() < attackingMonster.getCritChance();
        boolean isEffective = isElementEffective(move.element());

        actualDamage = calculateDamage(move, isCritical, isEffective, attackingMonster.getAttack());

        if (actualDamage > currentHp) {
            currentHp = 0;
        } else {
            currentHp -= actualDamage;
        }

        logger.debug("Monster took {} damage and has {} hp left.", actualDamage, currentHp);

    }


    /**
     * Calculates the actual damage based on the move, critical hit, and elemental effectiveness.
     * @param move the move being executed.
     * @param isCritical whether the attack is a critical hit.
     * @param isEffective whether the attack is effective against the target's element.
     * @return the actual damage done.
     */
    public int calculateDamage(Move move, boolean isCritical, boolean isEffective, int attackerAttack) {
        double damage = move.amount() + attackerAttack;
        if (isCritical) {
            damage *= 1.5; // TODO: hardcoded critical multiplier
        }
        if (isEffective) {
            damage *= 1.5; // TODO: hardcoded effective multiplier
        }

        damage -= defense;

        if (damage < 0) {
            return 0;
        }

        return (int) Math.floor(damage);
    }

    /**
     * Checks if the move is effective against the target's element.
     *
     * @param moveElement the element of the move.
     * @return true if the move is effective, false otherwise.
     */
    public boolean isElementEffective(Element moveElement) {


        //Fire
        if (element == Element.FIRE) {
            if (moveElement == Element.GRASS) {
                return true;
            } else {
                return false;
            }
        }

        //Water
        if (element == Element.WATER) {
            if (moveElement == Element.FIRE) {
                return true;
            } else{
                return false;
            }
        }

        //Grass
        if (element == Element.GRASS) {
            if (moveElement == Element.WATER) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }

    /**
     * Adds health to the monster.
     *
     * @param amount the amount of health to add.
     */
    public void addHealth(int amount) {
        currentHp += amount;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
        logger.debug("Monster health increased by {} to {}", amount, currentHp);
    }

    /**
     * Applies a buff to the monster based on the provided move.
     *
     * @param move the move that contains the buff information.
     */
    public void buffMonster(Move move) {
        String stat = move.stat();
        double amount = move.amount();
        switch (stat) {
            case "health":
                addHealth((int) amount);
                break;
            case "attack":
                attack += (int) amount;
                break;
            case "evasionChance":
                evasionChance = Math.max(0.0, Math.min(1.0, evasionChance + amount));
                break;
            case "critChance":
                critChance = Math.max(0.0, Math.min(1.0, critChance + amount));
                break;
            case "defense":
                defense += (int) amount;
                break;
            default:
                logger.warn("Invalid stat for buff: {}", stat);
        }
        logger.debug("Monster buffed: {}", stat);
    }

    /**
     * Applies a debuff to the Monster based on the provided Move.
     *
     * @param move the Move that causes the debuff.
     */
    public void debuffMonster(Move move) {

        String stat = move.stat();
        double amount = move.amount();
        switch (stat) {
            case "attack":
                attack -= (int) amount;
                break;
            case "defense":
                defense -= (int) amount;
                break;
            case "evasionChance":
                evasionChance = Math.max(0.0, Math.min(1.0, evasionChance - amount));
                break;
            case "critChance":
                critChance = Math.max(0.0, Math.min(1.0, critChance - amount));
                break;
            default:
                logger.warn("Invalid stat for debuff: {}", stat);
        }
        logger.debug("Monster debuffed: {}", stat);
    }

    /**
     * Removes a buff from the monster.
     *
     * @param move that was applied as a buff.
     */
    public void removeBuff(Move move) {
        String stat = move.stat();
        double amount = move.amount();
        switch (stat) {
            case "attack":
                attack -= (int) amount;
                break;
            case "evasionChance":
                evasionChance = Math.max(0.0, Math.min(1.0, evasionChance - amount));
                break;
            case "critChance":
                critChance = Math.max(0.0, Math.min(1.0, critChance - amount));
                break;
            case "defense":
                defense -= (int) amount;
                break;
            default:
                logger.warn("Invalid stat for removing buff: {}", stat);
        }
        logger.debug("Monster buff removed: {}", stat);
    }

    /**
     * Removes a debuff from the monster.
     *
     * @param move that was applied as a debuff.
     */
    public void removeDebuff(Move move) {
        String stat = move.stat();
        double amount = move.amount();
        switch (stat) {
            case "attack":
                attack += (int) amount;
                break;
            case "evasionChance":
                evasionChance = Math.max(0.0, Math.min(1.0, evasionChance + amount));
                break;
            case "critChance":
                critChance = Math.max(0.0, Math.min(1.0, critChance + amount));
                break;
            case "defense":
                defense += (int) amount;
                break;
            default:
                logger.warn("Invalid stat for removing debuff: {}", stat);
        }
        logger.debug("Monster debuff removed: {}", stat);
    }

    /**
     * Checks if the monster is still alive.
     *
     * @return true if the monster has HP remaining, false otherwise.
     */
    public boolean isAlive() {
        return currentHp > 0;
    }

    /**
     * Checks if the monster is at full health.
     *
     * @return true if current HP equals max HP, false otherwise.
     */
    public boolean isAtFullHealth() {
        return currentHp == maxHp;
    }

    /**
     * Resets the monster's health to maximum HP.
     */
    public void resetToFullHealth() {
        currentHp = maxHp;
        logger.debug("Monster health reset to max: {}", maxHp);
    }

    @Override
    public String toString() {
        return String.format("SimpleMonster[HP: %d/%d, ATK: %d, DEF: %d, Element: %s]",
                currentHp, maxHp, attack, defense, element);
    }

    /**
     * Gets a move by its index.
     *
     * @param index the index of the move.
     * @return the move at the specified index.
     */
    public Move getMove(int index) {
        return moves.get(index);
    }

    /**
     * Checks if the monster has a move at the specified index.
     *
     * @param index the index to check.
     * @return true if a move exists at the index, false otherwise.
     */
    public boolean hasMove(int index) {
        return moves.containsKey(index);
    }

    /**
     * Gets the maximum HP of the monster.
     *
     * @return the maximum HP.
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Gets the current HP of the monster.
     *
     * @return the current HP.
     */
    public int getCurrentHp() {
        return currentHp;
    }

    /**
     * Gets the attack stat of the monster.
     *
     * @return the attack value.
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Gets the defense stat of the monster.
     *
     * @return the defense value.
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Gets the evasion chance of the monster.
     *
     * @return the evasion chance (0.0 to 1.0).
     */
    public double getEvasionChance() {
        return evasionChance;
    }

    /**
     * Gets the critical hit chance of the monster.
     *
     * @return the critical hit chance (0.0 to 1.0).
     */
    public double getCritChance() {
        return critChance;
    }

    /**
     * Gets the element type of the monster.
     *
     * @return the element.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets all moves of the monster.
     *
     * @return a HashMap containing all moves indexed by their position.
     */
    public HashMap<Integer, Move> getMoves() {
        return moves;
    }

}
