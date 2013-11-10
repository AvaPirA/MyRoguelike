package com.avapir.roguelike.locatable;

import java.awt.*;

/** User: Alpen Ditrix Date: 09.11.13 Time: 15:39 To change this template use File | Settings | File Templates. */
public class DroppedItem implements Locatable {

    private final Item  item;
    private       Point location;

    public DroppedItem(Item item, Point point) {
        this.item = item;
        location = new Point(point);
    }

    public Item getItem() {
        return item;
    }

    @Override
    public int getX() {
        return location.x;
    }

    @Override
    public int getY() {
        return location.y;
    }

    @Override
    public Point getLoc() {
        return location;
    }

    @Override
    public void setLocation(int x, int y) {
        location = new Point(x, y);
    }

    @Override
    public void setLocation(Point p) {
        location = new Point(p);
    }
}
