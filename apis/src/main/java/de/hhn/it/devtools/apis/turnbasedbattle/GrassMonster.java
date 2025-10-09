package de.hhn.it.devtools.apis.turnbasedbattle;

public class GrassMonster extends Monster {
  public GrassMonster(int currentPlayer) {
    setMaxHp(500);
    setCurrentHp(getMaxHp());
    setAttack(0);
    setDefense(0);
    setElement(Element.GRASS);

    setCurrentPlayer(currentPlayer);
  }
}
