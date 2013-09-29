package com.avapir.roguelike;


public abstract class Character {

	private String name;
	private HiddenStats stats;
	
	public Character(String n, HiddenStats s) {
		name = n;
		stats = s;
	}
	
	public String getName() {
		return name;
	}
	
	public HiddenStats getHiddenStats() {
		return stats;
	}
	
	public abstract void computeAI();
	
	public abstract void move();
	
}
