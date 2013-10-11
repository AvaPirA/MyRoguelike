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

	private final float[]	armor			= new float[TOTAL_DEF_TYPES];

	public Armor(final float... input) {
		if (input.length > TOTAL_DEF_TYPES) { throw new IllegalArgumentException("Unknown armor type"); }
		for (int i = 0; i < input.length; i++) {
			armor[i] = input[i];
		}
	}

	public float getArmor(final int index) {
		return armor[index];
	}

	public Armor addArmor(final int[] def) {//@formatter:off
		if (def == null) {return this;}
		if (def.length != TOTAL_DEF_TYPES) {throw new IllegalArgumentException("Unknown armor type");}//@formatter:on

		for (int i = 0; i < TOTAL_DEF_TYPES; i++) {
			armor[i] += def[i];
		}
		return this;
	}

	public Armor addArmor(final Armor def) {//@formatter:off
		if (def == null) {return this;}//@formatter:on
		for (int i = 0; i < TOTAL_DEF_TYPES; i++) {
			armor[i] = def.armor[i];
		}
		return this;
	}

	public Armor replaceBy(final Armor armor) {
		if (armor == null) { return this; }
		for (int i = 0; i < TOTAL_DEF_TYPES; i++) {
			this.armor[i] = armor.armor[i];
		}
		return this;
	}

}
