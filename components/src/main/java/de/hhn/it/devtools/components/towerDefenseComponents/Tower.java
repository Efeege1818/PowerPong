package de.hhn.it.devtools.components.towerDefenseComponents;

import de.hhn.it.devtools.apis.towerDefenseApis.Coordinates;

public interface Tower {

  //TODO Kommentare adden

  int getId();
  int getAttackSpeed();
  int getPrice();
  int getRange();
  Coordinates getCoordinates();

  /**
   * Attacks the enemy in range, that has advanced the furthest on the path
   * @return true, if the attack was successful, otherwise return false
   */
  public boolean attack();

}
