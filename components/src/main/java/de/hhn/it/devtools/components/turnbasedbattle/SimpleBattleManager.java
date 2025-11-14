package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;

public class SimpleBattleManager implements BattleManager {

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
    this.player1 = p1;
    this.player2 = p2;

    this.p1Monster = new SimpleMonster(m1);
    this.p2Monster = new SimpleMonster(m2);

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
  }

  @Override
  public boolean isBattleOver() {
    return battleOver;
  }

  @Override
  public int getTurnCount() {
    return turnCount;
  }

  @Override
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public SimpleMonster getCurrentMonster() {
    return currentMonster;
  }

  public SimpleMonster getOpponentMonster() {
    return opponentMonster;
  }

  @Override
  public Player getWinner() {
    if(!battleOver) {
      return null;
    }

    if(p1Monster.getCurrentHp() <= 0) {
      return player2;
    }
    if(p2Monster.getCurrentHp() <= 0) {
      return player1;
    }

    return null;
  }

  @Override
  public int executeTurn(int moveNumber) {
    if (!currentMonster.hasMove(moveNumber)) {
      throw new IllegalArgumentException("Invalid move number.");
    }

    Move selectedMove = currentMonster.getMove(moveNumber);

    switch (selectedMove.type()) {
      case ATTACK -> {
        // do damage
        opponentMonster.takeDamage(selectedMove, currentMonster);

        // Check for death
        if(!opponentMonster.isAlive()) {
          battleOver = true;

          if(currentPlayer == player1) {
            return 1; //Player 1 win
          } else {
            return 2; //Player 2 win
          }
        }
      }
      case BUFF -> currentMonster.buffMonster(selectedMove);
      case DEBUFF -> opponentMonster.debuffMonster(selectedMove);
      default -> throw new IllegalStateException("Unknown move type: " + selectedMove.type());
    }

    return 0; // no winner yet
  }

  @Override
  public void nextTurn() {
    if(battleOver) {
      throw new IllegalStateException("Battle is already over.");
    }

    // Switch the current player and monsters
    if(currentPlayer == player1) {
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
    // if same element → random
    if (p1Monster.getElement() == p2Monster.getElement()) {
      if (Math.random() < 0.50) {
        return player1;
      } else {
        return player2;
      }
    }

    if (isElementEffective(p1Monster, p2Monster)) {
      return player1;
    } else {
      return player2;
    }
  }

  public boolean isElementEffective(SimpleMonster currentMonster, SimpleMonster opponentMonster) {
    Element currentElement = currentMonster.getElement();
    Element opponentElement = opponentMonster.getElement();

    if(currentElement == Element.FIRE) {
      return opponentElement == Element.GRASS;
    } else if(currentElement == Element.WATER) {
      return opponentElement == Element.FIRE;
    } else if(currentElement == Element.GRASS) {
      return opponentElement == Element.WATER;
    }

    return false;
  }
}
