package com.avapir.roguelike.core;

import com.avapir.roguelike.core.statehandlers.ChangingStatsHandler;
import com.avapir.roguelike.core.statehandlers.InventoryHandler;
import com.avapir.roguelike.game.world.character.Hero;
import com.avapir.roguelike.game.world.character.Hero.PrimaryStats;
import com.avapir.roguelike.game.world.map.MapHolder;
import com.avapir.roguelike.game.world.map.Tile;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * That class stores all information about the game (actors, terrains, current state, etc) and manages it (transfers
 * some information from one actor to another or provides necessary data to painters.
 *
 * @author Alpen Ditrix
 * @since 0.0.1
 */
public class GameStateManager {

    /**
     * States in which game may be present. Current state affects GUI and the availability of various functions
     *
     * @author Alpen Ditrix
     * @since 0.0.1
     */
    public static enum GameState {
        /**
         * Awaiting of hero's try to step to another tile. That's default state of game on start and after end of
         * previous turn
         */
        MOVE,
        /**
         * Showing to the player content of hero's inventory and equipment. Also provides ability to operate with items
         * at inventory
         *
         * @see com.avapir.roguelike.game.world.character.Hero.InventoryHandler
         * @see com.avapir.roguelike.game.world.items.Item
         */
        INVENTORY,
        /**
         * Changing hero's stats on user's demand (and when hero has unclaimed stat-points)
         *
         * @see com.avapir.roguelike.game.world.character.Hero.PrimaryStats
         * @see com.avapir.roguelike.game.world.character.Hero.DefaultStats
         * @see com.avapir.roguelike.game.world.character.Hero.StatsFormulas
         */
        CHANGE_STATS,
        /**
         * Terminal state of the game
         */
        GAME_OVER,
        /**
         * For using distance attack user must point the tile, where he want to shot (or spit, or smth else)
         */
        DISTANCE_ATTACK,
        /**
         * That state is similar to distance attack, but here you can look at any visible tile and after termination of
         * that state, game will just return to previous one
         */
        VIEW
    }

    /* Singleton mechanism */
    private static final GameStateManager INSTANCE = new GameStateManager();
    /**
     * Amount of turns since start of game
     */
    private int                  turnCounter;
    /**
     * Current state of the game
     */
    private GameState            state;
    /* handler instances */
    private ChangingStatsHandler changingStatsHandler;
    private InventoryHandler     inventoryHandler;
    private boolean helpFlag = false;

    private GameStateManager() {}

    public static GameStateManager getInstance() { return INSTANCE; }

    /**
     * Checks if provided {@link Point} is a proper "step" and if it is non-zero (distance > 0)
     *
     * @param dp step direction
     *
     * @return true if both coordinates of step are non-zero
     *
     * @throws java.lang.IllegalArgumentException if one of coordinates is not -1, 0 or 1
     */
    private static boolean isActuallyMoved(final Point dp) {
        if (!((dp.x == 1 || dp.x == 0 || dp.x == -1) && (dp.y == 1 || dp.y == 0 || dp.y == -1))) {
            throw new IllegalArgumentException("Wrong step");
        } else {
            return !(dp.x == 0 && dp.y == 0);
        }
    }

    /**
     * Preload fonts to have no freezes for that later (when font must be painted)
     */
    @Deprecated
    private static void loadFonts() {
        final Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics();
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 8));
        g2.drawString("asd", 0, 0);
    }

    /**
     * Enlarges tile size
     */
    public static void zoomIn() {
        Tile.SIZE_px += 1;
    }

    /**
     * Reduces tile size
     */
    public static void zoomOut() {
        Tile.SIZE_px -= 1;
    }

    public void start() {
        state = GameState.MOVE;
        Viewport.INSTANCE.setCenter(MapHolder.getInstance().putCharacter(Hero.getInstance()));
        turnCounter = 0;
        Point zeroStep = new Point(0, 0);
        EOT(zeroStep);
    }

    /**
     * If performed step is correct, logs that and checks if viewport must be moved
     *
     * @param dp step direction
     */
    private void move(final Point dp) {
        if (isActuallyMoved(dp)) {
            Log.g("Перешел в [%s, %s]", Hero.getInstance().getLoc().x, Hero.getInstance().getLoc().y);
            Viewport.INSTANCE.move(dp);
        } else {
            Log.g("Смеркалось");
        }
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void repaint() {
        Hero.getInstance().updateStats();
        Main.renderer.repaint();
    }

    public void EOT(final Point playerStep) {
        move(playerStep);
        // TODO SET GAME.BUSY
        MapHolder.getInstance()
                 .computeFOV(Hero.getInstance().getLoc(), Hero.StatsFormulas.getFovRadius(Hero.getInstance()));
        MapHolder.getInstance().doMobsAi();

        turnCounter++;
        repaint();
    }

    public void gameOver() {
        setState(GameState.GAME_OVER);
    }

    public GameState getState() {
        return state;
    }

    public void setState(final GameState state) {
        if (this.state == state) {
            this.state = GameState.MOVE;
        } else {
            this.state = state;
        }
    }

    public ChangingStatsHandler getStatsHandler() {
        return changingStatsHandler;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public void createStatsHandler() {
        Log.g("_____________________");
        Log.g("Изменение характеристик:");
        Log.g("Свободных хар-к: %s", Hero.getInstance().getStats().getFreeStats());
        changingStatsHandler = new ChangingStatsHandler();
    }

    public void removeStatsHandler() {
        Hero h = Hero.getInstance();
        changingStatsHandler.flush();
        Log.g("Характеристики увеличились на:");
        final String[] ss = PrimaryStats.STATS_STRINGS;
        Log.g("%s:%s;              %s:%s", ss[0], (h.getStats().values(0) - changingStatsHandler.getDiff()[0]), ss[1],
              (h.getStats().values(1) - changingStatsHandler.getDiff()[1]));
        Log.g("%s:%s;              %s:%s", ss[2], (h.getStats().values(2) - changingStatsHandler.getDiff()[2]), ss[3],
              (h.getStats().values(5) - changingStatsHandler.getDiff()[5]));
        Log.g("%s:%s;              %s:%s", ss[4], (h.getStats().values(4) - changingStatsHandler.getDiff()[4]), ss[5],
              (h.getStats().values(5) - changingStatsHandler.getDiff()[5]));
        Log.g("__________________________");
        changingStatsHandler = null;
    }

    public void createInventoryHandler() {
        Log.g("_____________________");
        Log.g("Открыт инвентарь!");
        inventoryHandler = new InventoryHandler();
    }

    public void removeInventoryHandler() {
        Log.g("Инвентарь закрыт");
        Log.g("_____________________");
        inventoryHandler = null;
    }

    public boolean isNeedHelp() {
        return helpFlag;
    }

    public void help() {
        helpFlag = !helpFlag;
    }
}
