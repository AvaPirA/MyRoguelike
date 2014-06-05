package com.avapir.roguelike.game.world.character.ai;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.items.Item;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Defines mob behavior on every state. Also defines drop-list of mob, which uses that AI
 */
public abstract class AbstractAI {

    /**
     * General haphazard generator
     */
    protected static final Random random = new Random();

    /**
     * @param m
     * @param g
     */
    public abstract void computeAI(Mob m, Game g);

    public void onDeath(final Mob mob, final Game g) {
        g.getMap().removeCharacter(mob.getLoc());
        g.getMap().dropItems(getDrop(mob, g), mob.getLoc());
    }

    List<Item> getDrop(final Mob mob, final Game g) {
        //TODO drop
        return Collections.emptyList();
    }

    protected Point getRandomDirection() {
        return new Point(new Point(random.nextInt(3) - 1, random.nextInt(3) - 1));
    }

}
