package com.avapir.roguelike.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Deque;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.game.Tile;

public class GamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	private final Game				game;
	private final GameWindow		parentWindow;
	// private Log log;
	private final int				WIDTH_IN_TILES;
	private final int				HEIGHT_IN_TILES;

	private static final Toolkit	tKit				= Toolkit.getDefaultToolkit();

	public static class Log {

		public Log() {
			messagesQueue = new LinkedList<String>();
		}

		public Deque<String>	messagesQueue;

		public void write(final String string) {
			messagesQueue.add(string);
			if (messagesQueue.size() > 15) {
				messagesQueue.poll();
			}
		}

	}

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

	public void drawLog(final Graphics g) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	private void paintLog(final Graphics2D g2) {
		// TODO Auto-generated method stub

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
					if (tile.isVisible()) {
						paintTile(g2, tile, xx, yy);
						dS(g2, xx, yy, "vis");
					} else if (tile.isSeen()) {
						paintTile(g2, tile, xx, yy);
						drawImage(g2, tKit.getImage("res/sprite/wFog.png"), xx, yy);
						dS(g2, xx, yy, "seen");
					} else {
						dS(g2, xx, yy, "invi");
					}
				} else {
					dS(g2, xx, yy, "notile");
				}
			}
		}
	}

	private void dS(final Graphics2D g2, final int xx, final int yy, String s) {
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
			dS(g2, xx, yy, "MOB");
			drawImage(g2, tKit.getImage("res/sprite/hero.png"), xx, yy);
		}
	}

	private void paintBackground(final Graphics2D g2) {
		g2.setColor(Color.BLACK);
		final Rectangle2D canvas = new Rectangle2D.Double(0, 0, parentWindow.getWindowWidth(),
				parentWindow.getWindowHeight());
		g2.fill(canvas);
	}

	public void drawGUI(final Graphics g) {
		// TODO Auto-generated method stub

	}

}
