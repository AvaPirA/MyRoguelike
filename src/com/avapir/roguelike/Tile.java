package com.avapir.roguelike;

import java.util.ArrayList;
import java.util.List;

public class Tile {

	public static final int TILE_SIZE_px = 35;

	private final Tile.Type initialType;
	private int flags;
	private Character charHere;
	private List<Item> itemsHere;

	public Tile(Tile.Type it) {
		initialType = it;
		flags = Tile.Type.example[it.ordinal()].flags;
		charHere = null;
		itemsHere = new ArrayList<>();
	}
	
	private Tile(int... flg) {
		initialType = null;
		for(int f : flg) {
			flags |= f;
		}
		charHere = null;
		itemsHere = new ArrayList<>();
		
	}

	public void restoreDefault() {
		if(initialType == null) {
			return;
		} else {
			flags = Tile.Type.example[initialType.ordinal()].flags;
			charHere = null;
			itemsHere = new ArrayList<>();
		}
	}
	
	public static enum Type {
		EMPTY, GRASS, TREE, CLOSED_DOOR, OPENED_DOOR, STAIR_UP, STAIR_DOWN, WALL;
		public static final Tile[] example = {
			
		};
	}

	public static final class Flag {
		public static final int FULL_FLAG 		= 0b11111111111111111111111111111111;
		public static final int EMPTY_FLAG		= 0b00000000000000000000000000000000;
		
												/*0-8 FOV*/
		public static final int F0 = 1<<0;//VISIBLE
		public static final int F1 = 1<<1;//SEEN
		public static final int F2 = 1<<2;//LIGHT_ON
		public static final int F3 = 1<<3;//TRANSPARENT
		public static final int F4 = 1<<4;
		public static final int F5 = 1<<5;
		public static final int F6 = 1<<6;
		public static final int F7 = 1<<7;
		public static final int F8 = 1<<8;
									/* 9-14 terrain and moving */
		public static final int F9 = 1<<9;//PASSABLE
		public static final int F10 = 1<<10;//EMPTY -- nothing
		public static final int F11	= 1<<11;//GRASS -- may heal/gain luck
		public static final int F12	= 1<<12;//STONES -- may fall(dont go and lose hp)
		public static final int F13	= 1<<13;//ICE -- may slip(lose hp, gain luck)
									/* 14-24 gaining effects */
		public static final int F14	= 1<<14;//POISIONING -- DoT, decreases dmg from lightning
		public static final int F15	= 1<<15;//FLAMING -- DoT, dramatically increases dmg from lightning
		public static final int F16	= 1<<16;//WET -- removes flame, increases dmg from lightning
		public static final int F17	= 1<<17;//INSTANT_DEATH -- you can avoid it only using smth expensive BEFORE IT (consumable, rare item, etc.)
		public static final int F18	= 1<<18;
		public static final int F19	= 1<<19;
		public static final int F20	= 1<<20;
		public static final int F21	= 1<<21;
		public static final int F22	= 1<<22;
		public static final int F23	= 1<<23;
									/* 24-31 specials */		
		public static final int F24	= 1<<24;
		public static final int F25	= 1<<25;
		public static final int F26	= 1<<26;
		public static final int F27	= 1<<27;
		public static final int F28	= 1<<28;//OPEN_DOOR
		public static final int F29	= 1<<29;//CLOSED_DOOR
		public static final int F30	= 1<<30;//UP_LADDER
		public static final int F31	= 1<<31;//DOWN_LADDER
		
	}
	
	private boolean checkFlag(int flag) {
		return flag == (flags & flag);
	}
	private void addFlags(int newFlag) {
		flags = flags | newFlag;
	}
	private void removeFlags(int newFlag) {
		flags = flags & invertFlag(newFlag);
	}
	private int invertFlag(int flag) {
		return Flag.FULL_FLAG ^ flag;
	}
	private void setFlag(boolean b, int flag) {
		if (b) {
			addFlags(flag);
		} else {
			removeFlags(flag);
		}
	}
	
	public boolean isVisible() 		{return checkFlag(Flag.F0);}
	public boolean isSeen() 		{return checkFlag(Flag.F1);}
	public boolean isLantern() 		{return checkFlag(Flag.F2);}
	public boolean isTransparent() 	{return checkFlag(Flag.F3);}
	
	public boolean isPassable() 	{return checkFlag(Flag.F9);}
	public boolean isEmpty()		{return checkFlag(Flag.F10);}
	
	public boolean isGrass() 		{return checkFlag(Flag.F11);}
	public boolean isStone() 		{return checkFlag(Flag.F12);}
	public boolean isIce() 			{return checkFlag(Flag.F13);}
	
	public boolean isPoisioning() 	{return checkFlag(Flag.F14);}
	public boolean isFlaming() 		{return checkFlag(Flag.F15);}
	public boolean isWet() 			{return checkFlag(Flag.F16);}
	public boolean isInstantKille() {return checkFlag(Flag.F17);}

	public boolean isUpLadder() 	{return checkFlag(Flag.F30);}
	public boolean isDownLadder() 	{return checkFlag(Flag.F31);}
	
	public void setVisible(boolean b)		{setFlag(b, Flag.F0);}
	public void setSeen(boolean b) 			{setFlag(b, Flag.F1);}
	
	public Character removeCharacter() {
		Character c = charHere;
		charHere = null;
		return c;
	}

	public boolean putCharacter(Character chr) {
		if (charHere != null) {
			return false;
		} else {
			charHere = chr;
			return true;
		}
	}

	public void dropItem(Item item) {
		itemsHere.add(item);
	}
	
}
