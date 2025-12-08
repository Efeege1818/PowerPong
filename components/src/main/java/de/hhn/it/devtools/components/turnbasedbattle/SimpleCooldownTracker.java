package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Move;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks per-monster, per-move cooldowns and blocks moves while they are on cooldown.
 */
public class SimpleCooldownTracker {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(SimpleCooldownTracker.class);

  /** Player 1 monster. */
  private final SimpleMonster player1Monster;

  /** Player 2 monster. */
  private final SimpleMonster player2Monster;

  /**
   * For each monster we track a map: moveIndex -> remaining cooldown turns.
   * remaining = 0 or missing => move is available.
   */
  private final Map<SimpleMonster, Map<Integer, Integer>> cooldowns = new HashMap<>();

  /**
   * Constructor: initializes internal maps for both monsters.
   *
   * @param p1Monster player 1 monster
   * @param p2Monster player 2 monster
   */
  public SimpleCooldownTracker(SimpleMonster p1Monster, SimpleMonster p2Monster) {
    this.player1Monster = p1Monster;
    this.player2Monster = p2Monster;

    cooldowns.put(p1Monster, new HashMap<>());
    cooldowns.put(p2Monster, new HashMap<>());
  }

  /**
   * Decrements cooldowns for the given monster at the start of its turn.
   * Cooldowns reaching 0 are removed (move wird wieder verfügbar).
   *
   * @param monster monster whose cooldowns should tick
   */
  public void tickCooldowns(SimpleMonster monster) {
    Map<Integer, Integer> current = cooldowns.get(monster);
    if (current == null || current.isEmpty()) {
      return;
    }

    Map<Integer, Integer> updated = new HashMap<>();

    for (Map.Entry<Integer, Integer> entry : current.entrySet()) {
      int moveIndex = entry.getKey();
      int remaining = entry.getValue();

      if (remaining > 0) {
        remaining -= 1;
        if (remaining > 0) {
          updated.put(moveIndex, remaining);
        }
        logger.debug("Cooldown ticked for move {} of {}: {} turns left",
            moveIndex, monster, remaining);
      }
    }

    cooldowns.put(monster, updated);
  }

  /**
   * Puts a move on cooldown after it has been used.
   * If move.cooldown() == 0, nothing is done.
   *
   * @param monster   the monster that used the move
   * @param moveIndex index of the move in the monster's move list
   * @param move      the move definition (for cooldown value)
   */
  public void applyCooldown(SimpleMonster monster, int moveIndex, Move move) {
    int cd = move.cooldown();
    if (cd <= 0) {
      return;
    }

    Map<Integer, Integer> current = cooldowns.get(monster);
    if (current == null) {
      current = new HashMap<>();
      cooldowns.put(monster, current);
    }

    current.put(moveIndex, cd);
    logger.debug("Applied cooldown {} to move {} of monster {}", cd, moveIndex, monster);
  }

  /**
   * Returns true if the given move is currently on cooldown (> 0 remaining turns).
   */
  public boolean isMoveOnCooldown(SimpleMonster monster, int moveIndex) {
    Map<Integer, Integer> current = cooldowns.get(monster);
    if (current == null) {
      return false;
    }
    Integer remaining = current.get(moveIndex);
    return remaining != null && remaining > 0;
  }

  /**
   * Returns remaining cooldown for the given move, or 0 if none.
   */
  public int getRemainingCooldown(SimpleMonster monster, int moveIndex) {
    Map<Integer, Integer> current = cooldowns.get(monster);
    if (current == null) {
      return 0;
    }
    return current.getOrDefault(moveIndex, 0);
  }
}
