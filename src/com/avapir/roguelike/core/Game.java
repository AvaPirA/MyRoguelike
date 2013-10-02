package com.avapir.roguelike.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.avapir.roguelike.Character;
import com.avapir.roguelike.Character.HiddenStats;
import com.avapir.roguelike.CharacterType;

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

	private Character hero;
	private List<Character> mobs;
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
		currentMap = new Map(200, 200);// TODO потом мб надо поставить подходящий
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
					CharacterType.Player);
			Point p = currentMap.putCharacter(hero);
			currentX = p.x - GameWindow.getWidthInTiles() * 2;
			currentY = p.y - GameWindow.getHeightInTiles() * 2;
			p = null;
			placeMobsAndItems(index);
		} else {
			currentMap = maps.get(index);
			hero = new Hero(-1, -1, "Hero", new HiddenStats(),
					CharacterType.Player);
			Point p = currentMap.putCharacter(hero);
			currentX = p.x - GameWindow.getWidthInTiles() * 2;
			currentY = p.y - GameWindow.getHeightInTiles() * 2;
			System.out.println(currentX);
			System.out.println(currentY);
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

	private void endOfTurn() {
		currentMap.computeFOV(hero.getX(), hero.getY(), hero.getHiddenStats()
				.getFOVR());
		gameWindow.repaint();
		System.out.println(turnCounter++);
	}

	public void done() {
		// TODO Auto-generated method stub

	}

	public Character getHero() {
		return hero;
	}

	public void move(Point p) {
		checkStep(p);
		currentX = p.x;
		currentY = p.y;
	}

	public static void checkStep(Point dp) {
		if (!(dp.x == 1 || dp.x == 0 || dp.x == -1)
				&& (dp.y == 1 || dp.y == 0 || dp.y == -1)) {
			throw new RuntimeException("Wrong step: "+dp);
		}
	}

	public void computeAI() {
		for (Character c : mobs) {
			c.doAI();
		}
	}

}