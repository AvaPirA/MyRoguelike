package com.avapir.roguelike.game.world;

import java.awt.*;

public interface Locatable {

    public static final Point UNRESOLVED_LOCATION = new Point(-1, -1);

    @Deprecated
    public int getX();

    @Deprecated
    public int getY();

    public Point getLoc();

    public void setLocation(final int x, final int y);

    public void setLocation(final Point p);

}
