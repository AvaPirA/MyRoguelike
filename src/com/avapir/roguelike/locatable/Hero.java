package com.avapir.roguelike.locatable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.game.Map;

public class Hero extends Mob implements Locatable{

	public Hero(int x, int y, String n, HiddenStats s, MobType t) {
		super(x, y, n, s, null, t);
		//TODO
		inventory=new Inventory();
	}

	private class Inventory {

		public Inventory() {
			items = new ArrayList<>();
		}
		
		private List<Item> items;
		private int storageWeight;

		public boolean isOverweighted() {
			// TODO formulas mechanics
			return storageWeight > 10 * primary.getStr();
		}

		public boolean hasTooMuchItems() {
			// TODO formulas mechanics
			return items.size() > 3 * primary.getStr() / 2;
		}

	}

	private Inventory inventory;
	private SecondaryStats secStats;

	@Override
	public boolean move(Point dp) {
		Game.checkStep(dp);
		if(dp.x == 0 && dp.y ==0) {
			return false;
		}
		Game g = Game.getInstance();
		Map m = g.getCurrentMap();
		if (inventory.isOverweighted()) {
			g.log("Вы #2#перегружены!#^#");
			return false;
		}
		if (inventory.hasTooMuchItems()) {
			g.log("Вы несете #2#слишком много вещей!#^#");
			return false;
		}
		int ny = (getY() + dp.y);
		int nx = (getX() + dp.x);
		if (m.hasTile(nx, ny)) {
			// Character mob = m.getTile(nx, ny).getCharacter();
			// if (mob != null) {
			// attackMonster(mob);
			// } else if (!m.getTile(nx, ny).isPassable()
			// && m.getTile(nx, ny).isOpenable()) {
			// game.TryToOpenSomething(m, ny , nx, true);
			// } else if (m.getTile(nx, ny).isPassable()) {
			m.putCharacter(this, nx, ny);
			switch (m.getTile(nx, ny).getItemList().size()) {
			case 1:
				g.log("Здесь есть "
						+ m.getTile(nx, ny).getItemList().get(0).getName()
								.toLowerCase() + ".");
			case 0:
				break;
			default:
				g.log("Здесь лежит много вещей.");
			}
			// }
			Game.getInstance().repaint();
			return true;
		} else {
			return false;
		}
	}

	private void attackMonster(Mob mob) {
		// TODO Auto-generated method stub

	}

	public static class SecondaryStats extends Stats {

		@Override
		protected void setToDefault() {
			// TODO Auto-generated method stub

		}

	}

}
