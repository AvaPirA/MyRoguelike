package com.avapir.roguelike.game.world.character.ai;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.game.world.character.Mob;

public class IdleAI extends EasyAI {

    static {
        instance = new IdleAI();
    }

    public static EasyAI getNewInstance() {
        return instance;
    }

    @Override
    public void computeAI(final Mob m, final Game g) {}

}
