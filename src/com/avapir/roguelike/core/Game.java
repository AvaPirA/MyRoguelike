package com.avapir.roguelike.core;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Mob;

public class Game {

	public static void checkStep(final Point dp) {
		assert !(dp.x == 1 || dp.x == 0 || dp.x == -1) && (dp.y == 1 || dp.y == 0 || dp.y == -1);
	}

	private List<Map>	maps	= new ArrayList<>();
	private Map			currentMap;
	private int			currentX;
	private int			currentY;

	private GameWindow	gameWindow;

	private Hero		hero;
	private List<Mob>	mobs;
	private final int	mobsAmountScaler;

	private int			turnCounter;
	private boolean		gameOver;

	public Game(final String t) {
		gameWindow = new GameWindow(t, this);
		mobsAmountScaler = 15;
	}

	public void start() {
		// TODO потом мб надо поставить подходящий конструктор для карты
		int firstMap = 0;
		maps.add(firstMap, new Map(200, 200));
		hero = new Hero(-1, -1, "Hero", currentMap);
		switchToMap(firstMap);
		turnCounter = 0;
	}

	public void done() {
		gameWindow.setVisible(true);
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

	public void log(final String s) {
		// TODO
	}

	public void move(final Point p) {
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
//		if (hero.getHP() <= 0) {
//			System.out.println("LOL");
//			gameOver = true;
//		}
	}

	private void doTurnEffects() {
		hero.doTurnEffects();
		for (final Mob m : mobs) {
			m.doTurnEffects();
		}
	}

	private void placeMobsAndItems(final int scaler) {
		mobs = new ArrayList<>(mobsAmountScaler * scaler);
		for (int i = 0; i < mobsAmountScaler * scaler; i++) {

		}
	}

	private void setScreenCenterAt(Point p) {
		currentX = p.x - GameWindow.getWidthInTiles() / 2;
		currentY = p.y - GameWindow.getHeightInTiles() / 2;
	}

	private void switchToMap(final int index) {

		if (maps.get(index) == null) {
			currentMap = new Map(200, 200);
			maps.add(index, currentMap);
		} else {
			currentMap = maps.get(index);
		}
		setScreenCenterAt(currentMap.putCharacter(hero));
		placeMobsAndItems(index);

		EOT();
	}

	void EOT() {
		// TODO SET GAME.BISY
		if (gameOver) { return; }
		currentMap.computeFOV(hero.getX(), hero.getY(), Hero.StatsFormulas.getFOVR(hero));
		doAIforAll();
		doTurnEffects();
		checkGameOverConditions();
		turnCounter++;
		gameWindow.repaint();
	}

}
