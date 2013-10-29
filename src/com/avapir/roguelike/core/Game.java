package com.avapir.roguelike.core;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.core.gui.GameWindow;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Hero.PrimaryStats;
import com.avapir.roguelike.locatable.Mob;

public class Game {

	public static boolean checkStep(final Point dp) {
		if (!((dp.x == 1 || dp.x == 0 || dp.x == -1) && (dp.y == 1 || dp.y == 0 || dp.y == -1))) {
			throw new IllegalArgumentException("Wrong step");
		} else {
			return !(dp.x == 0 && dp.y == 0);
		}
	}

	public class Log extends LinkedList<String> {

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;
		private int					oneTurnCounter;

		@Override
		public boolean add(final String s) {
			super.add("[" + oneTurnCounter + ":" + (turnCounter) + "] " + s);
			oneTurnCounter++;
			if (size() > 15 && oneTurnCounter <= 15) {
				super.poll();
			}
			return true;// so as super.add()
		}

		public void refresh() {
			oneTurnCounter = 0;
		}

	}

	private Log	gameLog	= new Log();

	public void log(final String s) {
		gameLog.add(s);
	}

	public Log getLog() {
		return gameLog;
	}

	private final List<Map>			maps	= new ArrayList<>();
	private final WindowsManager	winManager;

	private Hero					hero;
	private Map						currentMap;
	private int						currentX;
	private int						currentY;

	private List<Mob>				mobs;

	private int						turnCounter;

	public Game(final String t) {
		winManager = new WindowsManager(t, this);
		hero = new Hero(-1, -1, "Hero", currentMap);
	}

	public void start() {
		state = GameState.MOVE;
		// TODO потом надо поставить подходящий конструктор для карты
		final int firstMap = 0;
		maps.add(firstMap, new Map(this, 40, 40));
		switchToMap(firstMap);
		turnCounter = 0;
	}

	private void switchToMap(final int index) {

		if (maps.get(index) == null) {
			currentMap = new Map(this, 40, 40);
			maps.add(index, currentMap);
		} else {
			currentMap = maps.get(index);
		}
		setScreenCenterAt(currentMap.putCharacter(hero));
		placeMobsAndItems(index);

		EOT(new Point(0, 0));
	}

	public void done() {
		loadFonts();
		winManager.showGame(true);
	}

	private void loadFonts() {
		final Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)
				.createGraphics();
		g2.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		g2.drawString("asd", 0, 0);
	}

	private void doAIforAll() {
		hero.doAI(this);
		for (int i = 0; i < mobs.size(); i++) {
			mobs.get(i).doAI(this);
		}
	}

	public Hero getHero() {
		return hero;
	}

	public Map getMap() {
		return currentMap;
	}

	public int getTurnCounter() {
		return turnCounter;
	}

	public void init() {}

	public void move(final Point p) {
		if (checkStep(p)) {
			log("Перешел в [" + hero.getLoc().x + ", " + hero.getLoc().y + "]");
		}
		currentX += p.x;
		currentY += p.y;
	}

	public void repaint() {
		winManager.repaintGame();
	}

	public int X() {
		return currentX;
	}

	public int Y() {
		return currentY;
	}

	private void doTurnEffects() {
		hero.doTurnEffects();
		for (final Mob m : mobs) {
			m.doTurnEffects();
		}
	}

	private void placeMobsAndItems(final int scaler) {
		mobs = new LinkedList<>();

		final Random r = new Random();
		for (int x = 0; x < currentMap.getWidth(); x++) {
			for (int y = 0; y < currentMap.getHeight(); y++) {
				if (currentMap.hasTile(x, y) && r.nextInt(20) == 1) {
					mobs.add(Mob.MobSet.getSlime());
					currentMap.putCharacter(mobs.get(mobs.size() - 1), x, y);
				}
			}
		}
	}

	public Mob removeMob(final Mob m) {
		mobs.remove(m);
		return m;
	}

	private void setScreenCenterAt(final Point p) {
		currentX = p.x - AbstractGamePanel.getWidthInTiles() / 2;
		currentY = p.y - AbstractGamePanel.getHeightInTiles() / 2;
	}

	/**
	 * Обрабатывает всю хурму после хода игрока, если не выполнилось условие конца игры.
	 * 
	 * 
	 * @param mapMove
	 *            если герой сдвинулся, то нужно сдвинуть карту в том же направлении
	 */
	void EOT(final Point mapMove) {
		move(mapMove);
		// TODO SET GAME.BISY
		currentMap.computeFOV(hero.getLoc(), Hero.StatsFormulas.getFOVR(hero));
		doAIforAll();
		doTurnEffects();

		turnCounter++;
		gameLog.refresh();
		repaint();
	}

	/**
	 * Устанавливает состояния конца игры
	 */
	public void gameOver() {
		setGameState(GameState.GAME_OVER);
	}

	public static final class WindowsManager {
		private final GameWindow	gameWindow;

		// private final NewLevelWindow newLevelWindow;
		// private final InventoryWindow inventoryWindow;

		public WindowsManager(String title, Game game) {
			gameWindow = new GameWindow(title, game);
			gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// newLevelWindow = new NewLevelWindow(game);
			// newLevelWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			//
			// inventoryWindow = new InventoryWindow(game);
			// inventoryWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}

		public void repaintGame() {
			gameWindow.repaint();
		}

		public void showGame(boolean visibility) {
			gameWindow.setVisible(visibility);
		}

		// public void showInventory(boolean visibility) {
		// // TODO
		// // inventoryWindow.setVisible(visibility);
		// }
		//
		// public void showNewLevel(boolean visibility) {
		// // TODO
		// // newLevelWindow.setVisible(visibility);
		// }

	}

	public WindowsManager getWindowsManager() {
		return winManager;
	}

	public static enum GameState {
		MOVE, INVENTORY, CHANGE_STATS, GAME_OVER, DISTANCE_ATTACK, VIEW
	}

	private GameState	state;

	public GameState getState() {
		return state;
	}

	public void setGameState(GameState state) {
		if (this.state == state) {
			this.state = GameState.MOVE;
		} else {
			this.state = state;
		}
	}

	private ChangingStatsHandler	chs;

	public StateHandler getStatsHandler() {
		return chs;
	}

	public void createStatsHandler() {
		log("_____________________");
		log("Изменение характеристик:");
		log("Свободных хар-к: " + hero.getStats().getFreeAmount());
		chs = new ChangingStatsHandler(this);
	}

	public void removeStatsHandler() {
		log("Характеристики увеличились на:");
		String[] ss = PrimaryStats.STATS_STRINGS;
		log(ss[0] + ":" + (hero.getStats().values(0) - chs.getBuild()[0]) + ";              "
				+ ss[1] + ":" + (hero.getStats().values(1) - chs.getBuild()[1]));
		log(ss[2] + ":" + (hero.getStats().values(2) - chs.getBuild()[2]) + ";              "
				+ ss[3] + ":" + (hero.getStats().values(3) - chs.getBuild()[3]));
		log(ss[4] + ":" + (hero.getStats().values(4) - chs.getBuild()[4]) + ";             "
				+ ss[5] + ":" + (hero.getStats().values(5) - chs.getBuild()[5]));
		log("__________________________");
		chs = null;
	}

	private InventoryHandler ih;
	
	public InventoryHandler getInventoryHandler() {
		return ih;
	}
	
	public void createInventoryHandler(){
		//TODO log
		ih = new InventoryHandler(this);
	}
	
	public void removeInventoryHandler(){
		//TODO log
		ih = null;
	}
	
}
