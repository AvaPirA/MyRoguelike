package com.avapir.roguelike.core.statehandlers;

import java.awt.Point;

import com.avapir.roguelike.core.Game;

public abstract class AbstractStateHandler implements StateHandler {

	protected int	x;
	protected int	y;
	protected Game	game;

	public AbstractStateHandler(Game g) {
		game = g;
	}

	protected abstract int checkRestoreX(int x);

	protected abstract int checkRestoreY(int y);

	@Override
	public final void pressDown() {
		y++;
		y = checkRestoreY(y);
		game.repaint();
	}

	@Override
	public final void pressUp() {
		y--;
		y = checkRestoreY(y);
		game.repaint();
	}

	@Override
	public void pressLeft() {
		x--;
		x = checkRestoreX(x);
		game.repaint();
	}

	@Override
	public void pressRight() {
		x++;
		x = checkRestoreX(x);
		game.repaint();
	}

	@Override
	public Point getCursor() {
		return new Point(x, y);
	}

}
