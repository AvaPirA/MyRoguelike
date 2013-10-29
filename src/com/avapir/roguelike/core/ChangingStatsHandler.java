package com.avapir.roguelike.core;

import java.awt.Point;

import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Hero.PrimaryStats;

public class ChangingStatsHandler implements StateHandler {

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
	}

	public int[] getBuild() {
		return build;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avapir.roguelike.core.StateHandler#pressDown()
	 */
	@Override
	public void pressDown() {
		cursor++;
		check();
		game.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avapir.roguelike.core.StateHandler#pressUp()
	 */
	@Override
	public void pressUp() {
		cursor--;
		check();
		game.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avapir.roguelike.core.StateHandler#pressLeft()
	 */
	@Override
	public void pressLeft() {
		if (game.getHero().getStats().values(cursor) > build[cursor]) {
			game.getHero().getStats().decrease(cursor);
			game.getHero().recomputeStats();
			game.getLog().add(
					game.getHero().getName() + " уменьшает " + PrimaryStats.STATS_STRINGS[cursor]
							+ " на 1");
			game.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avapir.roguelike.core.StateHandler#pressRight()
	 */
	@Override
	public void pressRight() {
		if (!game.getHero().getStats().isMaxed(cursor) && game.getHero().getStats().hasFreeStats()) {
			game.getHero().getStats().increase(cursor);
			game.getHero().recomputeStats();
			game.getLog().add(
					game.getHero().getName() + " увеличивает " + PrimaryStats.STATS_STRINGS[cursor]
							+ " на 1");
			game.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.avapir.roguelike.core.StateHandler#getCursor()
	 */
	@Override
	public Point getCursor() {
		return new Point(cursor, 0);
	}

}
