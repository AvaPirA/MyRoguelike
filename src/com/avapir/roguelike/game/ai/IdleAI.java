package com.avapir.roguelike.game.ai;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Mob;

public class IdleAI extends EasyAI {

    static {
        instance = new IdleAI();
    }

    @Override
    public void computeAI(final Mob m, final Game g) {}

    public static EasyAI getNewInstance() {
        return instance;
    }

}
