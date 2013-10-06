package com.avapir.roguelike.game.ai;

import java.awt.Point;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Mob;

public class SlimeAI implements AI {

	@Override
	public void computeAI(Mob m) {
		int x = m.getX();
		int y = m.getY();
		Point p = new Point(r.nextInt(2)-1, r.nextInt(2)-1);
		for (int i = x - 4; i < x + 4; i++) {
			for (int j = y - 4; j < y + 4; j++) {
				if (Game.getInstanceLast().getMap().getTile(i, j).getMob().mobID == 0) {
					p.setLocation(i < x ? -1 : 1, j < y ? -1 : 1);
				}
			}
		}
		m.move(p);
	}
}
