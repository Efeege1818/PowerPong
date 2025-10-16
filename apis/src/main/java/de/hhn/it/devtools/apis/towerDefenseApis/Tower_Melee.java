package de.hhn.it.devtools.apis.towerDefenseApis;

public class Tower_Melee implements Tower{


	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getAttackSpeed() {
		return 0;
	}

	@Override
	public int getPrice() {
		return 0;
	}

	@Override
	public int getRange() {
		return 0;
	}

	@Override
	public Coordinates getCoordinates() {
		return null;
	}

	@Override
	public boolean attack() {
		return false;
	}
}
