package com.avapir.roguelike.core;

import com.avapir.roguelike.locatable.Hero;

public class ChangingStatsHandler extends AbstractStateHandler {

	public ChangingStatsHandler(Game g) {
		super(g);
		diff = new int[Hero.PrimaryStats.PRIMARY_STATS_AMOUNT];
	}

	private final int[]	diff;

	@Override
	public void pressRight() {
		diff[y]++;
		diff[y] = checkRestoreX(diff[y]);
		game.repaint();
	}

	@Override
	public void pressLeft() {
		diff[y]--;
		diff[y] = checkRestoreX(diff[x]);
		game.repaint();
	}

	@Override
	protected int checkRestoreX(int x) {
		int stat = game.getHero().getStats().values(y);
		if (x + stat > 300) {
			return 300 - stat;
		} else {
			return x;
		}
	}

	@Override
	protected int checkRestoreY(int y) {
		return y < 0 ? 0 : y > 5 ? 5 : y;
	}

	public void flush() {
		for (int i = 0; i < Hero.PrimaryStats.PRIMARY_STATS_AMOUNT; i++) {
			game.getHero().getStats().increaseBy(i, diff[i]);
		}
	}

	public int[] getDiff() {
		return diff;
	}
}
