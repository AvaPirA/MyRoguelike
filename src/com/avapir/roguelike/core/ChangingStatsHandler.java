package com.avapir.roguelike.core;

import com.avapir.roguelike.locatable.Hero;

public class ChangingStatsHandler {

	private final Game	game;

	private int			cursor	= 0;
	private final int	length	= Hero.PrimaryStats.PRIMARY_STATS_AMOUNT;
	private final int[]	build	= new int[length];

	public ChangingStatsHandler(Game g) {
		game = g;
		System.arraycopy(game.getHero().getStats().getArray(), 0, build, 0, length);
	}

	private void check() {
		cursor = cursor > length ? length : cursor < 0 ? 0 : cursor;
		System.out.println(cursor);
	}

	public void pressDown() {
		cursor++;
		check();
		game.repaint();
	}

	public void pressUp() {
		cursor--;
		check();
		game.repaint();
	}

	public void pressLeft() {
		if (game.getHero().getStats().values(cursor) > build[cursor]) {
			game.getHero().getStats().decrease(cursor);
			game.getHero().recomputeStats();
			game.repaint();
		}
	}

	public void pressRight() {
		if (!game.getHero().getStats().isMaxed(cursor) && game.getHero().getStats().hasFreeStats()) {
			game.getHero().getStats().increase(cursor);
			game.getHero().recomputeStats();
			game.repaint();
		}
	}

	public int getCursor() {
		return cursor;
	}

}
