package de.hhn.it.devtools.apis.towerdefenseapi;

/**
 * Listener, that is given from the ViewModel to the Service.
 * The Service uses this Listener to notify the ViewModel, when Variables in the Service change.
 */
public interface TowerDefenseListener {

  /**
   *
   * Gets called, when the player Health changes.
   */
  void updateHealth();

  /**
   * Gets called, when the player Money changes.
   */
  void updateMoney();

  /**
   * Gets called, when the players health are reduced to zero.
   */
  void gameEnded();

  /**
   * Gets called, when the round is completed
   */
  void gameCompleted();

  /**
   * Gets called, when the TowerMap changes.
   */
  void updateMap();
}
