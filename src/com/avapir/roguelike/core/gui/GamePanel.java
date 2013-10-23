package com.avapir.roguelike.core.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
	private final int			WIDTH_IN_TILES;
	private final int			HEIGHT_IN_TILES;

	private final GuiPainter	guiPainter;

	public GamePanel(final Game g) {
		super();

		game = g;
		WIDTH_IN_TILES = getWidthInTiles();
		HEIGHT_IN_TILES = getHeightInTiles();
		System.out.println(WIDTH_IN_TILES);
		System.out.println(HEIGHT_IN_TILES);
		guiPainter = new GuiPainter();
		addKeyListener(new KeyboardHandler(game));
		setFocusable(true);
	}

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
				g2.setColor(getDefaultStringColor(Integer.parseInt(token)));
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
		case DISTANCE_ATTACK:
		case GAME_OVER:
		case INVENTORY:
		case VIEW:
		case MOVE:
		case CHANGE_STATS:
			guiPainter.paint(g2);
		}
	}

	private class GuiPainter {

		private final Point	attackOffset	= new Point(0, 90);
		private final Point	armorOffset		= new Point(150, 0);
		private final Point	statsOffset		= new Point(-150, 110);
		private final Point	offset			= new Point(WIDTH_IN_TILES * Tile.SIZE_px + 15,
													HEIGHT_IN_TILES * Tile.SIZE_px / 2);
		private final Point	offsetDFT		= new Point(offset.x, offset.y);

		public void paint(final Graphics2D g2) {
			final Hero h = game.getHero();
			offset.move(offsetDFT.x, offsetDFT.y);
			heroInventory(g2, h);
			heroMainStats(g2, h);
			heroAttack(g2, h);
			heroArmor(g2, h);
			heroStats(g2, h);
		}

		private void heroInventory(Graphics2D g2, Hero h) {
			// TODO Auto-generated method stub

		}

		private void heroMainStats(final Graphics2D g2, final Hero hero) {
			g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
			g2.setColor(Color.yellow);
			// location
			System.out.println(offset.x + " " + offset.y);
			drawString(g2, offset.x, offset.y, "X: " + hero.getX());
			drawString(g2, offset.x, offset.y + 15, "Y: " + hero.getY());

			// name
			drawString(g2, offset.x, offset.y + 30, hero.getName());

			// level xp/XP
			drawString(
					g2,
					offset.x + 80,
					offset.y,
					String.format("Level [%s] (%s/%s)", hero.getLevel(), hero.getXP(),
							hero.getAdvanceXP()));

			// HP
			g2.setColor(Color.green);
			drawString(g2, offset.x, offset.y + 50,
					String.format("%s/%s", hero.getHP(), Hero.StatsFormulas.getMaxHP(hero)));

			// MP
			g2.setColor(Color.blue);
			drawString(g2, offset.x, offset.y + 65,
					String.format("%s/%s", hero.getMP(), Hero.StatsFormulas.getMaxMP(hero)));
		}

		private void heroAttack(final Graphics2D g2, final Hero hero) {
			g2.setColor(Color.red);

			offset.translate(attackOffset.x, attackOffset.y);

			for (int i = 0; i < Attack.TOTAL_DMG_TYPES; i++) {
				final float heroDmg = roundOneDigit(hero.getAttack(i));
				final float pureDmg = roundOneDigit(((Mob) hero).getAttack(i));
				final float itemDmg = heroDmg - pureDmg;
				drawString(g2, offset.x, offset.y + i * 15,
						String.format("%s + %s = %s", pureDmg, itemDmg, heroDmg));
			}
		}

		private void heroArmor(final Graphics2D g2, final Hero hero) {
			g2.setColor(Color.orange);
			offset.translate(armorOffset.x, armorOffset.y);

			for (int i = 0; i < Armor.TOTAL_DEF_TYPES; i++) {
				final float heroDef = roundOneDigit(hero.getArmor(i));
				final float pureDef = roundOneDigit(((Mob) hero).getArmor(i));
				final float itemDef = heroDef - pureDef;
				drawString(g2, offset.x, offset.y + i * 15,
						String.format("%s + %s = %s", pureDef, itemDef, heroDef));
			}
		}

		private void heroStats(final Graphics2D g2, final Hero hero) {
			g2.setColor(Color.yellow);

			offset.translate(statsOffset.x, statsOffset.y);

			final String[] stat = { "STR", "AGI", "VIT", "INT", "DEX", "LUK" };
			for (int i = 0; i < stat.length; i++) {
				drawString(g2, offset.x, offset.y + i * 15, stat[i] + "  "
						+ hero.getStats().values(i));
			}
		}

	}

	private void paintLog(final Graphics2D g2) {
		final Point offset = new Point(15, 15);
		g2.setFont(logFont);
		for (int i = 0; i < game.getLog().size(); i++) {
			g2.setColor(Color.white);
			drawString(g2, offset.x, offset.y + i * logFont.getSize() + 3, game.getLog().get(i));
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

//	private boolean hasTileOnScreen(final int y, final int x) {
//		return y >= game.Y() && y < HEIGHT_IN_TILES + game.Y() && x >= game.X()
//				&& x < WIDTH_IN_TILES + game.X();
//	}

}
