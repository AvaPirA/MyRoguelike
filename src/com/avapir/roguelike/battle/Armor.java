package com.avapir.roguelike.battle;

/**
 * Physical<br>
 * Magic<br>
 * Water<br>
 * Fire<br>
 * Lightning<br>
 * Curse
 */
public class Armor {

	public static final int	TOTAL_DEF_TYPES	= 6;
	private final float[]		armor			= new float[TOTAL_DEF_TYPES];

	public Armor(final float... input) {
		if (input.length > TOTAL_DEF_TYPES) { throw new RuntimeException("Unknown armor type"); }
		for (int i = 0; i < input.length; i++) {
			armor[i] = input[i];
		}
	}

	public float getArmor(int index){
		return armor[index];
	}
	
	public Armor() {}

	public Armor addArmor(final Armor def) {
		for (int i = 0; i < TOTAL_DEF_TYPES; i++) {
			armor[i] = def.armor[i];
		}
		return this;
	}
	
}
