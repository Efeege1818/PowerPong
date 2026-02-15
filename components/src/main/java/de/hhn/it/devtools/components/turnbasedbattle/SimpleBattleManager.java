package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.BattleManager;
import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.apis.turnbasedbattle.move.MoveType;

/**
 * Class for managing and executing turns.
 */
public class SimpleBattleManager implements BattleManager {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleBattleManager.class);

  private Player player1;
  private Player player2;

  private SimpleMonster p1Monster;
  private SimpleMonster p2Monster;

  private Player currentPlayer;
  private SimpleMonster currentMonster;
  private SimpleMonster opponentMonster;

  private int turnCount;
  private boolean battleOver;

  @Override
  public void initializeBattle(Player p1, Player p2, Monster m1, Monster m2) {
    logger.info("SimpleBattleManager: initializeBattle, player1 = {}, player2 = {}, monster1 = {},"
        + " monster2 = {}", p1.playerId(), p2.playerId(), m1.element(), m2.element());
    // used element for monsters because name didn't work

    this.player1 = p1;
    this.player2 = p2;

    this.p1Monster = SimpleMonster.create(m1);
    this.p2Monster = SimpleMonster.create(m2);

    this.turnCount = 0;
    this.battleOver = false;

    // determine who starts
    this.currentPlayer = determineStartingPlayer();
    if (currentPlayer == player1) {
      this.currentMonster = p1Monster;
      this.opponentMonster = p2Monster;
    } else {
      this.currentMonster = p2Monster;
      this.opponentMonster = p1Monster;
    }
    logger.debug("Starting player: {} with monster {}", currentPlayer.playerId(), currentMonster);
  }

  @Override
  public boolean isBattleOver() {
    logger.debug("SimpleBattleManager: isBattleOver, battleOver = {}", battleOver);

    return battleOver;
  }

  @Override
  public int getTurnCount() {
    logger.debug("SimpleBattleManager: getTurnCount, turnCount = {}", turnCount);
    return turnCount;
  }

  @Override
  public Player getCurrentPlayer() {
    logger.debug(
            "SimpleBattleManager: getCurrentPlayer, currentPlayer = {}",
            currentPlayer.playerId()
    );
    return currentPlayer;
  }

  /**
   * Returns the runtime monster representing the current player's monster.
   *
   * @return the current SimpleMonster instance used in the battle
   */
  public SimpleMonster getCurrentMonster() {
    logger.debug(
            "SimpleBattleManager: getCurrentMonster, monster = {}",
            currentMonster.getName()
    );
    return currentMonster;
  }

  /**
   * Returns the runtime monster representing the opponent player's monster.
   *
   * @return the opponent SimpleMonster instance used in the battle
   */
  public SimpleMonster getOpponentMonster() {
    logger.debug(
            "SimpleBattleManager: getOpponentMonster, monster = {}",
            opponentMonster.getName()
    );

    return opponentMonster;
  }

  @Override
  public Player getWinner() {
    logger.debug("SimpleBattleManager: getWinner, battleOver = {}", battleOver);

    if (!battleOver) {
      return null;
    }

    if (p1Monster.getCurrentHp() <= 0) {
      return player2;
    }
    if (p2Monster.getCurrentHp() <= 0) {
      return player1;
    }

    return null;
  }

  @Override
  public int executeTurn(int moveNumber) {
    logger.info("SimpleBattleManager: executeTurn, player = {}, moveNumber = {}",
            currentPlayer.playerId(), moveNumber);

    if (!currentMonster.hasMove(moveNumber)) {
      throw new IllegalArgumentException("Invalid move number.");
    }

    // Check if move is locked
    if (currentMonster.isMoveLocked(moveNumber)) {
      logger.debug("Move {} is locked!", moveNumber);
      throw new IllegalStateException("Move is locked.");
    }

    // Check if move is on cooldown
    if (currentMonster.isMoveOnCooldown(moveNumber)) {
      int remaining = currentMonster.getRemainingCooldown(moveNumber);
      logger.debug("Move {} is on cooldown for {} more turn(s).", moveNumber, remaining);
      throw new IllegalStateException("Move is on cooldown for " + remaining + " more turn(s).");
    }

    // Tick all effects for both monsters
    p1Monster.tickAllEffects();
    p2Monster.tickAllEffects();

    Move selectedMove = currentMonster.getMove(moveNumber);

    logger.debug("Player {} executing move: {}",
            currentPlayer.playerId(), selectedMove.name());

    // Apply cooldown to the move
    currentMonster.applyCooldown(moveNumber, selectedMove);


    for (int count = 1; count <= selectedMove.executionCount(); count++) {
      logger.debug("Executing move {} of {}", count, selectedMove.executionCount());

      executeMove(selectedMove, selectedMove.type());

      // Check for death
      if (!opponentMonster.isAlive() || !currentMonster.isAlive()) {
        battleOver = true;

        if (currentPlayer == player1) {
          return 1; //Player 1 win
        } else {
          return 2; //Player 2 win
        }
      }
    }
    currentMonster.setTimesHitPoison(opponentMonster.getTimesHitByPoison());
    if (selectedMove.isSpecial() && !selectedMove.name().equals("Leaf Cannon")) {
      currentMonster.lockMove(moveNumber);
    }
    if (selectedMove.name().equals("Leaf Cannon")) {
      currentMonster.resetTimesHitPoison();
      opponentMonster.resetTimesHitPoison();
    }
    return 0; // no winner yet
  }

  private void executeMove(Move move, MoveType moveType) {
    logger.info("SimpleBattleManager: executeMove, move = {}, moveType = {}",
            move.name(), moveType);

    switch (moveType) {
      case MoveType.ATTACK -> {
        // Applies counterattack
        if (currentMonster.hasTakeDamageOnAttack() != null) {
          logger.debug("{} got counterattacked!", currentMonster.name);
          currentMonster.takeDamage(currentMonster.hasTakeDamageOnAttack(), opponentMonster);
        }

        // Do damage
        opponentMonster.takeDamage(move, currentMonster);
        if (move.followUpMove() != null) {
          executeMove(move.followUpMove(), move.followUpMove().type());
        }
      }
      case MoveType.BUFF -> {
        currentMonster.addBuffOrDebuff(move);
        if (currentMonster.hasTakeDamageOnAttack() != null) {
          currentMonster.removeTakeDamageOnAttack();
        }
      }
      case MoveType.DEBUFF -> {
        opponentMonster.addBuffOrDebuff(move);
        if (currentMonster.hasTakeDamageOnAttack() != null) {
          currentMonster.removeTakeDamageOnAttack();
        }
      }
      case MoveType.DOT -> {
        opponentMonster.addDot(move);
        if (currentMonster.hasTakeDamageOnAttack() != null) {
          currentMonster.removeTakeDamageOnAttack();
        }
      }
      case MoveType.STANCE -> {
        currentMonster.switchStance();
        if (currentMonster.hasTakeDamageOnAttack() != null) {
          currentMonster.removeTakeDamageOnAttack();
        }
      }
      case MoveType.COUNTERATTACK -> {
        opponentMonster.takeDamageOnAttack(move);
      }
      default -> throw new IllegalStateException("Unknown move type: " + move.type());
    }
  }

  @Override
  public void nextTurn() {
    logger.info("SimpleBattleManager: nextTurn, currentPlayer = {}, turnCount = {}",
            currentPlayer.playerId(), turnCount);

    if (battleOver) {
      throw new IllegalStateException("Battle is already over.");
    }

    // Switch the current player and monsters
    if (currentPlayer == player1) {
      currentPlayer = player2;
      currentMonster = p2Monster;
      opponentMonster = p1Monster;
    } else {
      currentPlayer = player1;
      currentMonster = p1Monster;
      opponentMonster = p2Monster;
    }

    turnCount++;
  }

  @Override
  public Player determineStartingPlayer() {
    logger.info("SimpleBattleManager: determineStartingPlayer");

    // if same element → random
    if (p1Monster.getElement() == p2Monster.getElement()) {
      if (Math.random() < 0.50) {
        return player1;
      } else {
        return player2;
      }
    }

    if (isElementEffective(p1Monster, p2Monster)) {
      return player2;
    } else {
      return player1;
    }
  }

  /**
   * Determines whether element is effective against opponent element.
   * Fire is effective against grass.
   * Water is effective against fire.
   * Grass is effective against water.
   *
   * @param currentMonster Current Monster
   * @param opponentMonster Opponent Monster
   * @return true if currentMonster's element is effective
   */
  public boolean isElementEffective(SimpleMonster currentMonster, SimpleMonster opponentMonster) {
    logger.debug("SimpleBattleManager: isElementEffective, currentElement = {}, "
            + "opponentElement = {}", currentMonster.getElement(), opponentMonster.getElement());

    Element currentElement = currentMonster.getElement();
    Element opponentElement = opponentMonster.getElement();

    if (currentElement == Element.FIRE) {
      return opponentElement == Element.GRASS;
    } else if (currentElement == Element.WATER) {
      return opponentElement == Element.FIRE;
    } else if (currentElement == Element.GRASS) {
      return opponentElement == Element.WATER;
    }

    return false;
  }

  public SimpleMonster getPlayer1Monster() {
    return p1Monster;
  }

  public SimpleMonster getPlayer2Monster() {
    return p2Monster;
  }
}
