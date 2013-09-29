package com.avapir.roguelike;

public class Tile {

	public static final int TILE_SIZE_px = 35;

	public int flags;

	public static final class TileInstances {

		public Tile[] instances;
		
	}
	
	public static final class Flag {
		public static final int EMPTY 			= 0b00000000000000000000000000000001;
		public static final int PASSABLE 		= 0b00000000000000000000000000000010;
		public static final int UP_LADDER		= 0b00000000000000000000000000000100;
		public static final int DOWN_LADDER		= 0b00000000000000000000000000001000;
		public static final int POSIONING		= 0b00000000000000000000000000010000;
		public static final int FLAMING			= 0b00000000000000000000000000100000;
		public static final int WET				= 0b00000000000000000000000001000000;
		public static final int LIGHT_ON		= 0b00000000000000000000000010000000;
		public static final int F9				= 0b00000000000000000000000100000000;
		public static final int F10				= 0b00000000000000000000001000000000;
		public static final int F11				= 0b00000000000000000000010000000000;
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

	public boolean isEmpty(){
		return Flag.EMPTY == (flags & Flag.EMPTY );
	}
	
	public boolean isPassable() { 
		return Flag.PASSABLE == (flags & Flag.PASSABLE);
	}
	
}
