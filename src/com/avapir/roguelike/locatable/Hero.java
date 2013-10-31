package com.avapir.roguelike.locatable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;
import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.core.Game.GameState;

public class Hero extends Mob implements Locatable {

	private static final class DefaultStats {//@formatter:off
		/* 	STR 	AGI 	VIT 	INT 	DEX 	LUK */
		static final int[]	PLAYER	= { 3, 		3, 		3000,	 	3, 		2, 		1 };	// 16
//		static final int[]	PLAYER	= { 280, 	170,	230,	90,		70,		47 };	// 887
		static final int[]	NPC		= { 50, 	100, 	100, 	50, 	50, 	10 };	// 360
		static final int[]	ELDER	= { 290, 	120,	390, 	700,	400, 	100 }; 	// 2000
		static final int[]	UNDEAD	= { 120, 	40, 	120, 	0, 		40, 	0 };	// 320
	}
//@formatter:on

	public static final class StatsFormulas {

		public static float getMaxHP(final Hero h) {
			final int baseHP = 2;
			final int STR = getStr(h);
			final int VIT = getVit(h);
			return baseHP + 4 * STR + 7 * VIT;
		}

		public static float getMaxMP(final Hero h) {
			final int baseMP = 1;
			final int INT = getInt(h);
			return baseMP + 7 * INT;
		}

		public static double addBonusXp(final Hero h, final double xp) {
			final Random r = new Random();
			final double L = r.nextInt(h.stats.getLuk() * 2);
			// up to 100% bonus for each 50 LUK
			return (int) (xp * (1f + h.stats.getDex() / 300f + L / 100f));
		}

		public static int getFOVR(final Hero h) {
			final int baseFOVR = 5;
			final int INT = getInt(h);
			return baseFOVR + INT / 50;
		}

		public static int getATKR(final Hero h) {
			final int AGI = getAgi(h);
			return AGI == 0 ? 0 : AGI < 50 ? 1 : AGI < 150 ? 2 : 3;
		}

		public static Attack getAttack(final Hero h) {
			float STR = getStr(h);
			float DEX = getDex(h);
			float INT = getInt(h);
			final float phy = 1.6f + STR + DEX * 0.4f + INT * 0.2f;
			final float mag = 1.2f + INT + DEX * 0.4f;
			System.out.println(STR + " " + DEX + " " + INT + " " + phy);
			return new Attack(phy, mag);
		}

		public static Armor getArmor(final Hero h) {
			final float STR = getStr(h);
			final float AGI = getAgi(h);
			final float INT = getInt(h);
			final float phy = AGI * 0.7f + STR * 0.3f;
			final float mag = INT * 0.5f;
			return new Armor(phy, mag);
		}

		private static int getStr(Hero h) {
			return getStat(h, 0);
		}

		private static int getAgi(Hero h) {
			return getStat(h, 1);
		}

		private static int getVit(Hero h) {
			return getStat(h, 2);
		}

		private static int getInt(Hero h) {
			return getStat(h, 3);
		}

		private static int getDex(Hero h) {
			return getStat(h, 4);
		}

		private static int getLuk(Hero h) {
			return getStat(h, 5);
		}

		private static int getStat(Hero h, int i) {
			int STAT = h.getStats().values(i);
			GameState s = h.game.getState();
			if (s == GameState.CHANGE_STATS) {
				STAT += h.game.getStatsHandler().getDiff()[i];
			}
			return STAT;
		}
	}

	public static final class PrimaryStats {

		private void ac(final int[] a) {
			System.arraycopy(a, 0, values, 0, PRIMARY_STATS_AMOUNT);
		}

		public static final String[]	STATS_STRINGS			= { "STR", "AGI", "VIT", "INT",
																		"DEX", "LUK" };
		/**
		 * STRength <br>
		 * AGIlity <br>
		 * VITality <br>
		 * INTelligence <br>
		 * DEXterity <br>
		 * LUcK
		 */
		public static final int			PRIMARY_STATS_AMOUNT	= STATS_STRINGS.length;
		public static final int			DEFAULT_STAT_INCREASE	= 5;
		public static final int			MAX_STAT_VALUE			= 300;

		private final int[]				values					= new int[PRIMARY_STATS_AMOUNT];
		private int						freeStats				= 0;

		//@formatter:off
		public PrimaryStats(final String name) {
			if (name.contains("NPC")) {				ac(DefaultStats.NPC);
			} else if (name.contains("Elder")) {	ac(DefaultStats.ELDER);
			} else if (name.contains("Undead")) {	ac(DefaultStats.UNDEAD);
			} else {								ac(DefaultStats.PLAYER);
			}
		}

		public int	 values(final int i) {return values[i];}
		public int[] getArray() {return values;}
		public int 	 getStr()   {return values[0];}
		public int 	 getAgi()   {return values[1];}
		public int 	 getVit()   {return values[2];}
		public int 	 getInt()   {return values[3];}
		public int 	 getDex()   {return values[4];}
		public int 	 getLuk()   {return values[5];}
		//@formatter:on

		public boolean isMaxed(int i) {
			return values[i] >= MAX_STAT_VALUE;
		}

		/**
		 * Used by default stats distribution by user
		 * 
		 * @param cursor
		 *            stat index
		 */
		public void decrease(int cursor) {
			decreaseBy(cursor, 1);
			freeStats++;
		}

