package com.avapir.roguelike;

import com.avapir.roguelike.core.IGame;

public interface ITimeBasedGame extends IGame {

	public void computeTurn();
	
	public void repaint();
	
}
