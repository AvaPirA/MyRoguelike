package com.avapir.roguelike.locatable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.core.Game;

public class Hero extends Mob implements Locatable {

	private static final class DefaultStats {
		/* STR AGI VIT INT DEX LUK */
		static final int[] PLAYER = { 3, 3, 3, 3, 3, 1 };// 16
		static final int[] NPC = { 50, 100, 100, 50, 50, 10 };// 360
		static final int[] ELDER = { 290, 120, 390, 700, 400, 100 };// 2000
		static final int[] UNDEAD = { 120, 40, 120, 0, 40, 0 };// 320
	}

	public static class StatsFormulas {

		public static int getHP(Hero h) {
			return 1 + 4 * h.stats.getStr() + 7 * h.stats.getVit();
		}

		public static int getMP(Hero h) {
			return 1 + 7 * h.stats.getInt();
		}

		public static int addBonusXp(Hero h, double xp) {
			Random r = new Random();
			double L = r.nextInt(h.stats.getLuk() * 2);// up to 100% bonus for
														// each
														// 50 LUK
			return (int) (xp * (1d + h.stats.getDex() / 300d + L / 100d));
		}

		public static int getFOVR(Hero h) {
			return 5 + h.stats.getInt() / 50;
		}

		public static int getATKR(Hero h) {
			int AGI = h.stats.getAgi();
			return AGI == 0 ? 0 : AGI < 50 ? 1 : AGI < 150 ? 2 : 3;
		}

		public static Attack getAttack(Hero h) {
			int STR = h.stats.getStr();
			int DEX = h.stats.getDex();
			int INT = h.stats.getInt();
			int phys = STR + DEX * 4 / 10 + INT * 2 / 10;
			int magi = INT + DEX * 4 / 10;
			return new Attack(phys, magi);
		}

	}

	protected final class PrimaryStats {
		/**
		 * STRength <br>
		 * AGIlity <br>
		 * VITality <br>
		 * INTelligence <br>
		 * DEXterity <br>
		 * LUcK
		 */
		private static final int PRIMARY_STATS_AMOUNT = 6;
		private int[] values = new int[PRIMARY_STATS_AMOUNT];

		public int getStr() {
			return values[0];
		}

		public int getAgi() {
			return values[1];
		}

		public int getVit() {
			return values[2];
		}

		public int getInt() {
			return values[3];
		}

		public int getDex() {
			return values[4];
		}

		public int getLuk() {
			return values[5];
		}

		public PrimaryStats(Hero h) {
			if (name.contains("NPC")) {
				ac(DefaultStats.NPC);
			} else if (name.contains("Elder")) {
				ac(DefaultStats.ELDER);
			} else if (name.contains("Undead")) {
				ac(DefaultStats.UNDEAD);
			} else {
				ac(DefaultStats.PLAYER);
			}
		}

		private void ac(int[] a) {
			System.arraycopy(a, 0, values, 0, PRIMARY_STATS_AMOUNT);
		}
	}

	protected static final class HiddenStats {

		private final int[] values;

		public HiddenStats() {
			values = new int[2];
			setToDefault();
			// TODO Auto-generated constructor stub
		}

		protected void setToDefault() {
			values[0] = 5;
			values[1] = 1;
		}

		public int getFOVR() {
			return values[0];
		}

		public int getATKR() {
			return values[1];
		}
	}

	private class Inventory {

		private static final int SLOTS = 5;

		Inventory() {
			items = new ArrayList<>();
		}

		private final List<Item> items;
		private final int[] wearedItems = new int[SLOTS];
		private int storageWeight;

		ListIterator<Item> getIterator() {
			return items.listIterator();
		}

		// boolean isWeared(int index) {
		// boolean b = false;
		// for (int i = 0; i < SLOTS; i++) {
		// b |= (index == wearedItems[i]);
		// }
		// return b;
		// }

		Attack getDamage() {
			Attack atk = new Attack();
			for (int index : wearedItems) {
				atk.addDamage(items.get(index).getAttack());
			}
			return atk;
		}

		Armor getArmor() {
			Armor def = new Armor();
			for (int index : wearedItems) {
				def.addArmor(items.get(index).getArmor());
			}
			return def;
		}

		boolean isOverweighted() {
			// TODO formulas mechanics
			return storageWeight > 10 * stats.getStr();
		}

		boolean hasTooMuchItems() {
			// TODO formulas mechanics
			return items.size() > 3 * stats.getStr() / 2;
		}

	}

	public Hero(int x, int y, String n) {
		super(x, y, null, n);
		// TODO
		name = n;
		stats = new PrimaryStats(this);
		inventory = new Inventory();
	}

	private final String name;
	private final Inventory inventory;
	private final PrimaryStats stats;
	private int level;
	private int XP;
	private static final int[] XP_TO_LVL = { 0, 10, 15, 25, 40, 65, 105, 170,
			275, 445 };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean move(Point dp) {
		if (inventory.isOverweighted()) {
			Game.getInstanceLast().log("Вы #2#перегружены!#^#");
			return false;
		}
		if (inventory.hasTooMuchItems()) {
			Game.getInstanceLast().log("Вы несете #2#слишком много вещей!#^#");
			return false;
		}
		return super.move(dp);
	}

	public int getMaxWeight() {
		// TODO
		return 0;
	}

	public int getMaxInventoryCells() {
		// TODO
		return 0;
	}

	public int getMaxHP() {
		return 0;
	}

	@Override
	protected Armor getDefence() {
		return super.getDefence().addArmor(inventory.getArmor());
	}

	@Override
	protected Attack getAttack() {
		return super.getAttack().addDamage(inventory.getDamage());
	}

	public ListIterator<Item> getInventoryIterator() {
		return inventory.getIterator();
	}

	@Override
	public void doTurnEffects() {
		super.doTurnEffects();
		while (lvlUp()) {
			gainLvl();
		}
	}

	private boolean lvlUp() {
		return XP >= XP_TO_LVL[level - 1];
	}

	private void gainLvl() {
		// TODO

		level++;
	}
	
	public void gainXPfromDamage(float dmg) {
		int xp = (int) Math.pow(dmg, 4d/3d);
		XP+=StatsFormulas.addBonusXp(this, xp);
	}
	
}