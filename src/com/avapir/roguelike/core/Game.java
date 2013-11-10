package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.AbstractGamePanel;
import com.avapir.roguelike.core.gui.GameWindow;
import com.avapir.roguelike.core.statehandlers.ChangingStatsHandler;
import com.avapir.roguelike.core.statehandlers.InventoryHandler;
import com.avapir.roguelike.game.Map;
import com.avapir.roguelike.locatable.Hero;
import com.avapir.roguelike.locatable.Hero.PrimaryStats;
import com.avapir.roguelike.locatable.Mob;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game implements StateHandlerOperator, IGame, IRoguelikeGame {

    public static enum GameState {
        MOVE, INVENTORY, CHANGE_STATS, GAME_OVER, DISTANCE_ATTACK, VIEW
    }

    private final Log                  gameLog;
    private final List<Map>            maps;
    private final Hero                 hero;
    private final GameWindow           gameWindow;
    private final List<Mob>            mobs;
    private       Map                  currentMap;
    private       int                  currentX;
    private       int                  currentY;
    private       int                  turnCounter;
    private       GameState            state;
    private       ChangingStatsHandler chs;
    private       InventoryHandler     ih;
    private       KeyboardHandler      kh;

    public Game(final String title) {
        Mob.game = this;
        gameWindow = new GameWindow(title, this);
        gameWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        hero = new Hero("Hero", this);
        gameLog = new Log();
        maps = new ArrayList<>();
        mobs = new ArrayList<>();


    }

    public class Log extends LinkedList<String> {

        private static final long serialVersionUID = 1L;
        private int oneTurnCounter;

        @Override
        public boolean add(final String s) {
            super.add("[" + oneTurnCounter + ":" + (turnCounter) + "] " + s);
            oneTurnCounter++;
            if (size() > 15 && oneTurnCounter <= 15) {
                poll();
            }
            return true;// so as super.add()
        }

        public void refresh() {
            oneTurnCounter = 0;
            while (size() > 15) {
                poll();
            }
        }

    }

    private static boolean isMoved(final Point dp) {
        if (!((dp.x == 1 || dp.x == 0 || dp.x == -1) && (dp.y == 1 || dp.y == 0 || dp.y == -1))) {
            throw new IllegalArgumentException("Wrong step");
        } else {
            return !(dp.x == 0 && dp.y == 0);
        }
    }

    private static void loadFonts() {
        final Graphics2D g2 = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics();
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        g2.drawString("asd", 0, 0);
    }

    @Override
    public void init() {}

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

    public void log(final String s) {
        gameLog.add(s);
    }

    private void placeMobsAndItems(final int scaler) {
        int i = 320;
        for (int x = 0; x < currentMap.getWidth(); x++) {
            for (int y = 0; y < currentMap.getHeight(); y++) {
                if (i > 0) {
                    mobs.add(Mob.MobSet.getSlime());
                    currentMap.putCharacter(mobs.get(mobs.size() - 1), x, y);
                    i--;
                }
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
        setScreenCenterAt(currentMap.putCharacter(hero));
        placeMobsAndItems(index);

        EOT(new Point(0, 0));
    }

    private void move(final Point p) {
        if (isMoved(p)) {
            log("Перешел в [" + hero.getLoc().x + ", " + hero.getLoc().y + "]");
        }
        currentX += p.x;
        currentY += p.y;
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
        return currentX;
    }

    @Override
    public int getCurrentY() {
        return currentY;
    }

    @Override
    public Mob removeMob(final Mob m) {
        mobs.remove(m);
        return m;
    }

    @Override
    public void setScreenCenterAt(final Point p) {
        currentX = p.x - AbstractGamePanel.getWidthInTiles() / 2;
        currentY = p.y - AbstractGamePanel.getHeightInTiles() / 2;
        repaint();
    }

    @Override
    public void EOT(final Point mapMove) {
        move(mapMove);
        // TODO SET GAME.BUSY
        currentMap.computeFOV(hero.getLoc(), Hero.StatsFormulas.getFOVR(hero));
        doUnlimitedAiWorks();

        turnCounter++;
        gameLog.refresh();
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
        log("_____________________");
        log("Изменение характеристик:");
        log("Свободных хар-к: " + hero.getStats().getFree());
        chs = new ChangingStatsHandler(this);
    }

    @Override
    public void removeStatsHandler() {
        chs.flush();
        log("Характеристики увеличились на:");
        final String[] ss = PrimaryStats.STATS_STRINGS;
        log(ss[0] + ":" + (hero.getStats().values(0) - chs.getDiff()[0]) + ";              " + ss[1] + ":" +
                    (hero.getStats().values(1) - chs.getDiff()[1]));
        log(ss[2] + ":" + (hero.getStats().values(2) - chs.getDiff()[2]) + ";              " + ss[3] + ":" +
                    (hero.getStats().values(3) - chs.getDiff()[3]));
        log(ss[4] + ":" + (hero.getStats().values(4) - chs.getDiff()[4]) + ";             " + ss[5] + ":" +
                    (hero.getStats().values(5) - chs.getDiff()[5]));
        log("__________________________");
        chs = null;
    }

    @Override
    public void createInventoryHandler() {
        log("_____________________");
        log("Открыт инвентарь!");
        ih = new InventoryHandler(this);
    }

    @Override
    public void removeInventoryHandler() {
        log("Инвентарь закрыт");
        log("_____________________");
        ih = null;
    }

    public Log getLog() {
        return gameLog;
    }
}
