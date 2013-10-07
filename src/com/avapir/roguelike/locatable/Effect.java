package com.avapir.roguelike.locatable;

public abstract class Effect {

	/**
	 * false => this effect may be applied only on Hero
	 */
	private final String	name;
	private final boolean	forAll;
	protected int			level;
	private int				timeLeft;

	public Effect(final String n, final int lvl, final int t, final boolean f) {
		name = n;
		forAll = f;
		level = lvl;
		timeLeft = t;
	}

	public boolean isAppliedForAll() {
		return forAll;
	}

	public abstract void applyTo(Mob m);

	public abstract void onRemove(Mob m);

	/**
	 * Equality is about names. But less powerfull effects will be removed (on
	 * apply on someone) by more powerfull
	 */
	@Override
	public boolean equals(final Object o) {
		return o instanceof Effect && this.name.equals(((Effect) o).name);
	}

	public int getAndDecrementTime() {
		return timeLeft--;
	}

}
