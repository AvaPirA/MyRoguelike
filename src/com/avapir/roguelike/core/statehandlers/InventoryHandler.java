package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.game.world.character.ClothingSlots;
import com.avapir.roguelike.game.world.character.Hero;

import java.awt.*;

public class InventoryHandler extends AbstractStateHandler {


    private Point   savedInventoryState = new Point(0, 0);
    private Point   savedEquipmentState = new Point(0, 0);
    private boolean focusOnEquipment    = false;
    private Point press;

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

    @Override
    protected int checkRestoreX(final int x) {
        int limitRight = focusOnEquipment ? 2 : com.avapir.roguelike.game.world.character.InventoryHandler.LINE;
        return x < 0 ? 0 : x > limitRight ? limitRight : x;
    }

    @Override
    protected int checkRestoreY(final int y) {
        int limitDown = focusOnEquipment ? 3 : Hero.getInstance().getInventory().getSize();
        return y < 0 ? 0 : y > limitDown ? limitDown : y;
    }

    public void press() {
        Hero h = Hero.getInstance();
        if (!focusOnEquipment) {
            if (press == null) { //memorize
                press = new Point(x, y);
            } else { //use
                h.getInventory().move(press, new Point(x, y));
                press = null;
            }
        } else {
            if (press == null) { //take off
                h.getEquipment().takeOff(ClothingSlots.fromCoord(x, y));
            } else { //put on
                h.getEquipment().putOn(press, ClothingSlots.fromCoord(x, y));
                changeFocus();
                press = null;
            }
        }
    }

    public void equip() {
        if (!focusOnEquipment) {
            press();
            changeFocus();
        }
    }

    public Point getPress() {
        return press;
    }
}
