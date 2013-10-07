package com.avapir.roguelike.core;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

import com.avapir.roguelike.game.Tile;

/**
 * Стандартное окно. Умеет считать высоту\ширину игрового окна в тайлах. Знает
 * размер экрана в пикселях.
 * 
 * @author Alpen
 * 
 */
@SuppressWarnings("serial")
public class GameWindow extends javax.swing.JFrame {

	private static int		WINDOW_HEIGHT;
	private static int		WINDOW_WIDTH;
	private final JPanel	gamePanel;

	static {
		final Toolkit kit = Toolkit.getDefaultToolkit();
		final Dimension screenSize = kit.getScreenSize();
		WINDOW_HEIGHT = screenSize.height;
		WINDOW_WIDTH = screenSize.width;
	}

	public int getWindowWidth() {
		return WINDOW_WIDTH;
	}

	public int getWindowHeight() {
		return WINDOW_HEIGHT;
	}

	public static int getWidthInTiles() {
		return WINDOW_WIDTH / Tile.SIZE_px - 10;
	}

	public static int getHeightInTiles() {
		return WINDOW_HEIGHT / Tile.SIZE_px;
	}
	
	public GameWindow(final String title, final Game game) {
		setTitle(title);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setResizable(false);
		gamePanel = new GamePanel(game, this, getWidthInTiles(), getHeightInTiles());
		getContentPane().add(gamePanel);
	}

}
