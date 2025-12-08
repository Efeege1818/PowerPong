package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Move;
import de.hhn.it.devtools.apis.turnbasedbattle.MoveType;
import java.util.HashMap;
import java.util.Map;

/**
 * Tracks damage-over-time (DOT) effects for both monsters.
 *
 * <p>DOT effects are represented as {@link Move} instances of type {@link MoveType#DOT}.
 * The key of the inner map is the remaining duration in turns, the value is the Move.
 */
public class DotTracker {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(DotTracker.class);

  private final SimpleMonster player1Monster;
  private final SimpleMonster player2Monster;

  private final HashMap<SimpleMonster, HashMap<Integer, Move>> dots = new HashMap<>();
  private final HashMap<Integer, Move> dotsMonster1 = new HashMap<>();
  private final HashMap<Integer, Move> dotsMonster2 = new HashMap<>();

  /**
   * Initializes the DotTracker with the two monsters participating in the battle.
   * Each monster gets its own DOT map for tracking active damage-over-time effects.
   */
  public DotTracker(SimpleMonster player1Monster, SimpleMonster player2Monster) {
    this.player1Monster = player1Monster;
    this.player2Monster = player2Monster;
    dots.put(this.player1Monster, dotsMonster1);
    dots.put(this.player2Monster, dotsMonster2);
  }

  /**
   * Adds a damage-over-time (DOT) effect to the opponent of the current player.
   *
   * @param move             The move containing the DOT effect (with duration and description).
   * @param currentPlayerId  The ID of the player executing the move (1 or 2).
   */
  public void addDot(Move move, int currentPlayerId) {
    SimpleMonster target;
    if (currentPlayerId == 1) {
      target = player2Monster;
    } else {
      target = player1Monster;
    }

    dots.get(target).put(move.duration(), move);
    logger.debug("Player {} added DOT '{}' to monster {}",
        currentPlayerId, move.description(), target);
  }

  /**
   * Applies all active DOT effects for the acting monster when it performs
   * an ATTACK move. Duration is decreased only on such attacks.
   *
   * @param actingMonster the monster that is currently executing an ATTACK move
   */
  public void applyDotOnAttack(SimpleMonster actingMonster) {
    tickDotsForMonster(actingMonster);
  }

  private void tickDotsForMonster(SimpleMonster monster) {
    HashMap<Integer, Move> currentDots = dots.get(monster);
    if (currentDots == null || currentDots.isEmpty()) {
      return;
    }

    HashMap<Integer, Move> updated = new HashMap<>();

    for (Map.Entry<Integer, Move> entry : currentDots.entrySet()) {
      int duration = entry.getKey();
      Move move = entry.getValue();

      if (duration > 0) {

        if (!monster.isAlive()) {
          break;
        }

        int damage = (int) move.amount();
        if (damage > 0) {
          monster.takeDotDamage(damage);
          logger.debug("DOT '{}' deals {} damage to {} ({} turns left after this)",
              move.description(), damage, monster, duration - 1);
        }

        int newDuration = duration - 1;
        if (newDuration > 0) {
          updated.put(newDuration, move);
        } else {
          logger.debug("DOT '{}' expired on {}", move.description(), monster);
        }
      }
    }

    dots.put(monster, updated);
  }
}
