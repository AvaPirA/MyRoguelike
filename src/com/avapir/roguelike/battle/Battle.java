package com.avapir.roguelike.battle;

public class Battle {

	public static final float computeDamage(final Attack atk, final Armor arm) {
		// TODO
		float totalDamage = 0;
		totalDamage += atk.getDamageOfType(0) - arm.getArmor(0);
		totalDamage += atk.getDamageOfType(1) - arm.getArmor(1);
		return totalDamage;
	}

}
