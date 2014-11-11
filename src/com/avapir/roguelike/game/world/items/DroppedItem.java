package com.avapir.roguelike.game.world.items;

import com.avapir.roguelike.game.world.Locatable;

import java.awt.*;

/** User: Alpen Ditrix Date: 09.11.13 Time: 15:39 To change this template use File | Settings | File Templates. */
public class DroppedItem extends Locatable {

    private final Item  item;

    public DroppedItem(Item item, Point point) {
        super(point);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

}
