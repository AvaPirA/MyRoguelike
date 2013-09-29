package com.avapir.roguelike.core;

import java.awt.Graphics;

public interface IGamePanel {

	abstract static class Log{
	
		public abstract void write();
	}
	
	public void drawLog(Graphics g);

	public void drawComponent(Graphics g);

	public void drawGUI(Graphics g);

}