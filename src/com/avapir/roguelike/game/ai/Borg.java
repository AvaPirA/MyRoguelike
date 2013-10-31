package com.avapir.roguelike.game.ai;

import java.awt.Point;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;
import com.avapir.roguelike.game.Tile;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Locatable;
import com.avapir.roguelike.locatable.Mob;

public final class Borg extends SmartAI {

	private Locatable	target;
	private Point		targetP;

	public String getTargetString() {
		if (target != null)
			return target.toString() + " " + String.format("(%s, %s)", targetP.x, targetP.y);
		else
			return String.format("(%s, %s)", targetP.x, targetP.y);
	}

	@Override
	public void computeAI(final Mob m, final Game g) {
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		if (g.getState() != GameState.GAME_OVER) {
			if (m == g.getHero()) {
				Hero h = g.getHero();
				final int fovRad = Hero.StatsFormulas.getFOVR(h);
				if (target!=null && !((Mob) target).isAlive()) {
					target = null;
				}
				if (target == null) {
					int x = 0;
					int y = 0;
					while (Math.abs(x) < fovRad && Math.abs(y) < fovRad) {
						Tile tile = g.getMap().getTile(x + m.getX(), y + m.getY());
						if (tile != null) {
							Mob t = tile.getMob();
							if (t != null && t != m) {
								target = t;
								break;
							}
						}
						if (x > y) {
							if (x > -y) {
								y--;
							} else {
								x--;
							}
						} else {
							if (x < -y) {
								y++;
							} else {
								x++;
							}
						}
					}
				}

				if (target == null) {
					targetP = getRandomDirection();
				} else {
					targetP = getDirection(m.getLoc(), target.getLoc());
				}

				g.getKeyboardHandler().setBorgMove(targetP);
			}
		}
	}

	private static double getAngle(Point p1, Point p2) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		if (dx == 0) {
			if (dy == 0) { return 0; }
			if (dy > 0) { return Math.PI / 2; }
			if (dy < 0) { return Math.PI * 3 / 2; }
		}
		if (dx > 0) {
			if (dy == 0) {
				return 0;
			} else if (dy > 0) {
				return Math.atan(dy / dx);
			} else {
				return 2 * Math.PI + Math.atan(dy / dx);
			}
		}
		if (dx < 0) {
			if (dy == 0) {
				return Math.PI;
			} else if (dy > 0) {
				return Math.PI + Math.atan(dy / dx);
			} else {
				return Math.PI + Math.atan(dy / dx);
			}
		}
		throw new RuntimeException();
	}

	private static final class Points {
		private static final Point	Z	= new Point(0, 0);
		private static final Point	R	= new Point(1, 0);
		private static final Point	L	= new Point(-1, 0);
		private static final Point	U	= new Point(0, -1);
		private static final Point	D	= new Point(0, 1);
		private static final Point	RU	= new Point(R.x, U.y);
		private static final Point	RD	= new Point(R.x, D.y);
		private static final Point	LU	= new Point(L.x, U.y);
		private static final Point	LD	= new Point(L.x, D.y);
	}

	private static Point getDirection(Point from, Point to) {
		double pi = Math.PI;
		double a = getAngle(from, to);
		if (a <= pi / 8) { return Points.R; }
		if (a < 3 * pi / 8) { return Points.RD; }
		if (a < 5 * pi / 8) { return Points.D; }
		if (a < 7 * pi / 8) { return Points.LD; }
		if (a <= 9 * pi / 8) { return Points.L; }
		if (a < 11 * pi / 8) { return Points.LU; }
		if (a < 13 * pi / 8) { return Points.U; }
		if (a < 15 * pi / 8) { return Points.RU; }
		return Points.R;
	}

	@Override
	public void onDeath(Mob mob, Game g) {
		// TODO Auto-generated method stub

	}

	public static SmartAI getNewInstance() {
		return new Borg();
	}
}
