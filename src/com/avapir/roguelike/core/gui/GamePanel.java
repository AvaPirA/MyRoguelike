package com.avapir.roguelike.core.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.core.KeyboardHandler;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;

public class GamePanel extends AbstractGamePanel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final Game			game;
	private final int					WIDTH_IN_TILES;
	private final int					HEIGHT_IN_TILES;

	public GamePanel(final Game g) {
		super();
		
		game = g;
		WIDTH_IN_TILES = getWidthInTiles();
		HEIGHT_IN_TILES = getHeightInTiles();
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
			drawString(g2, lastX, lastY, token);
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
		if (game.getState() == GameState.GAME_OVER) {
			final Image img = getImage("gameover.png");
			drawImage(g, img, (WIDTH_IN_TILES * Tile.SIZE_px - img.getWidth(null)) / 2,
					(HEIGHT_IN_TILES * Tile.SIZE_px - img.getHeight(null)) / 2);
		}
		// } else {
		// TODO BORG RUN PAINTING
		// }
	}

	@Override
	protected void paintGUI(final Graphics2D g2) {
		switch (game.getState()) {
		case MOVE:
			guiGame(g2);
		}
	}

	private void guiGame(final Graphics2D g2) {
		g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		final int guiOffH = HEIGHT_IN_TILES * Tile.SIZE_px / 2;
		final int guiOffW = WIDTH_IN_TILES * Tile.SIZE_px + 15;
		g2.setColor(Color.white);
		final Hero h = game.getHero();

		g2.setColor(Color.yellow);
		drawString(g2, guiOffW, guiOffH - 30, "X: " + h.getX());
		drawString(g2, guiOffW, guiOffH - 15, "Y: " + h.getY());
		drawString(g2, guiOffW, guiOffH, h.getName());
		drawString(g2, guiOffW + 50, guiOffH,
				String.format("Level [%s] (%s/%s)", h.getLevel(), h.getXP(), h.getAdvanceXP()));

		g2.setColor(Color.green);
		drawString(g2, guiOffW, guiOffH + 20,
				String.format("%s/%s", h.getHP(), Hero.StatsFormulas.getMaxHP(h)));

		g2.setColor(Color.blue);
		drawString(g2, guiOffW, guiOffH + 35,
				String.format("%s/%s", h.getMP(), Hero.StatsFormulas.getMaxMP(h)));

		g2.setColor(Color.red);
		for (int i = 0; i < Attack.TOTAL_DMG_TYPES; i++) {
			final float heroDmg = roundOneDigit(h.getAttack(i));
			final float pureDmg = roundOneDigit(((Mob) h).getAttack(i));
			final float itemDmg = heroDmg - pureDmg;
			drawString(g2, guiOffW, guiOffH + 60 + i * 15,
					String.format("%s + %s = %s", pureDmg, itemDmg, heroDmg));
		}

		g2.setColor(Color.orange);
		for (int i = 0; i < Armor.TOTAL_DEF_TYPES; i++) {
			final float heroDef = roundOneDigit(h.getArmor(i));
			final float pureDef = roundOneDigit(((Mob) h).getArmor(i));
			final float itemDef = heroDef - pureDef;
			drawString(g2, guiOffW + 150, guiOffH + 60 + i * 15,
					String.format("%s + %s = %s", pureDef, itemDef, heroDef));
		}
		g2.setColor(Color.yellow);
		final String[] stat = { "STR", "AGI", "VIT", "INT", "DEX", "LUK" };
		for (int i = 0; i < 6; i++) {
			drawString(g2, guiOffW, guiOffH + 170 + i * 15, stat[i] + "  " + h.getStats().values(i));
		}
	}

	private static Font	f	= new Font("Times New Roman", Font.PLAIN, 15);

	private void paintLog(final Graphics2D g2) {
		final int off = 15;
		g2.setFont(f);
		for (int i = 0; i < game.getLog().size(); i++) {
			g2.setColor(Color.white);
			drawString(g2, off + WIDTH_IN_TILES * Tile.SIZE_px, off + i * f.getSize() + 3, game
					.getLog().get(i));
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
					drawImage(g2, getImage("empty.png"), xx, yy);
					if (tile.isVisible()) {
						paintTile(g2, tile, xx, yy);
						// dS(g2, xx, yy, "vis");
					} else if (tile.isSeen()) {
						paintTile(g2, tile, xx, yy);
						drawImage(g2, getImage("wFog.png"), xx, yy);
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

	private void paintTile(final Graphics2D g2, final Tile tile, final int xx, final int yy) {
		switch (tile.getType()) {
		case GRASS:
			drawImage(g2, getImage("grass.png"), xx, yy);
			break;
		case TREE:
			drawImage(g2, getImage("tree.png"), xx, yy);
			break;
		default:
			drawImage(g2, getImage("empty.png"), xx, yy);
			break;
		}
		if (tile.isVisible() && tile.getMob() != null) {
			paintMob(tile.getMob(), g2, xx, yy);
		}
	}

	private void paintMob(final Mob mob, final Graphics2D g2, final int xx, final int yy) {
		if (mob == game.getHero()) {
			drawImage(g2, getImage("hero.png"), xx, yy);
		} else {
			drawImage(g2, getImage(String.format("%s.png", mob.getName().toLowerCase())), xx, yy);
		}
		if (mob == game.getHero()) {
			g2.setColor(new Color(0, 255, 0, 128));
		} else {
			g2.setColor(new Color(255, 0, 0, 128));
		}
		g2.fillRect(xx, yy, Tile.SIZE_px, 3);
		g2.fillRect(xx, yy, (int) (Tile.SIZE_px * mob.getHP() / mob.getMaxHp()), 3);

		g2.setColor(new Color(0, 128, 255, 128));
		g2.fillRect(xx, yy + 3, Tile.SIZE_px, 2);
		g2.fillRect(xx, yy + 3, (int) (Tile.SIZE_px * mob.getMP() / mob.getMaxMp()), 2);
	}

	private boolean hasTileOnScreen(final int y, final int x) {
		return y >= game.Y() && y < HEIGHT_IN_TILES + game.Y() && x >= game.X()
				&& x < WIDTH_IN_TILES + game.X();
	}

}
