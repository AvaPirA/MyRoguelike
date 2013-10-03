package com.avapir.roguelike.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;
import com.avapir.roguelike.locatable.Mob.HiddenStats;
import com.avapir.roguelike.locatable.Mob.MobType;

public class Game {
	private final String title;

	public Game(String t) {
		title = t;
		mobsScaler = 15;
		instance = this;
		turnCounter = 0;
	}

	private static Game instance;

	public static Game getInstance() {
		return instance;
	}

	public void repaint() {
		gameWindow.repaint();
	}

	public void log(String s) {
		// TODO
	}

	private int turnCounter;

	private GameWindow gameWindow;

	private int currentX;
	private int currentY;

	public int X() {
		return currentX;
	}

	public int Y() {
		return currentY;
	}

	private Mob hero;
	private List<Mob> mobs;
	private final int mobsScaler;

	private Map currentMap;
	private List<Map> maps;

	public void init() {
		// TODO Auto-generated method stub

	}

	public Map getCurrentMap() {
		return currentMap;
	}

	public void start() {
		maps = new ArrayList<>();
		currentMap = new Map(200, 200);// TODO потом мб надо поставить
										// подходящий
		// конструктор
		maps.add(currentMap);
		gameWindow = new GameWindow(title);
		gameWindow.setVisible(true);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		switchToMap(0);
	}

	private void switchToMap(int index) {
		if (maps.get(index) == null) {
			currentMap = new Map(200, 200);//
			maps.add(index, currentMap);

			hero = new Hero(-1, -1, "Hero", new HiddenStats(),
					MobType.Player);
			Point p = currentMap.putCharacter(hero);
			currentX = p.x - GameWindow.getWidthInTiles() * 2;
			currentY = p.y - GameWindow.getHeightInTiles() * 2;
			p = null;
			placeMobsAndItems(index);
		} else {
			currentMap = maps.get(index);
			hero = new Hero(-1, -1, "Hero", new HiddenStats(),
					MobType.Player);
			Point p = currentMap.putCharacter(hero);
			currentX = p.x - GameWindow.getWidthInTiles() / 2;
			currentY = p.y - GameWindow.getHeightInTiles() / 2;
			p = null;
			placeMobsAndItems(index);

		}
		endOfTurn();
	}

	private void placeMobsAndItems(int scaler) {
		mobs = new ArrayList<>(mobsScaler * scaler);
		for (int i = 0; i < mobsScaler * scaler; i++) {

		}
	}

	void endOfTurn() {
		currentMap.computeFOV(hero.getX(), hero.getY(), hero.getHiddenStats()
				.getFOVR());
		gameWindow.repaint();
	}

	public void done() {
		// TODO Auto-generated method stub

	}

	public Mob getHero() {
		return hero;
	}

	public void move(Point p) {
		checkStep(p);
		
		if (p.x == -1) {
			if (hero.getX() > 14 && hero.getX() < 186) {
				currentX += p.x;
			}
		} else if (p.x == 1) {
			if (hero.getX() > 15 && hero.getX() < 187) {
				currentX += p.x;
			}
		}
		
		if (p.y == -1) {
			if (hero.getY() > 10 && hero.getY() < 190) {
				currentY += p.y;
			}
		} else if (p.y == 1) {
			if (hero.getY() > 11 && hero.getY() < 191) {
				currentY += p.y;
			}
		}
	}

	public static void checkStep(Point dp) {
		if (!(dp.x == 1 || dp.x == 0 || dp.x == -1)
				&& (dp.y == 1 || dp.y == 0 || dp.y == -1)) {
			throw new RuntimeException("Wrong step: " + dp);
		}
	}

	public void computeAI() {
		for (Mob c : mobs) {
			c.doAI();
		}
	}

}