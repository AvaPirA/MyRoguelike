package com.avapir.roguelike;

public abstract class Stats {

	protected int[] values;

	protected final void initStats(int size) {
		values = new int[size];
	}

	protected abstract void setToDefault(CharacterType type);

}