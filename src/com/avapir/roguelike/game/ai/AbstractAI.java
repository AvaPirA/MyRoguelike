package com.avapir.roguelike.game.ai;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Item;
import com.avapir.roguelike.locatable.Mob;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class AbstractAI {

    protected static final Random r = new Random();

    public abstract void computeAI(Mob m, Game g);

    public void onDeath(final Mob mob, final Game g) {
        g.getMap().removeCharacter(mob.getLoc());
        g.getMap().dropItems(getDrop(mob, g), mob.getLoc());
    }

    List<Item> getDrop(final Mob mob, final Game g) {
        //TODO drop
        return Collections.emptyList();
    }

    Point getRandomDirection() {
        return new Point(new Point(r.nextInt(3) - 1, r.nextInt(3) - 1));
    }

}
