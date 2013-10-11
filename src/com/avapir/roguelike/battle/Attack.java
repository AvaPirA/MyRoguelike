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

	public static final int		TOTAL_DMG_TYPES	= 6;
	private final float[]		damage			= new float[TOTAL_DMG_TYPES];
	private final List<Effect>	effects			= new ArrayList<Effect>();

	public Attack(final float... input) {
		if (input.length > TOTAL_DMG_TYPES) { throw new IllegalArgumentException("Unknown damage type"); }
		for (int i = 0; i < input.length; i++) {
			damage[i] = input[i];
		}
	}

	public Attack addDamage(final int[] damages) {
		if (damages.length > TOTAL_DMG_TYPES) { throw new IllegalArgumentException(
				"Unknow damage type"); }

		for (int i = 0; i < damages.length; i++) {
			damage[i] += damages[i];
		}
		return this;
	}

	public Attack addEffect(final Effect eff) {
		if (eff == null) { return this; }

		effects.add(eff);
		return this;
	}

	public Attack addEffects(final List<Effect> eff) {
		if (eff == null) { return this; }

		effects.addAll(eff);
		return this;
	}

	public Attack addDamageFromAttack(final Attack atk) {
		if (atk == null) { return this; }

		for (int i = 0; i < TOTAL_DMG_TYPES; i++) {
			damage[i] += atk.damage[i];
		}
		return this;
	}

	public Attack addEffectsFromAttack(final Attack atk) {
		if (atk == null) { return this; }

		effects.addAll(atk.effects);
		return this;
	}

	public Attack addAttack(final Attack atk) {
		if (atk == null) { return this; }

		addDamageFromAttack(atk);
		addEffectsFromAttack(atk);
		return this;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public float getDamageOfType(final int index) {
		return damage[index];
	}

	public float[] getDamage() {
		return damage;
	}

	public Attack replaceBy(final Attack attack) {
		if (attack == null) { return this; }

		clear();
		addAttack(attack);
		return this;
	}

	private void clear() {
		for (int i = 0; i < TOTAL_DMG_TYPES; i++) {
			damage[i] = 0;
		}
		effects.clear();
	}

}
