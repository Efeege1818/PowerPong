package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Move;

/**
 * Utility class for damage calculation.
 */
public class DamageCalculator {
    /**
     * Calculates the actual damage based on the move, critical hit, and elemental effectiveness.
     *
     * @param move the move being executed.
     * @param target the monster being targeted.
     * @param attacker the monster executing the move.
     * @param isCritical whether the attack is a critical hit.
     * @param isEffective whether the attack is effective against the target's element.
     * @return the actual damage done.
     */
    public static int calculateDamage(Move move, SimpleMonster target, SimpleMonster attacker, boolean isCritical, boolean isEffective) {
        double damage = move.amount() + attacker.getAttack();
        if (isCritical) {
            damage *= 1.5; // TODO: hardcoded critical multiplier
        }
        if (isEffective) {
            damage *= 1.5; // TODO: hardcoded effective multiplier
        }

        damage -= target.getDefense();

        if (damage < 0) {
            return 0;
        }

        return (int) Math.floor(damage);
    }


}
