package de.hhn.it.devtools.apis.turnbasedbattle;

public class FireMonster extends Monster {
  public FireMonster(int currentPlayer) {
    setMaxHp(500);
    setCurrentHp(getMaxHp());
    setAttack(0);
    setDefense(0);
    setElement(Element.FIRE);

    setCurrentPlayer(currentPlayer);
  }
}
