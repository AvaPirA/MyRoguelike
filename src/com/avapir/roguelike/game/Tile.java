package com.avapir.roguelike.game;

import java.util.ArrayList;
import java.util.List;

import com.avapir.roguelike.locatable.Item;
import com.avapir.roguelike.locatable.Mob;

/**
 * Представление одной ячейки карты. Умеет управлять тем, что лежит на ней,
 * хранит все свои свойства. Ну понятно, короче
 * 
 * @author Alpen
 *         //TODO нужно допридумать свойства (флаги) тайлов
 */
public class Tile {

	public static final int	SIZE_px		= 32;

	private Mob				charHere	= null;
	private List<Item>		itemsHere	= new ArrayList<>();
	private final Tile.Type	initialType;
	private int				flags;

	public Tile(final Tile.Type it) {
		initialType = it;
		flags = Tile.Type.examples[it.ordinal()].flags;
	}

	private Tile(final Tile.Type t, final int... flg) {
		initialType = t;
		for (final int f : flg) {
			flags |= f;
		}
	}

	public void restoreDefault() {
		if (initialType == null) {
			return;
		} else {
			flags = Tile.Type.examples[initialType.ordinal()].flags;
		}
	}

	/**
	 * Хранит типы тайлов, создавалку типовых тайлов и образцы (на всякий
	 * случай, но чтобы не создавать заново каждый раз, когда вдруг понадобится)
	 * 
	 * @author Alpen
	 * 
	 */
	public static enum Type {
		EMPTY, GRASS, TREE, CLOSED_DOOR, OPENED_DOOR, STAIR_UP, STAIR_DOWN, WALL;

		static Tile getDefault(final Type t) {
			switch (t) {
			case EMPTY:
				return new Tile(EMPTY, Flag.EMPTY, Flag.PASSABLE, Flag.TRANSPARENT);
			case GRASS:
				return new Tile(GRASS, Flag.GRASS, Flag.PASSABLE, Flag.TRANSPARENT);
			default:
				return null;
			}
		}

		static Tile[]	examples	= { getDefault(Type.EMPTY), getDefault(GRASS) };
	}

	/**
	 * Хранит всевозможные свойства-флаги тайла, а так же служебные флаги ( {@link #FULL_FLAG} и
	 * {@link #EMPTY_FLAG})
	 * 
	 * @author Alpen
	 */
	public static final class Flag {

		public static final int	FULL_FLAG		= 0b11111111111111111111111111111111;
		public static final int	EMPTY_FLAG		= 0b00000000000000000000000000000000;

		/* 0-8 FOV */
		public static final int	VISIBLE			= 1 << 0;
		public static final int	SEEN			= 1 << 1;
		public static final int	LIGHT_ON		= 1 << 2;
		public static final int	TRANSPARENT		= 1 << 3;
		public static final int	F4				= 1 << 4;
		public static final int	F5				= 1 << 5;
		public static final int	F6				= 1 << 6;
		public static final int	F7				= 1 << 7;
		public static final int	F8				= 1 << 8;
		/* 9-14 terrain and moving */
		public static final int	PASSABLE		= 1 << 9;
		public static final int	EMPTY			= 1 << 10;
		public static final int	GRASS			= 1 << 11;
		public static final int	STONES			= 1 << 12;
		public static final int	ICE				= 1 << 13;
		/* 14-24 gaining effects */
		public static final int	POISIONING		= 1 << 14;
		public static final int	FLAMING			= 1 << 15;
		public static final int	WET				= 1 << 16;
		public static final int	INSTANT_DEATH	= 1 << 17;
		public static final int	F18				= 1 << 18;
		public static final int	F19				= 1 << 19;
		public static final int	F20				= 1 << 20;
		public static final int	F21				= 1 << 21;
		public static final int	F22				= 1 << 22;
		public static final int	F23				= 1 << 23;
		/* 24-31 specials */
		public static final int	F24				= 1 << 24;
		public static final int	F25				= 1 << 25;
		public static final int	F26				= 1 << 26;
		public static final int	F27				= 1 << 27;
		public static final int	OPEN_DOOR		= 1 << 28;
		public static final int	CLOSED_DOOR		= 1 << 29;
		public static final int	UP_LADDER		= 1 << 30;
		public static final int	DOWN_LADDER		= 1 << 31;

	}

	private boolean checkFlag(final int flag) {
		return flag == (flags & flag);
	}

	private void addFlags(final int newFlag) {
		flags = flags | newFlag;
	}

	private void removeFlags(final int newFlag) {
		flags = flags & invertFlag(newFlag);
	}

	private int invertFlag(final int flag) {
		return Flag.FULL_FLAG ^ flag;
	}

	private void setFlag(final boolean b, final int flag) {
		if (b) {
			addFlags(flag);
		} else {
			removeFlags(flag);
		}
	}

//@formatter:off
	public boolean isVisible() 			{return checkFlag(Flag.VISIBLE);}
	public boolean isSeen() 			{return checkFlag(Flag.SEEN);}
	public boolean isLantern() 			{return checkFlag(Flag.LIGHT_ON);}
	public boolean isTransparent()		{return checkFlag(Flag.TRANSPARENT);}

	public boolean isPassable() 		{return checkFlag(Flag.PASSABLE);}
	public boolean isEmpty() 			{return checkFlag(Flag.EMPTY);}
	
	public boolean isGrass() 			{return checkFlag(Flag.GRASS);}
	public boolean isStone() 			{return checkFlag(Flag.STONES);}
	public boolean isIce() 				{return checkFlag(Flag.ICE);}
	public boolean isPoisioning() 		{return checkFlag(Flag.POISIONING);}
	public boolean isFlaming() 			{return checkFlag(Flag.FLAMING);}
	public boolean isWet() 				{return checkFlag(Flag.WET);}
	public boolean isInstantKiller()	{return checkFlag(Flag.INSTANT_DEATH);}

	public boolean isClosed() 			{return checkFlag(Flag.CLOSED_DOOR);}
	public boolean isUpLadder() 		{return checkFlag(Flag.UP_LADDER);}
	public boolean isDownLadder() 		{return checkFlag(Flag.DOWN_LADDER);}

	
	public void setVisible(final boolean b) {setFlag(b, Flag.VISIBLE);}
	public void setSeen(final boolean b) 	{setFlag(b, Flag.SEEN);}
//@formatter:on

	// add-methods

	/**
	 * Ставит персонажа на этот тайл, если <li>тут никого нет</li> <li>проходимо</li> <li>не
	 * instantKiller</li>
	 * 
	 * @param chr
	 *            кого ставим
	 * @return поставился ли
	 */
	public boolean putCharacter(final Mob chr) {
		if (charHere != null || !isPassable() || isInstantKiller()) {
			return false;
		} else {
			charHere = chr;
			return true;
		}
	}

	/**
	 * Кладет на тайл новый предмет
	 * 
	 * @param item
	 *            что кладем
	 */
	public void dropItem(final Item item) {
		itemsHere.add(item);
	}

	// get-methods

	public Mob getMob() {
		return charHere;
	}

	public int getFlags() {
		return flags;
	}

	public List<Item> getItemList() {
		return itemsHere;
	}

	// remove-methods

	/**
	 * Убирает всё живое с тайла
	 * 
	 * @return кто стоял
	 */
	public Mob removeCharacter() {
		final Mob c = charHere;
		charHere = null;
		return c;
	}
}
