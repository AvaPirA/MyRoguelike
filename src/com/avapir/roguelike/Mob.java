package com.avapir.roguelike;

public abstract class Mob extends Character {
	
	public Mob(String n, HiddenStats s) {
		super(n, s);
	}

	protected abstract void chechAggroArea();
	
}
