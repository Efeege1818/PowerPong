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

  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(SelectScreenMonsterAdapter.class);

  private final SimpleMonster simpleMonster;

  public SelectScreenMonsterAdapter(Monster monster) {
    logger.debug("SelectScreenMonsterAdapter: creating adapter for monster element={}",
        monster.element());
    // Create a SimpleMonster from the API Monster for initial display
    this.simpleMonster = SimpleMonster.create(monster);
  }

  @Override
  public String getName() {
    logger.debug("getName: returning {}", simpleMonster.getName());
    return simpleMonster.getName();
  }

  @Override
  public int getMaxHp() {
    logger.debug("getMaxHp: returning {}", simpleMonster.getMaxHp());
    return simpleMonster.getMaxHp();
  }

  @Override
  public int getCurrentHp() {
    logger.debug("getCurrentHp: returning {}", simpleMonster.getCurrentHp());
    return simpleMonster.getCurrentHp();
  }

  @Override
  public int getAttack() {
    logger.debug("getAttack: returning {}", simpleMonster.getAttack());
    return simpleMonster.getAttack();
  }

  @Override
  public int getDefense() {
    logger.debug("getDefense: returning {}", simpleMonster.getDefense());
    return simpleMonster.getDefense();
  }

  @Override
  public Element getElement() {
    logger.debug("getElement: returning {}", simpleMonster.getElement());
    return simpleMonster.getElement();
  }

  @Override
  public HashMap<Integer, Move> getMoves() {
    logger.debug("getMoves: returning {} moves", simpleMonster.getMoves().size());
    return simpleMonster.getMoves();
  }

  @Override
  public Move getMove(int moveIndex) {
    logger.debug("getMove: moveIndex={}, returning {}", moveIndex,
        simpleMonster.getMove(moveIndex) != null ? simpleMonster.getMove(moveIndex).name() : "null");
    return simpleMonster.getMove(moveIndex);
  }

  @Override
  public boolean hasMove(int moveIndex) {
    logger.debug("hasMove: moveIndex={}, returning {}", moveIndex,
        simpleMonster.hasMove(moveIndex));
    return simpleMonster.hasMove(moveIndex);
  }

  @Override
  public boolean isMoveOnCooldown(int moveIndex) {
    logger.debug("isMoveOnCooldown: moveIndex={}, returning {}", moveIndex,
        simpleMonster.isMoveOnCooldown(moveIndex));
    return simpleMonster.isMoveOnCooldown(moveIndex);
  }

  @Override
  public int getRemainingCooldown(int moveIndex) {
    logger.debug("getRemainingCooldown: moveIndex={}, returning {}", moveIndex,
        simpleMonster.getRemainingCooldown(moveIndex));
    return simpleMonster.getRemainingCooldown(moveIndex);
  }

  @Override
  public boolean isMoveLocked(int moveIndex) {
    logger.debug("isMoveLocked: moveIndex={}, returning {}", moveIndex,
        simpleMonster.isMoveLocked(moveIndex));
    return simpleMonster.isMoveLocked(moveIndex);
  }

  @Override
  public String getSpecialProgress() {
    logger.debug("getSpecialProgress: returning {}", simpleMonster.getSpecialProgress());
    return simpleMonster.getSpecialProgress();
  }

  @Override
  public String getFocus() {
    logger.debug("getFocus: returning {}", simpleMonster.getFocus());
    return simpleMonster.getFocus();
  }

  @Override
  public String getPassiveInfo() {
    logger.debug("getPassiveInfo: returning {}", simpleMonster.getPassiveInfo());
    return simpleMonster.getPassiveInfo();
  }

  @Override
  public String getImagePath() {
    logger.debug("getImagePath: returning {}", simpleMonster.getImagePath());
    return simpleMonster.getImagePath();
  }
}

