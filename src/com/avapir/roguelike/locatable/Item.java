package com.avapir.roguelike.locatable;

import com.avapir.roguelike.battle.Armor;
import com.avapir.roguelike.battle.Attack;

import java.awt.*;

/**
 * Мб будет компас, который указывает на место, где лежит искомый предмет =>
 * надо Locatables
 *
 * @author Alpen
 */
public class Item implements Locatable {

    private static final int items = 0;

    private String name;
    private  Attack damage;
    private  Armor  armor;
    private  int    weight;

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

    private  Point location;

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
