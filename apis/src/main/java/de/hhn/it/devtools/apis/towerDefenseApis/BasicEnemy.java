package de.hhn.it.devtools.apis.towerDefenseApis;

public class BasicEnemy implements Enemy{

	//TODO Kommentare adden

	@Override
	public int getHealth() {
		return 0;
	}

	@Override
	public int getSpeed() {
		return 0;
	}

	@Override
	public Path getPath() {
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public boolean getDamage(int amount) {
		return false;
	}

	@Override
	public Coordinates move() {
		return null;
	}

	@Override
	public void endReached() {

	}
}
