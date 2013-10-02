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

import com.avapir.roguelike.Tile;

public class GamePanel extends JPanel {

	private GameWindow parentWindow;
	private Log log;
	private final int WIDTH_IN_TILES;
	private final int HEIGHT_IN_TILES;
	private KeyboardHandler listener;

	private static final Toolkit tKit = Toolkit.getDefaultToolkit();

	public static class Log {

		public Log() {
			messagesQueue = new LinkedList<String>();
		}

		public Deque<String> messagesQueue;

		public void write(String string) {
			messagesQueue.add(string);
			if (messagesQueue.size() > 15) {
				messagesQueue.poll();
			}
		}

	}

	public boolean hasTileOnScreen(int y, int x) {
		Game g = Game.getInstance();
		return (y >= g.Y() && y < HEIGHT_IN_TILES + g.Y())
				&& (x >= g.X() && x < WIDTH_IN_TILES + g.X());
	}

	public GamePanel(GameWindow window, int tilesX, int tilesY) {
		super();
		parentWindow = window;
		log = new Log();
		WIDTH_IN_TILES = tilesX;
		HEIGHT_IN_TILES = tilesY;
		addKeyListener(new KeyboardHandler());
		setFocusable(true);
	}

	public void drawLog(Graphics g) {
		// TODO Auto-generated method stub
	}

	private static final Color[] COLOR_SET = { Color.BLACK, Color.WHITE,
			Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.GRAY,
			Color.ORANGE, Color.YELLOW, Color.PINK };

	void drawColorString(Graphics g, String str, int lastX, int lastY) {
		Graphics2D g2 = (Graphics2D) g;
		FontRenderContext context = g2.getFontRenderContext();
		Font f = new Font("Serif", Font.PLAIN, 12);
		g2.setFont(f);
		Rectangle2D bounds;
		StringTokenizer st = new StringTokenizer(str, "#");
		String token;
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			if (token.equals("^")) {
				g2.setColor(Color.WHITE);
				continue;
			} else if (token.length() <= 2) {
				int col = Integer.parseInt(token);
				g2.setColor(COLOR_SET[col]);
				continue;
			}
			g2.drawString(token, lastX, lastY);
			bounds = f.getStringBounds(token, context);
			lastX += (bounds.getWidth());
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		System.err.println(4564);
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		paintBackground(g2);
		paintMap(g2, Game.getInstance().getCurrentMap());
		paintGUI(g2);
		paintLog(g2);
	}

	private void paintGUI(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	private void paintLog(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	private void paintMap(Graphics2D g2, Map map) {
		int ox = Game.getInstance().X();
		int oy = Game.getInstance().Y();
		int cV=0;
		int cI=0;
		for (int i = 0; i < HEIGHT_IN_TILES; i++) {
			for (int j = 0; j < WIDTH_IN_TILES; j++) {
				// indexes on the Map
				int x = ox + j;
				int y = oy + i;
				// pixels where to paint current Tile
				int xx = j * Tile.SIZE_px;
				int yy = i * Tile.SIZE_px;
				Tile tile = map.getTile(x, y);
				if (map.hasTile(x, y)) {
					if (tile.isVisible()) {
						cV++;
						paintTile(g2, tile, xx, yy);
					} else if (tile.isSeen()) {
						paintTile(g2, tile, xx, yy);
						drawImage(g2, tKit.getImage("res/sprite/wFog.png"), xx,
								yy);
					}

				} else {
					drawImage(g2, tKit.getImage("res/sprite/wFog.png"), xx, yy);
				}
			}
		}
		System.out.println(cV+" "+cI);
	}

	private void drawImage(Graphics g2, Image img, int xx, int yy) {
		g2.drawImage(img, xx, yy, this);
	}

	private void paintTile(Graphics2D g2, Tile tile, int xx, int yy) {
		if (tile.isEmpty()) {
			drawImage(g2, tKit.getImage("res/sprite/empty.png"), xx, yy);
		} else if (tile.isGrass()) {
			drawImage(g2, tKit.getImage("res/sprite/grass.png"), xx, yy);
		}
		if (tile.getCharacter() != null) {
			drawImage(g2, tKit.getImage("res/sprite/hero.png"), xx, yy);
		}
	}

	private void paintBackground(Graphics2D g2) {
		g2.setColor(Color.BLACK);
		Rectangle2D canvas = new Rectangle2D.Double(0, 0,
				parentWindow.getWindowWidth(), parentWindow.getWindowHeight());
		g2.fill(canvas);
	}

	public void drawGUI(Graphics g) {
		// TODO Auto-generated method stub

	}

}
