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
import java.util.Random;

public class Game implements StateHandlerOperator, IGame, IRoguelikeGame {

    /**
     * States in which game may be present. Current state affects GUI and the availability of various functions
     *
     * @since 0.0.1
     * @author Alpen Ditrix
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
    private final List<Map> maps;

    /**
     * The only one object that is controlled by player (user). That's his\her personal unique {@link com.avapir.roguelike.game.world.character.Mob}
     *
     * @see com.avapir.roguelike.game.world.character.Mob
     * @see com.avapir.roguelike.game.world.character.Hero
     */
    private final Hero hero;

    /**
     * Main window of the game. Here everything will be painted.
     */
    private final GameWindow gameWindow;

    /**
     * List of the mobs on that map
     */
    private final List<Mob> mobs;

    /**
     * Currently representing terraing
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

    private ChangingStatsHandler chs;
    private InventoryHandler     ih;
    private KeyboardHandler      kh;

    public Game(final String title) {
        Mob.game = this;
        gameWindow = new GameWindow(title, this);
        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        hero = new Hero("Hero", this);
        maps = new ArrayList<>();
        mobs = new ArrayList<>();


    }

    private static boolean isActuallyMoved(final Point dp) {
        if (!((dp.x == 1 || dp.x == 0 || dp.x == -1) && (dp.y == 1 || dp.y == 0 || dp.y == -1))) {
            throw new IllegalArgumentException("Wrong step");
        } else {
            return !(dp.x == 0 && dp.y == 0);
        }
    }

    private static void loadFonts() {
        final Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics();
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 8));
        g2.drawString("asd", 0, 0);
    }

    @Override
    public void init() {
        Log.getInstance().connect(this);
        System.out.println("Used Toolkit: " + System.getProperty("awt.toolkit"));
    }

    @Override
    public void start() {
        state = GameState.MOVE;
        // TODO потом надо поставить подходящий конструктор для карты
        final int firstMap = 0;
        maps.add(firstMap, new Map(this, 40, 40));
        switchToMap(firstMap);
        turnCounter = 0;
    }

    @Override
    public void done() {
        loadFonts();
        gameWindow.setVisible(true);
    }

    private void placeMobsAndItems(final int scaler) {
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

    private void switchToMap(final int index) {
        if (maps.get(index) == null) {
            currentMap = new Map(this, 40, 40);
            maps.add(index, currentMap);
        } else {
            currentMap = maps.get(index);
        }
        viewport = new Viewport(currentMap.putCharacter(hero), this);
        placeMobsAndItems(index);

        //fixme remove later
        Random r = new Random();
        int xx = currentMap.getWidth();
        int yy = currentMap.getHeight();
        EOT(new Point(0, 0));
    }

    private void move(final Point p) {
        if (isActuallyMoved(p)) {
            Log.g("Перешел в [%s, %s]", hero.getLoc().x, hero.getLoc().y);
            viewport.move(p);
        }
    }

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
        return chs;
    }

    @Override
    public InventoryHandler getInventoryHandler() {
        return ih;
    }

    @Override
    public KeyboardHandler getKeyboardHandler() {
        return kh;
    }

    @Override
    public void setKeyboardHandler(final KeyboardHandler keyboardHandler) {
        kh = keyboardHandler;
    }

    @Override
    public void createStatsHandler() {
        Log.g("_____________________");
        Log.g("Изменение характеристик:");
        Log.g("Свободных хар-к: %s", hero.getStats().getFree());
        chs = new ChangingStatsHandler(this);
    }

    @Override
    public void removeStatsHandler() {
        chs.flush();
        Log.g("Характеристики увеличились на:");
        final String[] ss = PrimaryStats.STATS_STRINGS;
        Log.g("%s:%s;              %s:%s", ss[0], (hero.getStats().values(0) - chs.getDiff()[0]), ss[1], (
                hero.getStats().values(1) - chs.getDiff()[1]));
        Log.g("%s:%s;              %s:%s", ss[2], (hero.getStats().values(2) - chs.getDiff()[2]), ss[3], (
                hero.getStats().values(5) - chs.getDiff()[5]));
        Log.g("%s:%s;              %s:%s", ss[4], (hero.getStats().values(4) - chs.getDiff()[4]), ss[5], (
                hero.getStats().values(5) - chs.getDiff()[5]));
        Log.g("__________________________");
        chs = null;
    }

    @Override
    public void createInventoryHandler() {
        Log.g("_____________________");
        Log.g("Открыт инвентарь!");
        ih = new InventoryHandler(this);
    }

    @Override
    public void removeInventoryHandler() {
        Log.g("Инвентарь закрыт");
        Log.g("_____________________");
        ih = null;
    }

    public static void zoomIn() {
//        zoomFactor += 1;
        Tile.SIZE_px += 1;
    }

    public static void zoomOut() {
//        zoomFactor -= 1;
        Tile.SIZE_px -= 1;
    }

    public void resetViewport() {
        viewport.setCenter(hero.getLoc().x, hero.getLoc().y);
    }
}
