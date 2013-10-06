package com.avapir.roguelike.battle;

public class Armor {

	private static final int TOTAL_DEF_TYPES = 6;
	
	private final float[] armor = new float[TOTAL_DEF_TYPES];

	public Armor(int... input) {
		if (input.length > TOTAL_DEF_TYPES) {
			throw new RuntimeException("Unknown armor type");
		}
		for (int i = 0; i < input.length; i++) {
			armor[i] = input[i];
		}
	}
	
	public Armor() {
		
	}
	
	public Armor addArmor(Armor def) {
		for(int i = 0; i<TOTAL_DEF_TYPES; i++) {
			armor[i] = def.armor[i];
		}
		return this;
	}

}
