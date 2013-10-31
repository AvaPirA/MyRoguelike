package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Hero;

public class ChangingStatsHandler extends AbstractStateHandler {

	public ChangingStatsHandler(Game g) {
		super(g);
		diff = new int[Hero.PrimaryStats.PRIMARY_STATS_AMOUNT];
	}

	private final int[]	diff;

	private int			freeDiff;

	public int getFreeDiff() {
		return freeDiff;
	}

	private int free() {
		return freeDiff + game.getHero().getStats().getFree();
	}

	@Override
	public void pressRight() {
		if (free() > 0) {
			int d = diff[y]++;
			diff[y] = checkRestoreX(diff[y]);
			if(d!=diff[y]){
				freeDiff--;
			}
			game.repaint();
		}
	}

	@Override
	public void pressLeft() {
		int d = diff[y]--;
		diff[y] = checkRestoreX(diff[y]);
		if (d != diff[y]) {
			freeDiff++;
		}
		game.repaint();
	}

	@Override
	protected int checkRestoreX(int x) {
		int stat = game.getHero().getStats().values(y);
		System.out.println(x + stat);
		if (x + stat == 301) {
			return x - 1;
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
		game.getHero().getStats().changeFreeBy(freeDiff);
	}

	public int[] getDiff() {
		return diff;
	}
}
