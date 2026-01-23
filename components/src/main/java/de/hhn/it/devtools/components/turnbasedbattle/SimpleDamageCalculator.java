package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;

/**
 * Utility class for damage calculation.
 */
public class SimpleDamageCalculator {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleDamageCalculator.class);

  private static final double critMultiplier = 1.5;
  private static final double effectiveMultiplier = 1.5;

  /**
   * Calculates the actual damage based on the move, critical hit, and elemental effectiveness.
   *
   * @param move the move being executed.
   * @param target the monster being targeted.
   * @param attacker the monster executing the move.
   * @param isCritical whether the attack is a critical hit.
   * @param isEffective whether the attack is effective against the target's element.
   * @param multiplier multiplies damage.
   * @return the actual damage done.
   */
  public static int calculateDamage(Move move, SimpleMonster target, SimpleMonster attacker,
                                    boolean isCritical, boolean isEffective, int multiplier) {
    double damage = move.amount() * multiplier + attacker.getAttack();

    if (isCritical && isEffective) {
      damage *= (critMultiplier + effectiveMultiplier - 1);
      logger.debug("Critical and effective hit!");
      BattleLog.post("Critical and effective hit!");
    } else if (isCritical) {
      damage *= critMultiplier;
      logger.debug("Critical hit!");
      BattleLog.post("Critical hit!");
    } else if (isEffective) {
      damage *= effectiveMultiplier;
      logger.debug("Effective hit!");
      BattleLog.post("Effective hit!");
    }

    if (!move.isTrueDamage()) {
      damage -= target.getDefense();
    }

    if (damage < 0) {
      damage = 0;
    } else {
      damage = Math.floor(damage * (1 - target.getDamageReduction()));
    }
    return (int) damage;
  }
}
