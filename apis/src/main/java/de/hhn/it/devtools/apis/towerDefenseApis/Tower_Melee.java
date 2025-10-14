package de.hhn.it.devtools.apis.towerDefenseApis;

public class Tower_Melee implements Tower{
	@Override
	public int attack_speed() {
		return 0;
	}

	@Override
	public int cost() {
		return 0;
	}

	@Override
	public int range() {
		return 0;
	}

	@Override
	public Coordinates coords() {
		return null;
	}

	@Override
	public boolean attack() {
		return false;
	}
}
