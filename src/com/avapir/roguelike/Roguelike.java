package com.avapir.roguelike;

import com.avapir.roguelike.core.Game;

public class Roguelike {

	public static void main(String[] args) {
		 Game game = new Game();
		 game.init();
		 game.run();
		 game.done();
	}
}