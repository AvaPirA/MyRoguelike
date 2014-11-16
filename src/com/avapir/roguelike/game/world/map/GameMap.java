package com.avapir.roguelike.game.world.map;

import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.items.Item;
import com.avapir.roguelike.game.world.map.fov.LosMap;

import java.awt.*;
import java.util.List;

/**
 *
 */
public interface GameMap extends LosMap {


    public int getHeight();
    public int getWidth();
    public String getTitle();
    public Tile getTile(final int x, final int y);
    public boolean putCharacter(final Mob c, final int x, final int y);
    public Point putCharacter(final Mob c);
    public Mob removeMob(final Point p);
    public void dropItem(final Item item, final Point p);
    public void dropItems(final java.util.List<Item> items, final Point p);
    public void computeFOV(final Point p, final int radius);
    public boolean isVisible(final int x, final int y);

    public void kill(Mob mob);

    public List<Mob> getMobs();
    public void removeAllMobs();
    public void killAllMobs();

    public void doMobsAi();
}
