package com.avapir.roguelike.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;

public class GamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	private final Game				game;
	private final GameWindow		parentWindow;
	private final int				WIDTH_IN_TILES;
	private final int				HEIGHT_IN_TILES;

	private static final Toolkit	tKit				= Toolkit.getDefaultToolkit();

	public boolean hasTileOnScreen(final int y, final int x) {
		return y >= game.Y() && y < HEIGHT_IN_TILES + game.Y() && x >= game.X()
				&& x < WIDTH_IN_TILES + game.X();
	}

	public GamePanel(final Game g, final GameWindow window, final int tilesX, final int tilesY) {
		super();
		game = g;
		parentWindow = window;
		// log = new Log();
		WIDTH_IN_TILES = tilesX;
		HEIGHT_IN_TILES = tilesY;
		addKeyListener(new KeyboardHandler(game));
		setFocusable(true);
	}

	private static final Color[]	COLOR_SET	= { Color.BLACK, Color.WHITE, Color.RED,
			Color.GREEN, Color.BLUE, Color.CYAN, Color.GRAY, Color.ORANGE, Color.YELLOW, Color.PINK };

	void drawColorString(final Graphics g, final String str, int lastX, final int lastY) {
		final Graphics2D g2 = (Graphics2D) g;
		final FontRenderContext context = g2.getFontRenderContext();
		final Font f = new Font("Serif", Font.PLAIN, 12);
		g2.setFont(f);
		Rectangle2D bounds;
		final StringTokenizer st = new StringTokenizer(str, "#");
		String token;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equals("^")) {
				g2.setColor(Color.WHITE);
				continue;
			} else if (token.length() <= 2) {
				final int col = Integer.parseInt(token);
				g2.setColor(COLOR_SET[col]);
				continue;
			}
			dS(g2, lastX, lastY, token);
			bounds = f.getStringBounds(token, context);
			lastX += bounds.getWidth();
		}
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// if (!BORG) {
		final Graphics2D g2 = (Graphics2D) g;
		paintBackground(g2);
		paintMap(g2, game.getMap());
		paintGUI(g2);
		paintLog(g2);
		// } else {
		// TODO BORG RUN PAINTING
		// }
	}

	private void paintGUI(final Graphics2D g2) {
		g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		final int guiOffH = HEIGHT_IN_TILES * Tile.SIZE_px / 2;
		final int guiOffW = WIDTH_IN_TILES * Tile.SIZE_px + 15;
		g2.setColor(Color.white);
		final Hero h = game.getHero();

		g2.setColor(Color.yellow);
		dS(g2, guiOffW, guiOffH - 30, "X: " + h.getX());
		dS(g2, guiOffW, guiOffH - 15, "Y: " + h.getY());
		dS(g2, guiOffW, guiOffH, h.getName());

		g2.setColor(Color.green);
		dS(g2, guiOffW, guiOffH + 25, h.getHP() + "/" + Hero.StatsFormulas.getMaxHP(h));

		g2.setColor(Color.blue);
		dS(g2, guiOffW, guiOffH + 40, h.getMP() + "/" + Hero.StatsFormulas.getMaxMP(h));

		g2.setColor(Color.red);
		for (int i = 0; i < Attack.TOTAL_DMG_TYPES; i++) {
			final float heroDmg = roundOneDigit(h.getAttack(i));
			final float pureDmg = roundOneDigit(((Mob) h).getAttack(i));
			final float itemDmg = heroDmg - pureDmg;
			dS(g2, guiOffW, guiOffH + 60 + i * 15, pureDmg + " + " + itemDmg + " = " + heroDmg);
		}

		g2.setColor(Color.orange);
		for (int i = 0; i < Armor.TOTAL_DEF_TYPES; i++) {
			final float heroDef = roundOneDigit(h.getArmor(i));
			final float pureDef = roundOneDigit(((Mob) h).getArmor(i));
			final float itemDef = heroDef - pureDef;
			dS(g2, guiOffW + 150, guiOffH + 60 + i * 15, pureDef + " + " + itemDef + " = "
					+ heroDef);
		}
		g2.setColor(Color.yellow);
		final String[] stat = { "STR", "AGI", "VIT", "INT", "DEX", "LUK" };
		for (int i = 0; i < 6; i++) {
			dS(g2, guiOffW, guiOffH + 170 + i * 15, stat[i] + "  " + h.getStats().values(i));
		}
	}

	public static float roundOneDigit(final float f) {
		return Math.round(10 * f) / 10f;
	}

	private static Font	f	= new Font("Times New Roman", Font.PLAIN, 15);
	{}

	private void paintLog(final Graphics2D g2) {
		final int off = 15;
		g2.setFont(f);
		for (int i = 0; i < game.gameLog.size(); i++) {
			g2.setColor(Color.white);
			dS(g2, off + WIDTH_IN_TILES * Tile.SIZE_px, off + i * f.getSize() + 3,
					game.gameLog.get(i));
		}
	}

	private void paintMap(final Graphics2D g2, final Map map) {
		final int ox = game.X();
		final int oy = game.Y();
		for (int i = 0; i < HEIGHT_IN_TILES; i++) {
			for (int j = 0; j < WIDTH_IN_TILES; j++) {

				// indexes on the Map
				final int x = ox + j;
				final int y = oy + i;
				// pixels where to paint current Tile
				final int xx = j * Tile.SIZE_px;
				final int yy = i * Tile.SIZE_px;
				final Tile tile = map.getTile(x, y);
				g2.setColor(Color.red);
				if (map.hasTile(x, y)) {
					drawImage(g2, tKit.getImage("res/sprite/empty.png"), xx, yy);
					if (tile.isVisible()) {
						paintTile(g2, tile, xx, yy);
						// dS(g2, xx, yy, "vis");
					} else if (tile.isSeen()) {
						paintTile(g2, tile, xx, yy);
						drawImage(g2, tKit.getImage("res/sprite/wFog.png"), xx, yy);
						// dS(g2, xx, yy, "seen");
					} else {
						// dS(g2, xx, yy, "invi");
					}
				} else {
					// dS(g2, xx, yy, "notile");
				}
			}
		}
	}

	private void dS(final Graphics2D g2, final int xx, final int yy, final String s) {
		g2.drawString(s, xx, yy + 8);
	}

	private void drawImage(final Graphics g2, final Image img, final int xx, final int yy) {
		g2.drawImage(img, xx, yy, this);
	}

	private void paintTile(final Graphics2D g2, final Tile tile, final int xx, final int yy) {
		if (tile.isEmpty()) {
			drawImage(g2, tKit.getImage("res/sprite/empty.png"), xx, yy);
		} else if (tile.isGrass()) {
			drawImage(g2, tKit.getImage("res/sprite/grass.png"), xx, yy);
		}
		if (tile.getMob() != null) {
			paintMob(tile.getMob(),g2, xx, yy);
		}
	}

	private void paintMob(Mob mob, final Graphics2D g2, final int xx, final int yy) {
		drawImage(g2, tKit.getImage("res/sprite/hero.png"), xx, yy);
		
		if(mob == game.getHero()){
			g2.setColor(new Color(0,255,0,128));
		} else {
			g2.setColor(new Color(255,0,0,128));
		}
		g2.fillRect(xx, yy, Tile.SIZE_px, 3);
		g2.fillRect(xx, yy, (int)(Tile.SIZE_px*mob.getHP()/mob.getMaxHp()), 3);
		
		g2.setColor(new Color(0,128,255,128));
		g2.fillRect(xx, yy+3, Tile.SIZE_px, 2);
		g2.fillRect(xx, yy+3, (int)(Tile.SIZE_px*mob.getMP()/mob.getMaxMp()), 2);
	}

	public static BufferedImage toBufferedImage(final Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
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

	private void paintBackground(final Graphics2D g2) {
		// g2.setColor(Color.BLACK);
		final BufferedImage bgTexture = toBufferedImage(tKit
				.getImage("res/sprite/seamless_wall04.jpg"));
		final Rectangle2D canvas = new Rectangle2D.Double(0, 0, parentWindow.getWindowWidth(),
				parentWindow.getWindowHeight());
		final Rectangle2D tr = new Rectangle2D.Double(0, 0, bgTexture.getWidth(),
				bgTexture.getHeight());
		// Create the TexturePaint.
		final TexturePaint tp = new TexturePaint(bgTexture, tr);
		g2.setPaint(tp);
		g2.fill(canvas);
	}

}
