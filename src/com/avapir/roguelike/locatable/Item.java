package com.avapir.roguelike.locatable;

import java.awt.Point;

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

    private static final int items = 0;

    private final String name;
    private final Attack damage;
    private final Armor  armor;
    private final int    weight;

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

    private final Point location;

    @Deprecated
    @Override
    public int getX() {
        return location.x;
    }

    @Override
    public int getY() {
        return location.y;
    }

    @Override
    public void setLocation(final int x, final int y) {
        location.setLocation(x, y);
    }

    @Override
    public Point getLoc() {
        return location;
    }

    @Override
    public void setLocation(final Point p) {
        location.setLocation(p);
    }
}
