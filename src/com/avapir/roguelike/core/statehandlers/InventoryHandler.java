package com.avapir.roguelike.core.statehandlers;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Hero;

public class InventoryHandler extends AbstractStateHandler {

    private boolean focusOnEquipment = false;

    public void changeFocus() {
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
        return y < 0 ? 0 : y > 3 ? 3 : y;
    }
}
