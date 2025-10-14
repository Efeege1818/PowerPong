package de.hhn.it.devtools.apis.towerDefenseApis;

public class Enemy_Huge implements Enemy{
	@Override
	public int health() {
		return 0;
	}

	@Override
	public int speed() {
		return 0;
	}

	@Override
	public Path path() {
		return null;
	}

	@Override
	public int id() {
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
