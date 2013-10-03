package com.avapir.roguelike.locatable;

import java.awt.Point;

import com.avapir.roguelike.game.ai.AI;

public class Beast extends Mob implements Locatable{

	public Beast(int x, int y, String n, HiddenStats s, AI ai, MobType t) {
		super(x, y, n, s, ai, t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean move(Point dp) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
