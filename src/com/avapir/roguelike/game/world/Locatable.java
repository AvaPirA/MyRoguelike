package com.avapir.roguelike.game.world;

import java.awt.*;

/**
 * Every Locatable object has some projection on the game map (so it may be associated with some tile)
 */
public abstract class Locatable {

    private       Point location = UNRESOLVED_LOCATION;

    protected Locatable() {}

    public Locatable(Point point) {
        location = new Point(point);
    }

    /**
     * @return horizontal coordinate
     *
     * @deprecated use {@link Point}-based method {@link com.avapir.roguelike.game.world.Locatable#getLoc()}
     */
    public int getX() {
        return location.x;
    }
    /**
     * @return vertical coordinate
     *
     * @deprecated use {@link Point}-based method {@link com.avapir.roguelike.game.world.Locatable#getLoc()}
     */
    public int getY() {
        return location.y;
    }
    /**
     * @return location of object on the map
     */
    public Point getLoc() {
        return location;
    }
    /**
     * Sets new location of object by specified coordinated
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public void setLocation(int x, int y) {
        location = new Point(x, y);
    }
    /**
     * Sets new location of object by specified point
     *
     * @param p new location
     */
    public void setLocation(Point p) {
        location = new Point(p);
    }

    /**
     * Special value for objects, which are not located yet or temporarily removed from map or something else doesn't
     * let to associate object with it's tile
     */
    public static final Point UNRESOLVED_LOCATION = new Point(-1, -1);

}
