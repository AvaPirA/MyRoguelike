package com.avapir.roguelike.core;

import com.avapir.roguelike.core.controls.KeyboardHandler;
import com.avapir.roguelike.core.gui.GameWindow;
import com.avapir.roguelike.core.statehandlers.ChangingStatsHandler;
import com.avapir.roguelike.core.statehandlers.InventoryHandler;
import com.avapir.roguelike.game.world.character.Hero;
import com.avapir.roguelike.game.world.character.Hero.PrimaryStats;
import com.avapir.roguelike.game.world.character.Mob;
import com.avapir.roguelike.game.world.map.Map;
import com.avapir.roguelike.game.world.map.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * That class stores all information about the game (actors, terrains, current state, etc) and manages it (transfers
 * some information from one actor to another or provides necessary data to painters.
 *
 * @author Alpen Ditrix
 * @since 0.0.1
 */
public class Game implements StateHandlerOperator, IGame, IRoguelikeGame {

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

    /**
     * Full list of already created maps
     */
    private final List<Map> maps = new ArrayList<>();

    /**
     * The only object that is controlled by player (user). That's his\her personal unique {@link
     * com.avapir.roguelike.game.world.character.Mob}
     *
     * @see com.avapir.roguelike.game.world.character.Mob
     * @see com.avapir.roguelike.game.world.character.Hero
     */
    private Hero hero;

    /**
     * Main window of the game. Here everything will be painted.
     */
    private final GameWindow gameWindow = new GameWindow(this);

    /**
     * List of the mobs on that map
     */
    //todo move to Map.java
    private final List<Mob> mobs = new ArrayList<>();

    /**
     * Current terrain
     */
    private Map currentMap;

    /**
     * {@link Viewport} instance. That thing provides following players view to hero movement on the playground.
     */
    private Viewport viewport;

    /**
     * Amount of turns since start of game
     */
    private int       turnCounter;
    /**
     * Current state of the game
     */
    private GameState state;

    /* handler instances */
    private ChangingStatsHandler changingStatsHandler;
    private InventoryHandler     inventoryHandler;
    private KeyboardHandler      keyboardHandler;

    private final String title;

    /**
     * This is the only constructor for the game.
     *
     * @param title name of the game. It will be the title of window
     */
    public Game(final String title) {
        this.title = title;
    }

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
    private static void loadFonts() {
        final Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics();
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 8));
        g2.drawString("asd", 0, 0);
    }

    @Override
    public void start() {
        Log.getInstance().connect(this);
        System.out.println("Uses Toolkit: " + System.getProperty("awt.toolkit"));

        gameWindow.setTitle(title);
        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameWindow.setVisible(true);
    }

    @Override
    public void init() {
    }

    @Override
    public void done() {
        hero = new Hero("Hero", this);
        state = GameState.MOVE;
        // TODO потом надо поставить подходящий конструктор для карты
        final int firstMapIndex = 0;
        maps.add(firstMapIndex, new Map(this, 40, 40));
        switchToMap(firstMapIndex);
        turnCounter = 0;
    }

    private void placeMobsAndItems(final int scaler) {
        //todo move to Map.java
//        int i = 320;
        for (int x = 0; x < currentMap.getWidth(); x++) {
            for (int y = 0; y < currentMap.getHeight(); y++) {
//                if (i > 0) {
//                    mobs.add(Mob.MobSet.getSlime());
//                    currentMap.putCharacter(mobs.get(mobs.size() - 1), x, y);
//                    i--;
//                }
            }
        }
    }

    /**
     * Generates (if needed) and switches the game to another map (called when hero moved from one map to another).
     * <br>
     * Afterall turn will be ended,
     *
     * @param index internal index of map (just as ID)
     */
    private void switchToMap(final int index) {
        if (maps.get(index) == null) {
            currentMap = new Map(this, 40, 40);
            maps.add(index, currentMap);
        } else {
            currentMap = maps.get(index);
        }
        viewport = new Viewport(currentMap.putCharacter(hero), this);
        placeMobsAndItems(index);

        final Point zeroStep = new Point(0, 0);
        EOT(zeroStep);
    }

    /**
     * If performed step is correct, logs that and checks if viewport must be moved
     *
     * @param dp step direction
     */
    private void move(final Point dp) {
        if (isActuallyMoved(dp)) {
            Log.g("Перешел в [%s, %s]", hero.getLoc().x, hero.getLoc().y);
            viewport.move(dp);
        }
    }

    /**
     * Executes AI instructions for each Mob
     */
    private void doUnlimitedAiWorks() {
        hero.doAI(this);
        for (Mob mob : mobs) {
            mob.doAI(this);
        }
    }

    @Override
    public Hero getHero() {
        return hero;
    }

    @Override
    public Map getMap() {
        return currentMap;
    }

    @Override
    public int getTurnCounter() {
        return turnCounter;
    }

    @Override
    public void repaint() {
        hero.updateStats();
        gameWindow.repaint();
    }

    @Override
    public int getCurrentX() {
        return viewport.getX();
    }

    @Override
    public int getCurrentY() {
        return viewport.getY();
    }

    @Override
    public Mob removeMob(final Mob m) {
        mobs.remove(m);
        return m;
    }

    @Override
    public void EOT(final Point playerStep) {
        move(playerStep);
        // TODO SET GAME.BUSY
        currentMap.computeFOV(hero.getLoc(), Hero.StatsFormulas.getFovRadius(hero));
        doUnlimitedAiWorks();

        turnCounter++;
        repaint();
    }

    @Override
    public void gameOver() {
        setState(GameState.GAME_OVER);
    }

    @Override
    public GameState getState() {
        return state;
    }

    @Override
    public void setState(final GameState state) {
        if (this.state == state) {
            this.state = GameState.MOVE;
        } else {
            this.state = state;
        }
    }

    @Override
    public ChangingStatsHandler getStatsHandler() {
        return changingStatsHandler;
    }

    @Override
    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    @Override
    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    @Override
    public void setKeyboardHandler(final KeyboardHandler keyboardHandler) {
        this.keyboardHandler = keyboardHandler;
    }

    @Override
    public void createStatsHandler() {
        Log.g("_____________________");
        Log.g("Изменение характеристик:");
        Log.g("Свободных хар-к: %s", hero.getStats().getFree());
        changingStatsHandler = new ChangingStatsHandler(this);
    }

    @Override
    public void removeStatsHandler() {
        changingStatsHandler.flush();
        Log.g("Характеристики увеличились на:");
        final String[] ss = PrimaryStats.STATS_STRINGS;
        Log.g("%s:%s;              %s:%s", ss[0], (hero.getStats().values(0) -
                changingStatsHandler.getDiff()[0]), ss[1], (hero.getStats().values(1) -
                changingStatsHandler.getDiff()[1]));
        Log.g("%s:%s;              %s:%s", ss[2], (hero.getStats().values(2) -
                changingStatsHandler.getDiff()[2]), ss[3], (hero.getStats().values(5) -
                changingStatsHandler.getDiff()[5]));
        Log.g("%s:%s;              %s:%s", ss[4], (hero.getStats().values(4) -
                changingStatsHandler.getDiff()[4]), ss[5], (hero.getStats().values(5) -
                changingStatsHandler.getDiff()[5]));
        Log.g("__________________________");
        changingStatsHandler = null;
    }

    @Override
    public void createInventoryHandler() {
        Log.g("_____________________");
        Log.g("Открыт инвентарь!");
        inventoryHandler = new InventoryHandler(this);
    }

    @Override
    public void removeInventoryHandler() {
        Log.g("Инвентарь закрыт");
        Log.g("_____________________");
        inventoryHandler = null;
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

    /**
     * Sets screen center at the Hero
     */
    public void resetViewport() {
        viewport.setCenter(hero.getLoc().x, hero.getLoc().y);
    }
}
