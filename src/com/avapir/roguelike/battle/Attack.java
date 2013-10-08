package com.avapir.roguelike.battle;

import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.locatable.Effect;

/**
 * Physical<br>
 * Magic<br>
 * Water<br>
 * Fire<br>
 * Lightning<br>
 * Curse
 */
public class Attack {

	public static final int	TOTAL_DMG_TYPES	= 6;
	private final float[]		damage			= new float[TOTAL_DMG_TYPES];
	private final List<Effect>		effects			= new ArrayList<Effect>();

	public Attack(final float... input) {
		if (input.length > TOTAL_DMG_TYPES) { throw new RuntimeException("Unknown damage type"); }
		for (int i = 0; i < input.length; i++) {
			damage[i] = input[i];
		}
	}

	public Attack() {}

	public float getDamage(final int index){
		return damage[index];
	}
	
	public Attack addDamage(final Attack atk) {
		for (int i = 0; i < TOTAL_DMG_TYPES; i++) {
			damage[i] += atk.damage[i];
		}
		for (int i = 0; i < atk.effects.size(); i++) {
			effects.add(atk.effects.get(i));
		}
		return this;
	}

	public Attack addEffect(final Effect eff) {
		effects.add(eff);
		return this;
	}

	public List<Effect> getEffects() {
		return effects;
	}

}
