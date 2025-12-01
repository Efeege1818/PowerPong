package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.SelectScreen;
import java.util.List;

public class SimpleSelectScreen implements SelectScreen {
  private boolean selected1 = false;
  private boolean selected2 = false;
  private Monster p1Monster;
  private Monster p2Monster;

  @Override
  public void MonsterForP1(Monster monster) {
    p1Monster = monster;
    selected1 = true;
  }

  @Override
  public void MonsterForP2(Monster monster) {
    p2Monster = monster;
    selected2 = true;
  }

  @Override
  public List<Monster> getAvailableMonsters(List<Monster> monsters) {
    return monsters;
  }

  @Override
  public boolean isSelectionFinished() {
    return selected1 && selected2;
  }

  @Override
  public Monster getP1Monster() {
    return p1Monster;
  }

  @Override
  public Monster getP2Monster() {
    return p2Monster;
  }
}
