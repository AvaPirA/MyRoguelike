package com.avapir.roguelike;

import java.util.ArrayList;
import java.util.List;

public class Tile {

	public static final int TILE_SIZE_px = 35;

	private int flags;
	private Character charHere;
	private List<Item> itemsHere;
	
	public Tile(int flag, Character c, List<Item> items) {
		this.flags = flag;
		charHere = c;
		if (items == null) {
			itemsHere = new ArrayList<>();
		} else {
			itemsHere = items;
		}
	}
	
	private Tile(int... flg) {
		for(int f : flg) {
			flags |= f;
		}
		charHere = null;
		itemsHere = null;
	}

	public static enum Type {
		EMPTY, GRASS, TREE, CLOSED_DOOR, OPENED_DOOR, STAIR_UP, STAIR_DOWN, DESTROYED_TOWER_WALL, TOWER_WALL, TOWER_FLOOR, WATER, DUNGEON_FLOOR, DUNGEON_WALL;
		public static final Tile[] example = {
			new Tile(0b00000000000000000000000000000001, null, null),
			
		};
	}

	public static final class Flag {
		public static final int EMPTY_FLAG		= 0b00000000000000000000000000000000;
		public static final int FULL_FLAG		= 0b11111111111111111111111111111111;
		public static final int EMPTY 			= 0b00000000000000000000000000000001;
		public static final int PASSABLE 		= 0b00000000000000000000000000000010;
		public static final int UP_LADDER		= 0b00000000000000000000000000000100;
		public static final int DOWN_LADDER		= 0b00000000000000000000000000001000;
		public static final int POSIONING		= 0b00000000000000000000000000010000;
		public static final int FLAMING			= 0b00000000000000000000000000100000;
		public static final int WET				= 0b00000000000000000000000001000000;
		public static final int LIGHT_ON		= 0b00000000000000000000000010000000;
		public static final int VISIBLE			= 0b00000000000000000000000100000000;
		public static final int SEEN			= 0b00000000000000000000001000000000;
		public static final int TRANSPARENT		= 0b00000000000000000000010000000000;
		public static final int F12				= 0b00000000000000000000100000000000;
		public static final int F13				= 0b00000000000000000001000000000000;
		public static final int F14				= 0b00000000000000000010000000000000;
		public static final int F15				= 0b00000000000000000100000000000000;
		public static final int F16				= 0b00000000000000001000000000000000;
		public static final int F17				= 0b00000000000000010000000000000000;
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
		public static final int F31				= 0b01000000000000000000000000000000;
		public static final int F32				= 0b10000000000000000000000000000000;
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
		return checkFlag(Flag.EMPTY);
	}
	
	public boolean isPassable() { 
		return checkFlag(Flag.PASSABLE);
	}
	
	public boolean isUpLadder() {
		return checkFlag(Flag.UP_LADDER);
	}
	
	public boolean isDownLadder() {
		return checkFlag(Flag.DOWN_LADDER);
	}
	
	public boolean isPoisioning() {
		return checkFlag(Flag.POSIONING);
	}
	
	public boolean isFlaming() {
		return checkFlag(Flag.FLAMING);
	}
	
	public boolean isWet() {
		return checkFlag(Flag.WET);
	}
	
	public boolean isLantern() {
		return checkFlag(Flag.LIGHT_ON);
	}
	
	public boolean isVisible() {
		return checkFlag(Flag.VISIBLE);
	}

	public boolean isSeen() {
		return checkFlag(Flag.SEEN);
	}
	
	public boolean isTransparent() {
		return checkFlag(Flag.TRANSPARENT);
	}

	public void setSeen(boolean newIsSeen) {
		setFlag(newIsSeen, Flag.SEEN);
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
