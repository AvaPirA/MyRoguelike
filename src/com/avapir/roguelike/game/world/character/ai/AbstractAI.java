package com.avapir.roguelike.game.world.character.ai;

import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.items.Item;
import com.avapir.roguelike.game.world.map.MapHolder;

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
    static final Random random = new Random();

    /**
     */
    public abstract void computeAI(Mob m);

    public void mustDie(final Mob mob) {
        MapHolder.getInstance().kill(mob);
    }

    public List<Item> getDrop() {
        //TODO drop
        return Collections.emptyList();
    }

    protected Point getRandomDirection() {
        return new Point(new Point(random.nextInt(3) - 1, random.nextInt(3) - 1));
    }

}
