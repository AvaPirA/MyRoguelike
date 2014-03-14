package com.avapir.roguelike.core;

import com.avapir.roguelike.core.gui.AbstractGamePanel;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

public class Log implements Serializable, Paintable {
    private static final long serialVersionUID = 1L;

    private static Log instance = new Log();

    public static Log getInstance() {
        return instance;
    }

    private Log() {
    }

    /**
     * How much records will remain after end of turn
     */
    private static int REMAIN_RECORDS = 15;

    /**
     * {@link java.util.Formatter}-string for logging in {@link #g(String)}
     */
    private static final String FMT_GAME = "[%d:%d] %s";

    /**
     * {@link java.util.Formatter}-string for logging in {@link #g(String)}
     */
    private static final String FMT_GAME_FORMATTED = "[%d:%d] ";

    /**
     * Game instance for logging
     */
    private transient Game game;

    /**
     * How many records were made ​​from the beginning of turn
     */
    private transient int perTurn;

    /**
     * Storage of records
     */
    private LinkedList<String> loggedList;

    /**
     * @return is logger already connected to some instance of {@link com.avapir.roguelike.core.Game}
     */
    private boolean isConnected() {
        return game != null;
    }

    /**
     * Connects logger to instance of {@link com.avapir.roguelike.core.Game}.
     *
     * @param game instance
     *
     * @throws java.lang.IllegalStateException if logger already connected
     */
    public void connect(Game game) {
        if (isConnected()) {
            throw new IllegalStateException("Logger already connected to another game instance");
        }
        this.game = game;
        loggedList = new LinkedList<>();
    }

    /**
     * Disconnects logger from {@link com.avapir.roguelike.core.Game} instance.
     *
     * @return {@coed true} if succeed. {@code false} if logger was not connected yet
     */
    public boolean disconnect() {
        if (isConnected()) {
            game = null;
            return true;
        } else {
            return false;
        }
    }

    public static void g(final String s) {
        Log.getInstance().game(s);
    }

    /**
     * Sends default record for game
     *
     * @param s user data
     */
    public void game(final String s) {
        checkConnectivity();
        String formatted = String.format(FMT_GAME, perTurn, getTurnAndCheck(), s);
        loggedList.add(formatted);
        System.out.println(formatted);
        removePreviousTurnRecord();
    }

    /**
     * throws IllegalStateException if logger is not connected
     */
    private void checkConnectivity() {
        if (!isConnected()) {
            throw new IllegalStateException("Logger must be connected before making records");
        }
    }

    public static void g(final String s, final Object... params){
        Log.getInstance().game(s, params);
    }

    /**
     * Sends default record about game
     *
     * @param s formatting data string
     * @param s params data to insert
     */
    public void game(final String s, final Object... params) {
        game(String.format(s, params));
    }

    /**
     * Increments amount of added in that turn records and removes records from another turns if log is full
     */
    private void removePreviousTurnRecord() {
        perTurn++;
        if (loggedList.size() > REMAIN_RECORDS && perTurn <= REMAIN_RECORDS) {
            loggedList.poll();
        }
    }

    /**
     * Stores number of turn of last send/received record
     */
    private int turn = -1;

    /**
     * Retrieves number of current game turn and invokes {@link #refresh()} if it's not equal to {@link Log#turn}
     *
     * @return {@link com.avapir.roguelike.core.Game#getTurnCounter()}
     */
    private int getTurnAndCheck() {
        int currentTurn = game.getTurnCounter();
        if (turn != currentTurn) {
            refresh();
            turn = currentTurn;
        }
        return currentTurn;
    }

    /**
     * Removes extra records after end of turn.
     */
    private void refresh() {
        perTurn = 0;
        int extra = loggedList.size() - REMAIN_RECORDS;
        for (int i = 0; i < extra; i++) {
            loggedList.poll();
        }
    }

    /**
     * Sets new value to {@link Log#REMAIN_RECORDS}, which must be more than zero
     *
     * @param count new value
     */
    public void resetRemainRecordsAmount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Amount of records must be greater than zero");
        }
        REMAIN_RECORDS = count;
    }

    /**
     * @return the number of elements in the log
     */
    public int getSize() {
        return loggedList.size();
    }

    /**
     * @param i index of element to return
     *
     * @return the element at the specified position
     */
    public String get(int i) {
        return loggedList.get(i);
    }

    static final Font logFont = new Font("Times New Roman", Font.PLAIN, 15);

    @Override
    public void paint(AbstractGamePanel panel, Graphics2D g2, int x, int y) {
        final Point offset = new Point(x, y);
        g2.setFont(logFont);
        g2.setColor(Color.white);
        for (int i = 0; i < getSize(); i++) {
            g2.drawString(get(i), offset.x, offset.y + i * logFont.getSize() + 3);
        }
    }
}
