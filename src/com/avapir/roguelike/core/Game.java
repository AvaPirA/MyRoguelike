package com.avapir.roguelike.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;

public class Game {

	public static void checkStep(final Point dp) {
		assert !(dp.x == 1 || dp.x == 0 || dp.x == -1) && (dp.y == 1 || dp.y == 0 || dp.y == -1);
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

	Log	gameLog	= new Log();

	public void log(final String s) {
		gameLog.add(s);
	}

	private final List<Map>		maps	= new ArrayList<>();
	private Map					currentMap;
	private int					currentX;
	private int					currentY;

	private final GameWindow	gameWindow;

	private Hero				hero;
	private List<Mob>			mobs;
	private final int			mobsAmountScaler;

	private int					turnCounter;
	private boolean				gameOver;

	public Game(final String t) {
		gameWindow = new GameWindow(t, this);
		mobsAmountScaler = 15;
	}

	public void start() {
		// TODO потом мб надо поставить подходящий конструктор для карты
		final int firstMap = 0;
		maps.add(firstMap, new Map(this, 200, 200));
		hero = new Hero(-1, -1, "Hero", currentMap);
		switchToMap(firstMap);
		turnCounter = 0;
	}

	private void switchToMap(final int index) {

		if (maps.get(index) == null) {
			currentMap = new Map(this, 200, 200);
			maps.add(index, currentMap);
		} else {
			currentMap = maps.get(index);
		}
		setScreenCenterAt(currentMap.putCharacter(hero));
		placeMobsAndItems(index);

		EOT(new Point(0,0));
	}

	public void done() {
		loadFonts();
		gameWindow.setVisible(true);
	}

	private void loadFonts() {
		final Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)
				.createGraphics();
		g2.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		g2.drawString("asd", 0, 0);
	}

	private void doAIforAll() {
		hero.doAI(this);
		for (final Mob mob : mobs) {
			mob.doAI(this);
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

	public void init() {
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.setBackground(Color.black);
	}

	public void move(final Point p) {
		checkStep(p);
		log("Перешел в [" + hero.getX() + ", " + hero.getY() + "]");
		currentX += p.x;
		currentY += p.y;
	}

	public void repaint() {
		gameWindow.repaint();
	}

	public int X() {
		return currentX;
	}

	public int Y() {
		return currentY;
	}

	private void checkGameOverConditions() {
		// if (hero.getHP() <= 0) {
		// System.out.println("LOL");
		// gameOver = true;
		// }
	}

	private void doTurnEffects() {
		hero.doTurnEffects();
		for (final Mob m : mobs) {
			m.doTurnEffects();
		}
	}

	private void placeMobsAndItems(final int scaler) {
		mobs = new LinkedList<>();
		mobs.add(Mob.MobSet.getSlime());
		currentMap.putCharacter(mobs.get(0), hero.getX() + 10, hero.getY() + 10);

		for (int i = 0; i < mobsAmountScaler * scaler; i++) {

		}
	}

	public Mob removeMob(final Mob m) {
		mobs.remove(m);
		return m;
	}

	private void setScreenCenterAt(final Point p) {
		currentX = p.x - GameWindow.getWidthInTiles() / 2;
		currentY = p.y - GameWindow.getHeightInTiles() / 2;
	}

	void EOT(Point p) {
		move(p);
		// TODO SET GAME.BISY
		if (gameOver) {
			return;
		}
		currentMap.computeFOV(hero.getX(), hero.getY(), Hero.StatsFormulas.getFOVR(hero));
		doAIforAll();
		doTurnEffects();
		checkGameOverConditions();
		turnCounter++;
		gameLog.refresh();
		gameWindow.repaint();
	}

	public boolean isOver(){
		return gameOver;
	}

}
