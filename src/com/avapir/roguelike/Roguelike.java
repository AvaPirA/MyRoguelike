package com.avapir.roguelike;

import com.avapir.roguelike.core.Game;

public class Roguelike {

	public static void main(String[] args) throws IllegalArgumentException,
			IllegalAccessException {
		Game game = new Game("MyRoguelike");
		game.init();
		game.start();
		game.done();
	}
}