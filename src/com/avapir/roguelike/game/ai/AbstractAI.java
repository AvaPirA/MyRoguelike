package com.avapir.roguelike.game.ai;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.avapir.roguelike.core.Game;
import com.avapir.roguelike.locatable.Item;
import com.avapir.roguelike.locatable.Mob;

public abstract class AbstractAI {

	static final Random	r	= new Random();

	public abstract void computeAI(Mob m, Game g);

	public void onDeath(final Mob mob, final Game g) {
		g.getMap().removeCharacter(mob.getLoc());
		g.getMap().dropItems(getDrop(mob, g), mob.getLoc());
	}

	protected List<Item> getDrop(final Mob mob, final Game g) {
		final List<Item> list = Collections.emptyList();
		return list;
	}

	protected Point getRandomDirection() {
		return new Point(new Point(r.nextInt(3) - 1, r.nextInt(3) - 1));
	}

}
