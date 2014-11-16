package com.avapir.roguelike.game.world.map;

import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.items.Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MapHolder implements Map {

    private static final MapHolder INSTANCE = new MapHolder();
    /**
     * Full list of already created maps
     */
    private final List<GameMap> maps;
    private       GameMap       currentMap;

    private MapHolder() {
        maps = new ArrayList<>();
        currentMap = new GameMap(40, 40);
        maps.add(0, currentMap);
    }

    public static MapHolder getInstance() { return INSTANCE;}

    /**
     * Generates (if needed) and switches the game to another map (called when hero moved from one map to another).
     * <br>
     *
     * @param index internal index of map (just as ID)
     */
    public void switchToMap(final int index) {
        GameMap newMap = maps.get(index);
        if (newMap == null) {
            currentMap = new GameMap(40, 40);
            maps.add(index, currentMap);
        } else {
            currentMap = newMap;
        }
    }

    @Override
    public int getHeight() {
        return currentMap.getHeight();
    }

    @Override
    public int getWidth() {
        return currentMap.getWidth();
    }

    @Override
    public String getTitle() {
        return currentMap.getTitle();
    }

    @Override
    public Tile getTile(int x, int y) {
        return currentMap.getTile(x, y);
    }

    @Override
    public boolean putCharacter(Mob c, int x, int y) {
        return currentMap.putCharacter(c, x, y);
    }

    @Override
    public Point putCharacter(Mob c) {
        return currentMap.putCharacter(c);
    }

    @Override
    public Mob removeMob(Point p) {
        return currentMap.removeMob(p);
    }

    @Override
    public void dropItem(Item item, Point p) {
        currentMap.dropItem(item, p);
    }

    @Override
    public void dropItems(List<Item> items, Point p) {
        currentMap.dropItems(items, p);
    }

    @Override
    public void computeFOV(Point p, int radius) {
        currentMap.computeFOV(p, radius);
    }

    @Override
    public boolean isVisible(int x, int y) {
        return currentMap.isVisible(x, y);
    }

    @Override
    public void kill(Mob mob) { currentMap.kill(mob); }

    @Override
    public List<Mob> getMobs() { return currentMap.getMobs(); }

    @Override
    public void removeAllMobs() { currentMap.removeAllMobs(); }

    @Override
    public void killAllMobs() { currentMap.killAllMobs(); }

    @Override
    public boolean hasTile(int x, int y) {
        return currentMap.hasTile(x, y);
    }

    @Override
    public boolean isObstacle(int x, int y) {
        return currentMap.isObstacle(x, y);
    }

    @Override
    public void doMobsAi() {
        currentMap.doMobsAi();
    }

    @Override
    public void visit(int x, int y) {
        currentMap.visit(x, y);
    }
}

