package com.avapir.roguelike.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.avapir.roguelike.Character;
import com.avapir.roguelike.Item;
import com.avapir.roguelike.Tile;

public class Map {

	private static final Random random = new Random();
	private static final List<Long> usedSeeds = new ArrayList<>();

	private final int HEIGHT_MAP;
	private final int WIDTH_MAP;
	private final Tile[][] field;
	private String title;

	public int getHeight() {
		return HEIGHT_MAP;
	}

	public int getWidth() {
		return WIDTH_MAP;
	}

	public String getTitle() {
		return title;
	}

	public Tile getTile(int x, int y) {
		return field[y][x];
	}

	public boolean putCharacter(Character chr, int x, int y) {
		return field[y][x].putCharacter(chr);
	}

	public Character removeCharacter(int x, int y) {
		return field[y][x].removeCharacter();
	}

	public void dropItem(Item item, int x, int y) {
		field[y][x].dropItem(item);
	}

	public Map(int height, int width) {
		HEIGHT_MAP = height;
		WIDTH_MAP = width;
		field = new Tile[height][width];
		long newSeed = random.nextLong();
		while (usedSeeds.contains(newSeed)) {
			newSeed = random.nextLong();
		}
		title = "untitled";
		MapGenerator.generate(this, newSeed);
	}

	private static class MapGenerator {

		static void generate(Map map, long seed) {
			usedSeeds.add(seed);
		}
	}

	private boolean rangeCheck(int x, int y) {
		return (x >= 0 && x < WIDTH_MAP) && (y >= 0 && y < HEIGHT_MAP);
	}

	private void drawFOVLine(int x1, int y1, int x2, int y2) {
		int deltaX = Math.abs(x2 - x1);
		int deltaY = Math.abs(y2 - y1);
		int signX = x1 < x2 ? 1 : -1;
		int signY = y1 < y2 ? 1 : -1;
		int error = deltaX - deltaY;

		for (;;) {
			if (rangeCheck(x1, y1)) {
				field[x1][y1].setVisible(true);
				// field[x1][y1].lastseenID = field[x1][y1].getID();
			} else
				break;
			if (!field[x1][y1].isTransparent())
				break;

			if (x1 == x2 && y1 == y2)
				break;

			int error2 = error * 2;

			if (error2 > -deltaY) {
				error -= deltaY;
				x1 += signX;
			}

			if (error2 < deltaX) {
				error += deltaX;
				y1 += signY;
			}
		}
	}

	public void computeFOV(int x0, int y0, int radius) {
		for (int i = 0; i < HEIGHT_MAP; i++)
			for (int j = 0; j < WIDTH_MAP; j++)
				if (field[i][j].isVisible()) {
					field[i][j].setSeen(true);
					field[i][j].setVisible(false);
				}
		int x = 0;
		int y = radius;
		int delta = 2 - 2 * radius;
		int error = 0;
		while (y >= 0) {
			drawFOVLine(x0, y0, x0 + x, y0 + y);
			drawFOVLine(x0, y0, x0 + x, y0 - y);
			drawFOVLine(x0, y0, x0 - x, y0 + y);
			drawFOVLine(x0, y0, x0 - x, y0 - y);
			drawFOVLine(x0, y0, x0 + x - 1, y0 + y);
			drawFOVLine(x0, y0, x0 + x - 1, y0 - y);
			drawFOVLine(x0, y0, x0 - x, y0 + y - 1);
			drawFOVLine(x0, y0, x0 - x, y0 - y - 1);

			error = 2 * (delta + y) - 1;
			if (delta < 0 && error <= 0) {
				++x;
				delta += 2 * x + 1;
				continue;
			}
			error = 2 * (delta - x) - 1;
			if (delta > 0 && error > 0) {
				--y;
				delta += 1 - 2 * y;
				continue;
			}
			++x;
			delta += 2 * (x - y);
			--y;
		}

	}

}
