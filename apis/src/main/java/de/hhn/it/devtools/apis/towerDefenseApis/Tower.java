package de.hhn.it.devtools.apis.towerDefenseApis;

public interface Tower {

  /**
   * Attacks the enemy in range, that has advanced the furthest on the path
   * @return true, if the attack was successful, otherwise return false
   */
  public boolean attack();

}
