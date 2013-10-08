package com.avapir.roguelike.battle;

public class Battle {

	public static final float computeDamage(final Attack atk, final Armor arm) {
		float totalDamage = 0;
		totalDamage += atk.getDamage(0) - arm.getArmor(0);
		totalDamage += atk.getDamage(1) - arm.getArmor(1);
		return totalDamage;
	}

}
