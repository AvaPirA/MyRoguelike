package com.avapir.roguelike.game.ai;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Mob;

public class IdleAI extends EasyAI {

	static {
		instance = new IdleAI();
	}

	@Override
	public void computeAI(Mob m, Game g) {}
	
	public static EasyAI getNewInstance(){
		return instance;
	}

}