		public void decreaseBy(int cursor, int value) {
			values[cursor] -= value;
		}

		/**
		 * Used by default stats distribution by user when he has free
		 * 
		 * @param cursor
		 *            stat index
		 */
		public void increase(int cursor) {
			if (freeStats > 0) {
				increaseBy(cursor, 1);
				freeStats--;
			}
		}

		public void increaseBy(int cursor, int value) {
			values[cursor] += value;
		}

		public boolean hasFreeStats() {
			return freeStats > 0;
		}

		public int getFree() {
			return freeStats;
		}

		public void changeFreeBy(int freeDiff) {
			freeStats += freeDiff;
		}
	}

	protected final class HiddenStats {

		public int getFOVR() {
			return StatsFormulas.getFOVR(Hero.this);
		}

		public int getATKR() {
			return StatsFormulas.getATKR(Hero.this);
		}
	}

	private final class Inventory {

		private static final int	SLOTS	= 5;

		Inventory() {}

		private final List<Item>	items		= new ArrayList<>();
		private final int[]			wearedItems	= new int[SLOTS];
		private int					storageWeight;

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

		Attack getAttack() {
			final Attack atk = new Attack();
			for (final int index : wearedItems) {
				if (index < items.size() - 1) {
					atk.addAttack(items.get(index).getAttack());
				}
			}
			return atk;
		}

		Armor getArmor() {
			final Armor def = new Armor();
			for (final int index : wearedItems) {
				if (index < items.size() - 1) {
					def.addArmor(items.get(index).getArmor());
				}
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

	private static final int[]	XP_TO_LVL	= { 0, 68, 295, 805, 1716, 3154, 5249, 8136, 11955,
			16851, 22978, 30475, 39516, 50261, 62876, 77537, 94421, 113712, 135596, 160266, 84495,
			95074, 107905, 123472, 142427, 165669, 194509, 231086, 279822, 374430, 209536, 248781,
			296428, 354546, 425860, 514086, 624568, 765820, 954872 };

	private final String		name;
	private final Inventory		inventory	= new Inventory();
	private final PrimaryStats	stats;
	private final Game			game;

	private int					level		= 1;
	private int					XP			= 0;

	public Hero(final int x, final int y, final String n, final Game g) {
		super(x, y, null, n, g.getMap());
		// TODO
		name = n;
		stats = new PrimaryStats(name);
		game = g;
		restore();
	}

	private void restore() {
		maxHP = Hero.StatsFormulas.getMaxHP(this);
		maxMP = Hero.StatsFormulas.getMaxMP(this);
		HP = maxHP;
		MP = maxMP;
		baseAttack.replaceBy(Hero.StatsFormulas.getAttack(this));
		baseArmor.replaceBy(Hero.StatsFormulas.getArmor(this));
	}

	public void updateStats() {
		float HPperc = HP / maxHP;
		float MPperc = MP / maxMP;

		maxHP = Hero.StatsFormulas.getMaxHP(this);
		maxMP = Hero.StatsFormulas.getMaxMP(this);
		HP = maxHP * HPperc;
		MP = maxMP * MPperc;
		baseAttack.replaceBy(Hero.StatsFormulas.getAttack(this));
		baseArmor.replaceBy(Hero.StatsFormulas.getArmor(this));
	}

	@Override
	public String getName() {
		return name;
	}

	public PrimaryStats getStats() {
		return stats;
	}

	@Override
	public Point move(final Point dp, final Game g) {
		if (inventory.isOverweighted()) {
			g.log("Вы #2#перегружены!#^#");
			return null;
		}
		if (inventory.hasTooMuchItems()) {
			g.log("Вы несете #2#слишком много вещей!#^#");
			return null;
		}

		return super.move(dp, g);
	}

	@Override
	public Armor getArmor() {
		return super.getArmor().addArmor(inventory.getArmor());
	}

	@Override
	public float getArmor(final int i) {
		return getArmor().getArmor(i);
	}

	@Override
	public Attack getAttack() {
		return super.getAttack().addAttack(inventory.getAttack());
	}

	@Override
	public float getAttack(final int i) {
		return getAttack().getDamageOfType(i);
	}

	public ListIterator<Item> getInventoryIterator() {
		return inventory.getIterator();
	}

	@Override
	public void doTurnEffects() {
		super.doTurnEffects();
	}

	private boolean lvlUp() {
		return XP >= XP_TO_LVL[level];
	}

	private void gainLvl(final Game g) {
		XP = 0;
		level++;
		stats.freeStats += PrimaryStats.DEFAULT_STAT_INCREASE;
		restore();
		g.log(String.format("%s достиг %s уровня!", name, level));
		g.getWindowsManager();
	}

	public void gainXPfromDamage(final float dmg, final Game g) {
		final int xp = (int) Math.pow(dmg, 6 / 5f);
		final int gainedXP = (int) StatsFormulas.addBonusXp(this, xp);
		XP += gainedXP;
		g.log(String.format("%s получает %s опыта", getName(), gainedXP));
		while (lvlUp()) {
			gainLvl(g);
		}

	}

	@Override
	protected void onDeath(final Game g) {
		HP = 0;
		g.gameOver();
		g.repaint();
	}

	public int getXP() {
		return XP;
	}

	public int getAdvanceXP() {
		return XP_TO_LVL[level];
	}

	public int getLevel() {
		return level;
	}

}
