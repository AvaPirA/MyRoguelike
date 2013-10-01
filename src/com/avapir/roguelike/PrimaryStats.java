package com.avapir.roguelike;

public class PrimaryStats extends Stats {

	private static final class DFT {
		/*							 STR 	AGI		VIT		INT		DEX		LUK*/
		static final int[] PLAYER = {10,	8,		8,		6,		6,		2};
		static final int[] NPC =	{50,	100,	100,	50,		50,		10};
		static final int[] ELDER = 	{270,	120,	380,	700, 	400, 	100};
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
	
	{
		initStats(PRIMARY_STATS_AMOUNT);
	}
	
	public int getStr() {return values[0];}
	public int getAgi() {return values[1];}
	public int getVit() {return values[2];}
	public int getInt() {return values[3];}
	public int getDex() {return values[4];}
	public int getLuk() {return values[5];}
	
	@Override
	protected void setToDefault(CharacterType type) {
		switch (type) {
		case Player:
			System.arraycopy(DFT.PLAYER, 0, values, 0, PRIMARY_STATS_AMOUNT);
			break;
		case Elder:
			System.arraycopy(DFT.ELDER, 0, values, 0, PRIMARY_STATS_AMOUNT);
			break;
		default:
			values = new int[PRIMARY_STATS_AMOUNT];
			break;
		}
	}

}
