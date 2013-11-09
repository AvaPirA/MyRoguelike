package com.avapir.roguelike.locatable;

public abstract class Effect {

    private final String  name;
    private final boolean forAll;
    private final int     level;
    private       int     timeLeft;

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

    @Override
    public boolean equals(final Object o) {
        return o instanceof Effect && this.name.equals(((Effect) o).name);
    }

    public int getAndDecrementTime() {
        return timeLeft--;
    }

}
