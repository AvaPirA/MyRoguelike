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
		
		/*
		 * 1-9 FOV
		 * 10-15 terrain and moving
		 */
		public static final int F1 				= 0b00000000000000000000000000000001;//VISIBLE
		public static final int F2 				= 0b00000000000000000000000000000010;//SEEN
		public static final int F3 				= 0b00000000000000000000000000000100;//LIGHT_ON
		public static final int F4				= 0b00000000000000000000000000001000;//TRANSPARENT
		public static final int F5 				= 0b00000000000000000000000000010000;
		public static final int F6 				= 0b00000000000000000000000000100000;
		public static final int F7 				= 0b00000000000000000000000001000000;
		public static final int F8 				= 0b00000000000000000000000010000000;
		public static final int F9 				= 0b00000000000000000000000100000000;
		public static final int F10				= 0b00000000000000000000001000000000;//PASSABLE
		public static final int F11				= 0b00000000000000000000010000000000;//EMPTY -- ничего
		public static final int F12				= 0b00000000000000000000100000000000;//GRASS -- may heal/gain luck
		public static final int F13				= 0b00000000000000000001000000000000;//STONES -- may fall(dont go and lose hp)
		public static final int F14				= 0b00000000000000000010000000000000;//ICE -- may slip(lose hp, gain luck)
		public static final int F15				= 0b00000000000000000100000000000000;//POISIONING -- DoT, decreases dmg from lightning
		public static final int F16				= 0b00000000000000001000000000000000;//FLAMING -- DoT, incr
		public static final int F17				= 0b00000000000000010000000000000000;//WET -- removes flame, decreases dmg from lightning
		public static final int F18				= 0b00000000000000100000000000000000;
		public static final int F19				= 0b00000000000001000000000000000000;
		public static final int F20				= 0b00000000000010000000000000000000;
		public static final int F21				= 0b00000000000100000000000000000000;
		public static final int F22				= 0b00000000001000000000000000000000;
		public static final int F23				= 0b00000000010000000000000000000000;
		public static final int F24				= 0b00000000100000000000000000000000;
		public static final int F25				= 0b00000001000000000000000000000000;
		public static final int F26				= 0b00000010000000000000000000000000;
		public static final int F27				= 0b00000100000000000000000000000000;
		public static final int F28				= 0b00001000000000000000000000000000;
		public static final int F29				= 0b00010000000000000000000000000000;
		public static final int F30				= 0b00100000000000000000000000000000;
		public static final int F31				= 0b01000000000000000000000000000000;//UP_LADDER
		public static final int F32				= 0b10000000000000000000000000000000;//DOWN_LADDER
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
	
	public boolean isEmpty(){
		return checkFlag(Flag.F11);
	}
	
	public boolean isPassable() { 
		return checkFlag(Flag.F10);
	}
	
	public boolean isUpLadder() {
		return checkFlag(Flag.F31);
	}
	
	public boolean isDownLadder() {
		return checkFlag(Flag.F32);
	}
	
	public boolean isPoisioning() {
		return checkFlag(Flag.F15);
	}
	
	public boolean isFlaming() {
		return checkFlag(Flag.F16);
	}
	
	public boolean isWet() {
		return checkFlag(Flag.F17);
	}
	
	public boolean isLantern() {
		return checkFlag(Flag.F3);
	}
	
	public boolean isVisible() {
		return checkFlag(Flag.F1);
	}

	public boolean isSeen() {
		return checkFlag(Flag.F2);
	}
	
	public boolean isTransparent() {
		return checkFlag(Flag.F4);
	}

	public void setSeen(boolean newIsSeen) {
		setFlag(newIsSeen, Flag.F2);
	}

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

	public void setVisible(boolean b) {

	}
	
}
