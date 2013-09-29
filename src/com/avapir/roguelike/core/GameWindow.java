package com.avapir.roguelike.core;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.avapir.roguelike.Tile;

@SuppressWarnings("serial")
public class GameWindow extends javax.swing.JFrame {
	
	public int WINDOW_HEIGHT;
	public int WINDOW_WIDTH;
	
	{
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		WINDOW_HEIGHT = screenSize.height;
		WINDOW_WIDTH = screenSize.width;
	}
	
	public int getScreenTileSizeX() {
		return (WINDOW_WIDTH / Tile.TILE_SIZE_px) - 10;
	}

	public int getScreenTileSizeY() {
		return (WINDOW_HEIGHT / Tile.TILE_SIZE_px - 1);
	}
	
	private GamePanel gamePanel = new GamePanel(this, new Map(υσÿο), getScreenTileSizeX(),
			getScreenTileSizeY());
	
	public GameWindow() {
		setTitle("MyRoguelike");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
	}

}
