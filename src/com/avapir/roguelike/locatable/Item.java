package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;

/**
 * Мб будет компас, который указывает на место, где лежит искомый предмет =>
 * надо Locatables
 * 
 * @author Alpen
 * 
 */
public class Item implements Locatable {

	public static enum ItemType {
		ArmorHead, ArmorBody, ArmorLegs, ArmorArms, Weapon, Consumable
	}

	{
		itemID = items++;
	}
	@SuppressWarnings("unused")
	private final int	itemID;
	public static int	items	= 0;

	private String		name;
	private Attack		damage;
	private Armor		armor;
	private int			weight;

	public String getName() {
		return name;
	}

	public Attack getAttack() {
		return damage;
	}

	public Armor getArmor() {
		return armor;
	}

	public int getWeight() {
		return weight;
	}

	private int	X;
	private int	Y;

	@Override
	public int getX() {
		return X;
	}

	@Override
	public int getY() {
		return Y;
	}

	@Override
	public void setLocation(final int x, final int y) {
		X = x;
		Y = y;
	}
}
