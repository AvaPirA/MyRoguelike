package com.avapir.roguelike;

import java.awt.Point;

import com.avapir.roguelike.core.Game;

public abstract class Character {

	protected final String name;
	protected final PrimaryStats primary;
	protected final HiddenStats hidStats;
	private int X;
	private int Y;

	private AI intel;
	private final CharacterType type;

	public Character(int x, int y, String n, HiddenStats s, AI ai,
			CharacterType t) {
		name = n;
		hidStats = s;
		if (ai == null) {
			intel = new AI() {
				@Override
				public void computeAI() {
				}
			};
		} else {
			intel = ai;
		}
		type = t;
		primary = new PrimaryStats(t);
		Game.getInstance().getCurrentMap().putCharacter(this, x, y);
	}

	public String getName() {
		return name;
	}

	public HiddenStats getHiddenStats() {
		return hidStats;
	}

	public abstract boolean move(Point dp);

	public static class PrimaryStats extends Stats {

		private static final class DFT {
			/* STR AGI VIT INT DEX LUK */
			static final int[] PLAYER = { 3, 3, 3, 3, 3, 1 };// 16
			static final int[] NPC = { 50, 100, 100, 50, 50, 10 };// 360
			static final int[] ELDER = { 290, 120, 390, 700, 400, 100 };// 2000
			static final int[] UNDEAD = { 120, 40, 120, 0, 40, 0 };// 320
		}

		/**
		 * STRength <br>
		 * AGIlity <br>
		 * VITality <br>
		 * INTelligence <br>
		 * DEXterity <br>
		 * LUcK
		 */
		private static final int PRIMARY_STATS_AMOUNT = 6;
		private CharacterType charType;

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

		public PrimaryStats(CharacterType t, int... StrAgiVitIntDexLuk) {
			charType = t;
			if (StrAgiVitIntDexLuk.length == 0) {
				setToDefault();
			} else if (StrAgiVitIntDexLuk.length != PRIMARY_STATS_AMOUNT) {
				throw new RuntimeException("Wrong stats init");
			} else {
				values = new int[PRIMARY_STATS_AMOUNT];
				for (int i = 0; i < values.length; i++) {
					values[i] = StrAgiVitIntDexLuk[i];
				}
			}
		}

		@Override
		protected void setToDefault() {
			values = new int[PRIMARY_STATS_AMOUNT];
			switch (charType) {
			case Player:
				arrcpy(DFT.PLAYER);
				break;
			case Elder:
				arrcpy(DFT.ELDER);
				break;
			case NPC:
				arrcpy(DFT.NPC);
			case Undead:
				arrcpy(DFT.UNDEAD);
				break;
			}
		}

		private void arrcpy(int[] a) {
			System.arraycopy(a, 0, values, 0, PRIMARY_STATS_AMOUNT);
		}
	}

	public static class HiddenStats extends Stats {

		@Override
		protected void setToDefault() {

		}

		public int getFOVR() {
			return 5;
		}
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public void setX(int x) {
		X = x;
	}

	public void setY(int y) {
		Y = y;
	}

	public void doAI() {
		intel.computeAI();
	}

}
