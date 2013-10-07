package com.avapir.roguelike.game.ai;

import java.util.Random;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Mob;

public interface AI {

	static final Random	r	= new Random();

	public void computeAI(Mob m, Game g);

}
