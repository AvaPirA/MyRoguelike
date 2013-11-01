package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;

public class InventoryHandler extends AbstractStateHandler {

	public InventoryHandler(final Game g) {
		super(g);
	}

	@Override
	protected int checkRestoreX(final int x) {
		return x < 0 ? 0 : x > 2 ? 2 : x;
	}

	@Override
	protected int checkRestoreY(final int y) {
		return y < 0 ? 0 : y > 3 ? 3 : y;
	}
}
