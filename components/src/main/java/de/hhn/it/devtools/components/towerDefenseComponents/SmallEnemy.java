package de.hhn.it.devtools.components.towerDefenseComponents;

import de.hhn.it.devtools.apis.towerDefenseApis.Coordinates;
import de.hhn.it.devtools.apis.towerDefenseApis.EnemyType;
import de.hhn.it.devtools.components.towerDefenseComponents.Path;

public class SmallEnemy {

	//TODO Kommentare adden
  //TODO logik adden und EnemyTypes einfuehren


  public int getId() {
    return 0;
  }
  public int getSpeed() {
    return 0;
  }
	public int getHealth() {
		return 0;
	}
	public Path getPath() {
		return null;
	}
  public EnemyType getType() {
    return null;
  }
	public boolean damageEnemy(int amount) {
		return false;
	}
  public int damagePlayer() {
    return 0;
  }
	public Coordinates move() {
		return null;
	}
	public void endReached() {

	}
}
