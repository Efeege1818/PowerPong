package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.MonsterBattleState;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import java.util.HashMap;

/**
 * Adapter to convert a static Monster record to a MonsterBattleState.
 * Used for displaying monster information in SelectScreen where we only have the API Monster type.
 */
public class SelectScreenMonsterAdapter implements MonsterBattleState {

  private final SimpleMonster simpleMonster;

  public SelectScreenMonsterAdapter(Monster monster) {
    // Create a SimpleMonster from the API Monster for initial display
    this.simpleMonster = SimpleMonster.create(monster);
  }

  @Override
  public String getName() {
    return simpleMonster.getName();
  }

  @Override
  public int getMaxHp() {
    return simpleMonster.getMaxHp();
  }

  @Override
  public int getCurrentHp() {
    return simpleMonster.getCurrentHp();
  }

  @Override
  public int getAttack() {
    return simpleMonster.getAttack();
  }

  @Override
  public int getDefense() {
    return simpleMonster.getDefense();
  }

  @Override
  public Element getElement() {
    return simpleMonster.getElement();
  }

  @Override
  public HashMap<Integer, Move> getMoves() {
    return simpleMonster.getMoves();
  }

  @Override
  public Move getMove(int moveIndex) {
    return simpleMonster.getMove(moveIndex);
  }

  @Override
  public boolean hasMove(int moveIndex) {
    return simpleMonster.hasMove(moveIndex);
  }

  @Override
  public boolean isMoveOnCooldown(int moveIndex) {
    return simpleMonster.isMoveOnCooldown(moveIndex);
  }

  @Override
  public int getRemainingCooldown(int moveIndex) {
    return simpleMonster.getRemainingCooldown(moveIndex);
  }

  @Override
  public boolean isMoveLocked(int moveIndex) {
    return simpleMonster.isMoveLocked(moveIndex);
  }

  @Override
  public String getSpecialProgress() {
    return simpleMonster.getSpecialProgress();
  }

  @Override
  public String getFocus() {
    return simpleMonster.getFocus();
  }

  @Override
  public String getPassiveInfo() {
    return simpleMonster.getPassiveInfo();
  }

  @Override
  public String getImagePath() {
    return simpleMonster.getImagePath();
  }
}

