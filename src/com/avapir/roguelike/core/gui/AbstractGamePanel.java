package com.avapir.roguelike.core.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.avapir.roguelike.game.Tile;

public abstract class AbstractGamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	protected static final Toolkit	tKit				= Toolkit.getDefaultToolkit();
	protected static final int		SCREEN_WIDTH;
	protected static final int		SCREEN_HEIGHT;

	static {
		Dimension dim = tKit.getScreenSize();
		SCREEN_HEIGHT = (int) dim.getHeight();
		SCREEN_WIDTH = (int) dim.getWidth();
	}

	public static int getWidthInTiles() {
		return SCREEN_WIDTH / Tile.SIZE_px - 1 - 10;
	}

	public static int getHeightInTiles() {
		return SCREEN_HEIGHT / Tile.SIZE_px - 1;
	}

	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public static int getScreenHeight() {
		return SCREEN_HEIGHT;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		paintBackground(g2);
		paintGUI(g2);
	}

	public static float roundOneDigit(final float f) {
		return Math.round(10 * f) / 10f;
	}

	protected void drawString(final Graphics2D g2, final int xx, final int yy, final String s) {
		g2.drawString(s, xx, yy + 8);
	}

	protected void drawImage(final Graphics g2, final Image img, final int xx, final int yy) {
		g2.drawImage(img, xx, yy, this);
	}

	protected final void paintBackground(Graphics2D g2) {
		BufferedImage bgTex = toBufferedImage(getImage("seamless_wall04.jpg"));
		Rectangle2D canvas = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
		Rectangle2D tr = new Rectangle2D.Double(0, 0, bgTex.getWidth(), bgTex.getHeight());

		// Create the TexturePaint.
		TexturePaint tp = new TexturePaint(bgTex, tr);
		g2.setPaint(tp);
		g2.fill(canvas);
	}

	protected abstract void paintGUI(final Graphics2D g2);

	public static BufferedImage toBufferedImage(final Image img) {
		if (img instanceof BufferedImage) { return (BufferedImage) img; }
		BufferedImage bimage = null;
		try {
			bimage = new BufferedImage(img.getWidth(null), img.getHeight(null),
					BufferedImage.TYPE_INT_ARGB);
		} catch (final IllegalArgumentException e1) {
			return toBufferedImage(img);
		}
		final Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

	private static final String	path	= "res/sprite/";

	public Image getImage(String p) {
		return tKit.getImage(path.concat(p));
	}

}
