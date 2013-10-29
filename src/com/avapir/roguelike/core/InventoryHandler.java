package com.avapir.roguelike.core;


public class InventoryHandler extends AbstractStateHandler {

	public InventoryHandler(Game g) {
		super(g);
	}

	@Override
	protected int checkRestoreX(int x) {
		return x < 0 ? 0 : x > 2 ? 2 : x;
	}

	@Override
	protected int checkRestoreY(int y) {
		return y < 0 ? 0 : y > 3 ? 3 : y;
	}
}
