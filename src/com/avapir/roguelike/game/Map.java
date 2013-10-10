package com.avapir.roguelike.game;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Item;
import com.avapir.roguelike.locatable.Mob;

public class Map {

	private static final Random		random		= new Random();

	/**
	 * Список уже использованных seed для генерации карт. Надо бы не
	 * повторяться!
	 */
	private static final List<Long>	usedSeeds	= new ArrayList<>();
	private static final int		DFT_HEIGHT	= 100;
	private static final int		DFT_WIDTH	= 100;
	private static final int		DFT_DELTA	= 20;					// percents

	private final Game				game;

	private final int				HEIGHT_MAP;
	private final int				WIDTH_MAP;
	private final Tile[][]			field;
	private final String			title;

	public int getHeight() {
		return HEIGHT_MAP;
	}

	public int getWidth() {
		return WIDTH_MAP;
	}

	public String getTitle() {
		return title;
	}

	public Tile getTile(final int x, final int y) {
		if (hasTile(x, y)) {
			return field[y][x];
		} else {
			return null;
		}
	}

	/**
	 * Ставит кого-то на определенные координаты, если возможно.
	 * 
	 * @param x
	 * @param y
	 * @return смог ли поставить
	 */
	public boolean putCharacter(final Mob chr, final int x, final int y) {
		if (hasTile(x, y)) {
			try {
				field[chr.getY()][chr.getX()].removeCharacter();
			} catch (final ArrayIndexOutOfBoundsException e) {
				// placing "from memory"
				// we will never get here since using if(hasTile(x, y))
			}
			chr.setLocation(x, y);
			return field[y][x].putCharacter(chr);
		} else {
			return false;
		}
	}

	/**
	 * Ищет позицию, куда можно поставить персонажа и ставит
	 * 
	 * @param c
	 *            кого ставить
	 * @throws RuntimeException
	 *             если не находит подходящий тайл за {@code ЧИСЛО_ТАЙЛОВ * 2}
	 */
	public Point putCharacter(final Mob c) {
		int x = random.nextInt(WIDTH_MAP), y = random.nextInt(HEIGHT_MAP);
		int counter = 0;
		final int maxCounter = HEIGHT_MAP * WIDTH_MAP * 4;// просто на всякий случай

		while (!putCharacter(c, x, y)) {
			x = random.nextInt(WIDTH_MAP);
			y = random.nextInt(HEIGHT_MAP);
			if (counter++ > maxCounter) { throw new RuntimeException(
					"Bad map: no place to put character"); }
		}
		return new Point(x, y);
	}

	/**
	 * Убирает персонажа с определенного тайла
	 * 
	 * @param x
	 * @param y
	 * @return кто стоял
	 */
	public Mob removeCharacter(final int x, final int y) {
		return game.removeMob(field[y][x].removeCharacter());
	}

	/**
	 * Кладет предмет на определенные координаты
	 * 
	 * @param item
	 * @param x
	 * @param y
	 */
	public void dropItem(final Item item, final int x, final int y) {
		field[y][x].dropItem(item);
	}

	@Deprecated
	public Map(final Game g, final int height, final int width) {
		game = g;
		HEIGHT_MAP = height;
		WIDTH_MAP = width;
		field = new Tile[height][width];
		title = "untitled";
		MapGenerator.generate(this);
	}

	public Map(final Game g) {
		game = g;
		final int deltaHeight = DFT_HEIGHT * DFT_DELTA / 100;
		final int deltaWidth = DFT_WIDTH * DFT_DELTA / 100;
		HEIGHT_MAP = DFT_HEIGHT + random.nextInt(2 * deltaHeight) - deltaHeight;
		WIDTH_MAP = DFT_WIDTH + random.nextInt(2 * deltaWidth) - deltaWidth;
		field = new Tile[HEIGHT_MAP][WIDTH_MAP];
		title = "untitled";
		MapGenerator.generate(this);
	}

	/**
	 * Заполняет пустую карту объектами. Вроде как должен генерировать в
	 * соответствии с полученных seed
	 */
	private static class MapGenerator {

		static void generate(final Map map) {
			long seed = random.nextLong();
			while (usedSeeds.contains(seed)) {
				seed = random.nextLong();
			}
			usedSeeds.add(seed);
			for (int i = 0; i < map.WIDTH_MAP; i++) {
				for (int j = 0; j < map.HEIGHT_MAP; j++) {
					if (random.nextInt(100) > 80) {
						map.field[j][i] = new Tile(Tile.Type.TREE);
					} else {
						map.field[j][i] = new Tile(Tile.Type.GRASS);
					}
				}
			}
		}
	}

	/**
	 * Проверяет, есть ли на этой карте тайл с такими координатами
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean hasTile(final int x, final int y) {
		return x >= 0 && x < WIDTH_MAP && y >= 0 && y < HEIGHT_MAP;
	}

	/**
	 * Спижжено
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	private void drawFOVLine(int x1, int y1, final int x2, final int y2) {
		final int deltaX = Math.abs(x2 - x1);
		final int deltaY = Math.abs(y2 - y1);
		final int signX = x1 < x2 ? 1 : -1;
		final int signY = y1 < y2 ? 1 : -1;
		int error = deltaX - deltaY;

		for (;;) {
			if (hasTile(x1, y1)) {
				field[y1][x1].setVisible(true);
				// field[x1][y1].lastseenID = field[x1][y1].getID();
			} else {
				break;
			}
			if (!field[y1][x1].isTransparent()) {
				break;
			}

			if (x1 == x2 && y1 == y2) {
				break;
			}

			final int error2 = error * 2;

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

	/**
	 * Спижженый алгоритм для рассчета Field Of View персонажа. Без понятия, что
	 * там происходит
	 * 
	 * @param x0
	 *            где стоим?
	 * @param y0
	 *            где стоим?
	 * @param radius
	 *            радиус видимости?
	 */
	public void computeFOV(final int x0, final int y0, final int radius) {
		for (int i = 0; i < HEIGHT_MAP; i++) {
			for (int j = 0; j < WIDTH_MAP; j++) {
				if (field[i][j].isVisible()) {
					field[i][j].setSeen(true);
					field[i][j].setVisible(false);
				}
			}
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

	public static float distance(final Point2D.Float p1, final Point2D.Float p2) {
		return (float) p1.distance(p2);
	}

	public List<Tile> getVisible() {
		List<Tile> list = new ArrayList<>();
		for (Tile[] tt : field) {
			for (Tile t : tt) {
				if (t.isVisible()) {
					list.add(t);
				}
			}
		}
		return list;
	}

}
