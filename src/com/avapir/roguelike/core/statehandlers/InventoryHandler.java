package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.game.ClothingSlots;
import com.avapir.roguelike.locatable.Hero;

import java.awt.*;

public class InventoryHandler extends AbstractStateHandler {


    private Point   savedInventoryState = new Point(0, 0);
    private Point   savedEquipmentState = new Point(0, 0);
    private boolean focusOnEquipment    = false;

    public boolean isOnEquipment() {
        return focusOnEquipment;
    }

    public void changeFocus() {
        if (focusOnEquipment) {
            savedEquipmentState = new Point(x, y);
            x = savedInventoryState.x;
            y = savedInventoryState.y;
        } else {
            savedInventoryState = new Point(x, y);
            x = savedEquipmentState.x;
            y = savedEquipmentState.y;
        }
        focusOnEquipment = !focusOnEquipment;
    }

    public InventoryHandler(final Game g) {
        super(g);
    }

    @Override
    protected int checkRestoreX(final int x) {
        int limitRight = focusOnEquipment ? 2 : Hero.InventoryHandler.LINE;
        return x < 0 ? 0 : x > limitRight ? limitRight : x;
    }

    @Override
    protected int checkRestoreY(final int y) {
        int limitDown = focusOnEquipment ? 3 : game.getHero().getInventory().getSize();
        return y < 0 ? 0 : y > limitDown ? limitDown : y;
    }

    public Point press;

    public void press() {
        Hero h = game.getHero();
        if (!focusOnEquipment) {
            if (press == null) { //memorize
                press = new Point(x, y);
            } else { //use
                h.getInventory().move(press, new Point(x, y));
                press = null;
            }
        } else {
            if(press == null) { //take off
                h.getEquipment().takeOff(ClothingSlots.fromCoord(x, y));
            } else { //put on
                h.getEquipment().putOn(press, ClothingSlots.fromCoord(x, y));
                changeFocus();
                press = null;
            }
        }
    }

    public void equip() {
        if(!focusOnEquipment){press();
        changeFocus();
        }
    }

    public Point getPress() {
        return press;
    }
}
