package com.avapir.roguelike.game.world;

import java.awt.*;

/**
 * That interface applying to game-objects which somehow may be located on the game map (so the ma be associated with
 * some tile)
 */
public interface Locatable {

    /**
     * Special value for objects, which are not located yet or temporarily removed from map or something else doesn't
     * let to associate object with it's tile
     */
    public static final Point UNRESOLVED_LOCATION = new Point(-1, -1);

    /**
     * @return horizontal coordinate
     *
     * @deprecated use {@link Point}-based method {@link com.avapir.roguelike.game.world.Locatable#getLoc()}
     */
    @Deprecated
    public int getX();

    /**
     * @return vertical coordinate
     *
     * @deprecated use {@link Point}-based method {@link com.avapir.roguelike.game.world.Locatable#getLoc()}
     */
    @Deprecated
    public int getY();

    /**
     * @return location of object on the map
     */
    public Point getLoc();

    /**
     * Sets new location of object by specified coordinated
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public void setLocation(final int x, final int y);

    /**
     * Sets new location of object by specified point
     *
     * @param p new location
     */
    public void setLocation(final Point p);

}
